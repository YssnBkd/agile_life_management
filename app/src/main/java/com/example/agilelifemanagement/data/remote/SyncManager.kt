package com.example.agilelifemanagement.data.remote

import android.util.Log
import com.example.agilelifemanagement.data.local.dao.GoalDao
import com.example.agilelifemanagement.data.local.dao.SprintDao
import com.example.agilelifemanagement.data.local.dao.SyncStatusDao
import com.example.agilelifemanagement.data.local.dao.TaskDao
import com.example.agilelifemanagement.data.local.dao.UserDao
import com.example.agilelifemanagement.data.local.entity.PendingOperation
import com.example.agilelifemanagement.data.local.entity.SyncStatus
import com.example.agilelifemanagement.data.mappers.toEntity
import com.example.agilelifemanagement.data.remote.api.GoalApiService
import com.example.agilelifemanagement.data.remote.api.SprintApiService
import com.example.agilelifemanagement.data.remote.api.TaskApiService
import com.example.agilelifemanagement.data.remote.api.UserApiService
import com.example.agilelifemanagement.data.remote.dto.GoalDto
import com.example.agilelifemanagement.data.remote.dto.SprintDto
import com.example.agilelifemanagement.data.remote.dto.TaskDto
import com.example.agilelifemanagement.data.remote.dto.UserDto
import com.example.agilelifemanagement.domain.model.Result
import com.example.agilelifemanagement.util.NetworkMonitor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.decodeFromJsonElement
import io.github.jan.supabase.realtime.RealtimeChannel
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manager for synchronizing data between local database and Supabase.
 * Implements offline-first functionality with automatic synchronization.
 */
