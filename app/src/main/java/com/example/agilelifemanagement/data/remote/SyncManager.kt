package com.example.agilelifemanagement.data.remote

import android.util.Log
import com.example.agilelifemanagement.data.local.dao.SyncStatusDao
import com.example.agilelifemanagement.data.local.entity.PendingOperation
import com.example.agilelifemanagement.data.local.entity.SyncStatus
import com.example.agilelifemanagement.data.remote.api.GoalApiService
import com.example.agilelifemanagement.data.remote.api.SprintApiService
import com.example.agilelifemanagement.data.remote.api.TaskApiService
import com.example.agilelifemanagement.data.remote.api.UserApiService
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
    private val networkMonitor: NetworkMonitor,
    private val supabaseManager: SupabaseManager
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val maxRetryCount = 5
    private val retryDelayMillis = 5000L // 5 seconds
    
    init {
        // Monitor network connectivity
        scope.launch {
            networkMonitor.isOnlineFlow.collectLatest { isOnline ->
                if (isOnline) {
                    Log.d(TAG, "Network is available, starting sync")
                    syncPendingChanges()
                } else {
                    Log.d(TAG, "Network is unavailable, sync paused")
                }
            }
        }
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
    suspend fun getCurrentUserId(): String? =
        supabaseManager.getCurrentUserId().firstOrNull()
    
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
    suspend fun scheduleSync(entityId: String, entityType: String, operation: PendingOperation) {
        syncStatusDao.insertSyncStatus(
            com.example.agilelifemanagement.data.local.entity.SyncStatusEntity(
                entityId = entityId,
                entityType = entityType,
                syncStatus = SyncStatus.PENDING,
                lastSyncAttempt = 0,
                pendingOperation = operation
            )
        )
        
        // Try to sync immediately if online
        if (networkMonitor.isOnline()) {
            scope.launch {
                syncPendingChanges()
            }
        }
    }
    
    companion object {
        private const val TAG = "SyncManager"
    }
}