@Singleton
class SyncManager @Inject constructor(
    private val userApiService: UserApiService,
    private val sprintApiService: SprintApiService,
    private val goalApiService: GoalApiService,
    private val taskApiService: TaskApiService,
    private val syncStatusDao: SyncStatusDao,
    private val userDao: UserDao,
    private val sprintDao: SprintDao,
    private val goalDao: GoalDao,
    private val taskDao: TaskDao,
    private val networkMonitor: NetworkMonitor,
    private val supabaseManager: SupabaseManager,
    private val json: Json
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val maxRetryCount = 5
    private val retryDelayMillis = 5000L // 5 seconds

    // Track active Realtime subscriptions
    private val activeSubscriptions = mutableMapOf<String, RealtimeChannel.Status>()

    init {
        // Monitor network connectivity
        scope.launch {
            networkMonitor.isOnlineFlow.collectLatest { isOnline ->
                if (isOnline) {
                    Log.d(TAG, "Network is available, starting sync")
                    syncPendingChanges()
                    setupRealtimeSubscriptions()
                } else {
                    Log.d(TAG, "Network is unavailable, sync paused")
                    // Clean up subscriptions when offline
                    cleanupRealtimeSubscriptions()
                }
            }
        }

        // Monitor auth state
        scope.launch {
            supabaseManager.authState.collectLatest { authState ->
                when (authState) {
                    is AuthState.Authenticated -> {
                        Log.d(TAG, "User authenticated, setting up realtime subscriptions")
                        setupRealtimeSubscriptions()
                    }
                    is AuthState.NotAuthenticated -> {
                        Log.d(TAG, "User not authenticated, cleaning up subscriptions")
                        cleanupRealtimeSubscriptions()
                    }
                    else -> {
                        // Do nothing for other states
                    }
                }
            }
        }
    }

    /**
     * Set up Realtime subscriptions for all relevant tables.
     */
    private suspend fun setupRealtimeSubscriptions() {
        val userId = getCurrentUserId() ?: return

        // Subscribe to main tables
        subscribeToTable("agile_life.goals", userId)
        subscribeToTable("agile_life.tasks", userId)
        subscribeToTable("agile_life.sprints", userId)
        subscribeToTable("agile_life.tags", userId)

        // Potentially subscribe to cross-reference tables
        subscribeToTable("agile_life.task_sprint_cross_refs", userId)
        subscribeToTable("agile_life.task_goal_cross_refs", userId)
        subscribeToTable("agile_life.goal_sprint_cross_refs", userId)
    }

    /**
     * Subscribe to a specific table for real-time updates.
     */
    private suspend fun subscribeToTable(table: String, userId: String) {
        // Extract schema and table name
        val parts = table.split(".")
        val schema = parts[0]
        val tableName = parts[1]

        // Filter by user_id for user-specific tables
        val filter = if (tableName != "task_sprint_cross_refs" &&
            tableName != "task_goal_cross_refs" &&
            tableName != "goal_sprint_cross_refs") {
            "user_id=eq.$userId"
        } else null

        // Subscribe and collect status
        activeSubscriptions[table] = RealtimeChannel.Status.SUBSCRIBING

        val statusFlow = supabaseManager.subscribeToTable(
            schema = schema,
            table = tableName,
            filter = filter,
            onInsert = { record ->
                scope.launch {
                    handleInsert(tableName, record)
                }
            },
            onUpdate = { old, new ->
                scope.launch {
                    handleUpdate(tableName, old, new)
                }
            },
            onDelete = { old ->
                scope.launch {
                    handleDelete(tableName, old)
                }
            }
        )

        // Collect subscription status
        scope.launch {
            statusFlow.collectLatest { status ->
                activeSubscriptions[table] = status
                if (status == RealtimeChannel.Status.UNSUBSCRIBED) {
                    Log.e(TAG, "Subscription error for $table: channel unsubscribed")
                    // Attempt to reconnect after delay
                    delay(retryDelayMillis)
                    subscribeToTable(table, userId)
                }
            }
        }
    }

    /**
     * Handle an INSERT event from Supabase Realtime.
     */
    private suspend fun handleInsert(tableName: String, record: JsonElement) {
        try {
            // Convert the record to the appropriate DTO and save locally
            when (tableName) {
                "goals" -> {
                    val goalDto = json.decodeFromJsonElement(GoalDto.serializer(), record)
                    val goalEntity = goalDto.toEntity()
                    goalDao.insert(goalEntity)
                    syncStatusDao.markAsSynced(goalEntity.id)
                    Log.d(TAG, "Inserted goal from Realtime: ${goalEntity.id}")
                }
                "tasks" -> {
                    val taskDto = json.decodeFromJsonElement(TaskDto.serializer(), record)
                    val taskEntity = taskDto.toEntity()
                    taskDao.insert(taskEntity)
                    syncStatusDao.markAsSynced(taskEntity.id)
                    Log.d(TAG, "Inserted task from Realtime: ${taskEntity.id}")
                }
                "sprints" -> {
                    val sprintDto = json.decodeFromJsonElement(SprintDto.serializer(), record)
                    val sprintEntity = sprintDto.toEntity()
                    sprintDao.insertSprint(sprintEntity)
                    syncStatusDao.markAsSynced(sprintEntity.id)
                    Log.d(TAG, "Inserted sprint from Realtime: ${sprintEntity.id}")
                }
                "users" -> {
                    val userDto = json.decodeFromJsonElement(UserDto.serializer(), record)
                    val userEntity = userDto.toEntity()
                    userDao.upsert(userEntity) // Assuming UserDao has upsert method as shown earlier
                    syncStatusDao.markAsSynced(userEntity.id)
                    Log.d(TAG, "Inserted user from Realtime: ${userEntity.id}")
                }
                // Handle other tables as needed
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error handling INSERT for $tableName: ${e.message}", e)
        }
    }

    /**
     * Handle an UPDATE event from Supabase Realtime.
     */
    private suspend fun handleUpdate(tableName: String, old: JsonElement, new: JsonElement) {
        try {
            // Convert the record to the appropriate DTO and update locally
            when (tableName) {
                "goals" -> {
                    val goalDto = json.decodeFromJsonElement(GoalDto.serializer(), new)
                    val goalEntity = goalDto.toEntity()

                    // Check for conflict
                    val existingGoal = goalDao.getGoalById(goalEntity.id).first()
                    if (existingGoal != null && existingGoal.updatedAt > goalEntity.updatedAt) {
                        // Using updatedAt property for timestamp comparison
                        // Local version is newer, don't overwrite
                        Log.d(TAG, "Skipping Realtime update for goal ${goalEntity.id} - local version is newer")
                        return
                    }

                    goalDao.updateGoal(goalEntity)
                    syncStatusDao.markAsSynced(goalEntity.id)
                    Log.d(TAG, "Updated goal from Realtime: ${goalEntity.id}")
                }
                "tasks" -> {
                    val taskDto = json.decodeFromJsonElement(TaskDto.serializer(), new)
                    val taskEntity = taskDto.toEntity()

                    // Check for conflict
                    val existingTask = taskDao.getTaskById(taskEntity.id)
                    if (existingTask != null && existingTask.updatedAt > taskEntity.updatedAt) {
                        // Using updatedAt property for timestamp comparison
                        // Local version is newer, don't overwrite
                        Log.d(TAG, "Skipping Realtime update for task ${taskEntity.id} - local version is newer")
                        return
                    }

                    taskDao.update(taskEntity)
                    syncStatusDao.markAsSynced(taskEntity.id)
                    Log.d(TAG, "Updated task from Realtime: ${taskEntity.id}")
                }
                "sprints" -> {
                    val sprintDto = json.decodeFromJsonElement(SprintDto.serializer(), new)
                    val sprintEntity = sprintDto.toEntity()

                    // Check for conflict
                    val existingSprint = sprintDao.getSprintById(sprintEntity.id).first()
                    if (existingSprint != null && existingSprint.updatedAt > sprintEntity.updatedAt) {
                        // Using updatedAt property for timestamp comparison
                        // Local version is newer, don't overwrite
                        Log.d(TAG, "Skipping Realtime update for sprint ${sprintEntity.id} - local version is newer")
                        return
                    }

                    sprintDao.updateSprint(sprintEntity)
                    syncStatusDao.markAsSynced(sprintEntity.id)
                    Log.d(TAG, "Updated sprint from Realtime: ${sprintEntity.id}")
                }
                // Handle other tables as needed
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error handling UPDATE for $tableName: ${e.message}", e)
        }
    }

    /**
     * Handle a DELETE event from Supabase Realtime.
     */
    private suspend fun handleDelete(tableName: String, old: JsonElement) {
        try {
            // Delete the entity locally
            when (tableName) {
                "goals" -> {
                    val goalDto = json.decodeFromJsonElement(GoalDto.serializer(), old)
                    goalDao.deleteGoalById(goalDto.id)
                    syncStatusDao.deleteSyncStatus(goalDto.id)
                    Log.d(TAG, "Deleted goal from Realtime: ${goalDto.id}")
                }
                "tasks" -> {
                    val taskDto = json.decodeFromJsonElement(TaskDto.serializer(), old)
                    taskDao.deleteTaskById("userId", taskDto.id)
                    syncStatusDao.deleteSyncStatus(taskDto.id)
                    Log.d(TAG, "Deleted task from Realtime: ${taskDto.id}")
                }
                "sprints" -> {
                    val sprintDto = json.decodeFromJsonElement(SprintDto.serializer(), old)
                    sprintDao.deleteSprintById(sprintDto.id)
                    syncStatusDao.deleteSyncStatus(sprintDto.id)
                    Log.d(TAG, "Deleted sprint from Realtime: ${sprintDto.id}")
                }
                // Handle other tables as needed
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error handling DELETE for $tableName: ${e.message}", e)
        }
    }

    /**
     * Clean up all Realtime subscriptions.
     */
    private suspend fun cleanupRealtimeSubscriptions() {
        supabaseManager.closeAllSubscriptions() // calls getClient().realtime.closeAllChannels()
        activeSubscriptions.clear()
    }

    /**
     * Synchronize all pending changes with Supabase.
     */
    suspend fun syncPendingChanges() = withContext(Dispatchers.IO) {
        if (!networkMonitor.isOnline()) {
            Log.d(TAG, "Cannot sync: Network is unavailable")
            return@withContext
        }

        val userId = getCurrentUserId() ?: return@withContext

        // Sync in order of dependencies: users -> sprints/goals -> tasks -> cross-refs
        syncEntities("user", userId)
        syncEntities("sprint", userId)
        syncEntities("goal", userId)
        syncEntities("task", userId)
        syncEntities("task_sprint_cross_ref", userId)
        syncEntities("task_goal_cross_ref", userId)
        syncEntities("goal_sprint_cross_ref", userId)
    }

    /**
     * Get the current user's ID from SupabaseManager.
     */
    suspend fun getCurrentUserId(): String? {
        return try {
            supabaseManager.getCurrentUserId().first()
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Synchronize entities of a specific type.
     */
    private suspend fun syncEntities(entityType: String, userId: String) {
        try {
            // Get all pending entities of this type
            val pendingEntities = syncStatusDao.getSyncStatusByStatusAndType(SyncStatus.PENDING, entityType)
            Log.d(TAG, "Found ${pendingEntities.size} pending $entityType entities to sync")

            for (pendingEntity in pendingEntities) {
                if (pendingEntity.retryCount > maxRetryCount) {
                    Log.w(TAG, "Skipping sync for $entityType:${pendingEntity.entityId} - max retries exceeded")
                    continue
                }

                when (pendingEntity.pendingOperation) {
                    PendingOperation.CREATE, PendingOperation.UPDATE -> {
                        val result = when (entityType) {
                            "user" -> syncUser(pendingEntity.entityId)
                            "sprint" -> syncSprint(pendingEntity.entityId)
                            "goal" -> syncGoal(pendingEntity.entityId)
                            "task" -> syncTask(pendingEntity.entityId)
                            // Add other entity types as needed
                            else -> Result.Error("Unknown entity type: $entityType")
                        }

                        handleSyncResult(result, pendingEntity.entityId)
                    }
                    PendingOperation.DELETE -> {
                        val result = when (entityType) {
                            "user" -> userApiService.deleteUser(pendingEntity.entityId)
                            "sprint" -> sprintApiService.deleteSprint(pendingEntity.entityId)
                            "goal" -> goalApiService.deleteGoal(pendingEntity.entityId)
                            "task" -> taskApiService.deleteTask(pendingEntity.entityId)
                            // Add other entity types as needed
                            else -> Result.Error("Unknown entity type: $entityType")
                        }

                        handleSyncResult(result, pendingEntity.entityId)
                    }
                    PendingOperation.NONE -> {
                        // Nothing to do
                        syncStatusDao.markAsSynced(pendingEntity.entityId)
                    }
                }

                // Add a small delay between operations to avoid rate limiting
                delay(100)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error syncing $entityType entities: ${e.message}", e)
        }
    }

    /**
     * Synchronize a user with Supabase.
     */
    private suspend fun syncUser(userId: String): Result<Unit> {
        // Implementation depends on your UserRepository
        // This is a placeholder
        return Result.Success(Unit)
    }

    /**
     * Synchronize a sprint with Supabase.
     */
    private suspend fun syncSprint(sprintId: String): Result<Unit> {
        // Implementation depends on your SprintRepository
        // This is a placeholder
        return Result.Success(Unit)
    }

    /**
     * Synchronize a goal with Supabase.
     */
    private suspend fun syncGoal(goalId: String): Result<Unit> {
        // Implementation depends on your GoalRepository
        // This is a placeholder
        return Result.Success(Unit)
    }

    /**
     * Synchronize a task with Supabase.
     */
    private suspend fun syncTask(taskId: String): Result<Unit> {
        // Implementation depends on your TaskRepository
        // This is a placeholder
        return Result.Success(Unit)
    }

    /**
     * Handle the result of a synchronization operation.
     */
    private suspend fun handleSyncResult(result: Result<*>, entityId: String) {
        when (result) {
            is Result.Success -> {
                Log.d(TAG, "Successfully synced entity: $entityId")
                syncStatusDao.markAsSynced(entityId)
            }
            is Result.Error -> {
                Log.e(TAG, "Failed to sync entity: $entityId - ${result.message}")
                syncStatusDao.markAsFailed(entityId, errorMessage = result.message)
            }
            is Result.Loading -> {
                // Should not happen in this context
            }
        }
    }

    /**
     * Mark an entity as synced in the sync status database.
     */
    suspend fun markSynced(entityId: String, entityType: String) {
        syncStatusDao.markAsSynced(entityId)
    }

    /**
     * Schedule an entity for synchronization.
     */
    suspend fun scheduleSyncOperation(
        entityId: String,
        entityType: String,
        operation: PendingOperation = PendingOperation.UPDATE
    ) {
        // For compatibility with existing code that might call this method
        syncStatusDao.insertSyncStatus(
            com.example.agilelifemanagement.data.local.entity.SyncStatusEntity(
                entityId = entityId,
                entityType = entityType,
                syncStatus = SyncStatus.PENDING,
                lastSyncAttempt = 0,
                pendingOperation = operation
            )
        )

        // Try immediate sync if online
        if (networkMonitor.isOnline()) {
            scope.launch {
                syncEntities(entityType, getCurrentUserId() ?: return@launch)
            }
        }
    }

    /**
     * Get pending operation for an entity.
     * Returns the pending operation or null if no operation is pending.
     */
    suspend fun getPendingOperation(entityId: String, entityType: String): PendingOperation? {
        val syncStatus = syncStatusDao.getSyncStatusById(entityId)
        return if (syncStatus != null && syncStatus.syncStatus == SyncStatus.PENDING) {
            syncStatus.pendingOperation
        } else {
            null
        }
    }

    companion object {
        private const val TAG = "SyncManager"
    }
}
