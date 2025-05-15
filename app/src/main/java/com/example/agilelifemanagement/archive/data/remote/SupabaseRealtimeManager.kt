package com.example.agilelifemanagement.data.remote

import android.util.Log
import com.example.agilelifemanagement.data.local.dao.GoalDao
import com.example.agilelifemanagement.data.local.dao.GoalSprintCrossRefDao
import com.example.agilelifemanagement.data.local.dao.SprintDao
import com.example.agilelifemanagement.data.local.dao.TaskDao
import com.example.agilelifemanagement.data.local.dao.TaskGoalCrossRefDao
import com.example.agilelifemanagement.data.local.dao.TaskSprintCrossRefDao
import com.example.agilelifemanagement.data.local.entity.GoalEntity
import com.example.agilelifemanagement.data.local.entity.PendingOperation
import com.example.agilelifemanagement.data.local.entity.SprintEntity
import com.example.agilelifemanagement.data.local.entity.TaskEntity
import com.example.agilelifemanagement.data.remote.dto.GoalDto
import com.example.agilelifemanagement.data.remote.dto.SprintDto
import com.example.agilelifemanagement.data.remote.dto.TaskDto
import io.github.jan.supabase.realtime.RealtimeChannel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages real-time Supabase subscriptions and synchronizes remote changes to the local database.
 */
@Singleton
class SupabaseRealtimeManager @Inject constructor(
    private val supabaseManager: SupabaseManager,
    private val taskDao: TaskDao,
    private val sprintDao: SprintDao,
    private val goalDao: GoalDao,
    private val taskSprintCrossRefDao: TaskSprintCrossRefDao,
    private val taskGoalCrossRefDao: TaskGoalCrossRefDao,
    private val goalSprintCrossRefDao: GoalSprintCrossRefDao,
    private val syncManager: SyncManager
) {
    private val TAG = "SupabaseRealtimeManager"
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    
    // Track active subscriptions by table name
    private val activeSubscriptions = mutableMapOf<String, Job>()
    
    // Sync Status
    private val _syncStatus = MutableStateFlow(SyncStatus.IDLE)
    val syncStatus: StateFlow<SyncStatus> = _syncStatus.asStateFlow()
    
    /**
     * Initialize real-time subscriptions for all relevant tables.
     * This should be called when the user logs in.
     */
    suspend fun initializeSubscriptions(userId: String) {
        Log.d(TAG, "Initializing real-time subscriptions for user: $userId")
        _syncStatus.value = SyncStatus.CONNECTING
        
        // Subscribe to tasks table with user_id filter
        subscribeToTasks(userId)
        
        // Subscribe to sprints table with user_id filter
        subscribeToSprints(userId)
        
        // Subscribe to goals table with user_id filter  
        subscribeToGoals(userId)
        
        // Track successful connection
        _syncStatus.value = SyncStatus.CONNECTED
    }
    
    /**
     * Unsubscribe from all real-time channels.
     * This should be called when the user logs out.
     */
    suspend fun unsubscribeAll() {
        Log.d(TAG, "Unsubscribing from all real-time channels")
        try {
            // Cancel all active subscription jobs
            activeSubscriptions.forEach { (_, job) -> job.cancel() }
            activeSubscriptions.clear()
            _syncStatus.value = SyncStatus.IDLE
        } catch (e: Exception) {
            Log.e(TAG, "Error unsubscribing from realtime channels: ${e.message}", e)
        }
    }
    
    /**
     * Subscribe to the tasks table for a specific user.
     */
    private suspend fun subscribeToTasks(userId: String) {
        val job = scope.launch {
            try {
                val statusFlow = supabaseManager.subscribeToTable(
                    schema = "agile_life",
                    table = "tasks",
                    filter = "user_id=eq.$userId",
                    onInsert = { record ->
                        handleTaskInsert(record, userId)
                    },
                    onUpdate = { oldRecord, newRecord ->
                        handleTaskUpdate(oldRecord, newRecord, userId)
                    },
                    onDelete = { oldRecord ->
                        handleTaskDelete(oldRecord, userId)
                    }
                )
                
                // Monitor subscription status
                statusFlow.collect { status ->
                    when (status) {
                        // Note: We need to handle the status differently based on the Supabase SDK version
                        // This should work with both string-based and enum-based status implementations
                        is String -> {
                            handleStringStatus(status, "tasks")
                        }
                        else -> {
                            // For other status types, we can just log
                            Log.d(TAG, "Task subscription status: $status")
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to subscribe to tasks table: ${e.message}", e)
                _syncStatus.value = SyncStatus.ERROR
            }
        }
        
        activeSubscriptions["tasks"] = job
    }
    
    /**
     * Subscribe to the sprints table for a specific user.
     */
    private suspend fun subscribeToSprints(userId: String) {
        val job = scope.launch {
            try {
                val statusFlow = supabaseManager.subscribeToTable(
                    schema = "agile_life",
                    table = "sprints",
                    filter = "user_id=eq.$userId",
                    onInsert = { record ->
                        handleSprintInsert(record, userId)
                    },
                    onUpdate = { oldRecord, newRecord ->
                        handleSprintUpdate(oldRecord, newRecord, userId)
                    },
                    onDelete = { oldRecord ->
                        handleSprintDelete(oldRecord, userId)
                    }
                )
                
                // Monitor subscription status
                statusFlow.collect { status ->
                    when (status) {
                        // Handle string-based status
                        is String -> {
                            handleStringStatus(status, "sprints")
                        }
                        else -> {
                            // For other status types, we can just log
                            Log.d(TAG, "Sprint subscription status: $status")
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to subscribe to sprints table: ${e.message}", e)
            }
        }
        
        activeSubscriptions["sprints"] = job
    }
    
    /**
     * Subscribe to the goals table for a specific user.
     */
    private suspend fun subscribeToGoals(userId: String) {
        val job = scope.launch {
            try {
                val statusFlow = supabaseManager.subscribeToTable(
                    schema = "agile_life",
                    table = "goals",
                    filter = "user_id=eq.$userId",
                    onInsert = { record ->
                        handleGoalInsert(record, userId)
                    },
                    onUpdate = { oldRecord, newRecord ->
                        handleGoalUpdate(oldRecord, newRecord, userId)
                    },
                    onDelete = { oldRecord ->
                        handleGoalDelete(oldRecord, userId)
                    }
                )
                
                // Monitor subscription status
                statusFlow.collect { status ->
                    when (status) {
                        // Handle string-based status
                        is String -> {
                            handleStringStatus(status, "goals")
                        }
                        else -> {
                            // For other status types, we can just log
                            Log.d(TAG, "Goal subscription status: $status")
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to subscribe to goals table: ${e.message}", e)
            }
        }
        
        activeSubscriptions["goals"] = job
    }
    
    /**
     * Handle string-based status messages (for compatibility with different Supabase SDK versions)
     */
    private fun handleStringStatus(status: String, tableName: String) {
        when (status.lowercase()) {
            "subscribed" -> {
                Log.d(TAG, "Successfully subscribed to $tableName table")
            }
            "unsubscribed" -> {
                Log.d(TAG, "Unsubscribed from $tableName table")
            }
            else -> {
                if (status.lowercase().contains("error")) {
                    Log.e(TAG, "Error in $tableName subscription: $status")
                    _syncStatus.value = SyncStatus.ERROR
                } else {
                    Log.d(TAG, "$tableName subscription status: $status")
                }
            }
        }
    }
    
    /**
     * Handle a task insert event from Supabase.
     */
    private fun handleTaskInsert(record: JsonElement, userId: String) {
        scope.launch {
            try {
                // Parse the JsonElement to a TaskDto
                val taskDto = Json.decodeFromJsonElement(TaskDto.serializer(), record)
                
                // Check if this task already exists locally (we might have created it)
                val existingTask = taskDao.getTaskById(taskDto.id)
                
                if (existingTask == null) {
                    // This is a new task from the server, insert it locally
                    Log.d(TAG, "Inserting new task from server: ${taskDto.id}")
                    
                    // Convert DTO to entity
                    val entity = taskDto.toEntity()
                    
                    // Insert into local database
                    taskDao.insert(entity)
                    
                    // Mark as synced in sync manager
                    syncManager.markSynced(taskDto.id, "task")
                } else {
                    // Task exists locally, check if we're waiting to sync it
                    val pendingOp = syncManager.getPendingOperation(taskDto.id, "task")
                    if (pendingOp == null) {
                        // No pending op, might be a conflict - update with server version
                        Log.d(TAG, "Updating existing task with server version: ${taskDto.id}")
                        taskDao.insert(taskDto.toEntity()) // Using insert with REPLACE strategy
                    }
                    // If there's a pending op, we're likely the ones who created this task,
                    // so we don't update our local copy
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error handling task insert: ${e.message}", e)
            }
        }
    }
    
    /**
     * Handle a task update event from Supabase.
     */
    private fun handleTaskUpdate(oldRecord: JsonElement, newRecord: JsonElement, userId: String) {
        scope.launch {
            try {
                // Parse the JsonElement to a TaskDto
                val taskDto = Json.decodeFromJsonElement(TaskDto.serializer(), newRecord)
                
                // Check if we have pending updates for this task
                val pendingOp = syncManager.getPendingOperation(taskDto.id, "task")
                if (pendingOp != null) {
                    // We have a pending update, don't overwrite our local changes
                    Log.d(TAG, "Ignoring server update for task with pending op: ${taskDto.id}")
                    return@launch
                }
                
                // Update the task in local database
                Log.d(TAG, "Updating task from server: ${taskDto.id}")
                val entity = taskDto.toEntity()
                taskDao.insert(entity) // Using insert with REPLACE strategy
                
                // Mark as synced
                syncManager.markSynced(taskDto.id, "task")
            } catch (e: Exception) {
                Log.e(TAG, "Error handling task update: ${e.message}", e)
            }
        }
    }
    
    /**
     * Handle a task delete event from Supabase.
     */
    private fun handleTaskDelete(oldRecord: JsonElement, userId: String) {
        scope.launch {
            try {
                // Parse the JsonElement to get the task ID
                val taskDto = Json.decodeFromJsonElement(TaskDto.serializer(), oldRecord)
                val taskId = taskDto.id
                
                // Check if we have pending operations for this task
                val pendingOp = syncManager.getPendingOperation(taskId, "task")
                if (pendingOp != null) {
                    // We have a pending operation, don't delete if it's not a delete op
                    if (pendingOp == PendingOperation.DELETE) {
                        // This is our delete being confirmed, remove from pending ops
                        syncManager.markSynced(taskId, "task")
                    } else {
                        Log.d(TAG, "Conflict: Server deleted task ${taskId} but we have a pending ${pendingOp}")
                        // In this case, the server's delete takes precedence
                        // Delete the task from local database
                        taskDao.deleteTaskById(userId, taskId)
                        // Clear pending ops
                        syncManager.markSynced(taskId, "task")
                    }
                    return@launch
                }
                
                // No pending op, delete from local database
                Log.d(TAG, "Deleting task from server: $taskId")
                taskDao.deleteTaskById(userId, taskId)
            } catch (e: Exception) {
                Log.e(TAG, "Error handling task delete: ${e.message}", e)
            }
        }
    }
    
    /**
     * Handle a sprint insert event from Supabase.
     */
    private fun handleSprintInsert(record: JsonElement, userId: String) {
        scope.launch {
            try {
                // Parse the JsonElement to a SprintDto
                val sprintDto = Json.decodeFromJsonElement(SprintDto.serializer(), record)
                
                // Check if this sprint already exists locally
                val existingSprint = sprintDao.getSprintById(sprintDto.id).firstOrNull()
                
                if (existingSprint == null) {
                    // This is a new sprint from the server, insert it locally
                    Log.d(TAG, "Inserting new sprint from server: ${sprintDto.id}")
                    
                    // Convert DTO to entity
                    val entity = sprintDto.toEntity()
                    
                    // Insert into local database
                    sprintDao.insertSprint(entity)
                    
                    // Mark as synced in sync manager
                    syncManager.markSynced(sprintDto.id, "sprint")
                } else {
                    // Sprint exists locally, check if we're waiting to sync it
                    val pendingOp = syncManager.getPendingOperation(sprintDto.id, "sprint")
                    if (pendingOp == null) {
                        // No pending op, might be a conflict - update with server version
                        Log.d(TAG, "Updating existing sprint with server version: ${sprintDto.id}")
                        sprintDao.updateSprint(sprintDto.toEntity())
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error handling sprint insert: ${e.message}", e)
            }
        }
    }
    
    /**
     * Handle a sprint update event from Supabase.
     */
    private fun handleSprintUpdate(oldRecord: JsonElement, newRecord: JsonElement, userId: String) {
        scope.launch {
            try {
                // Parse the JsonElement to a SprintDto
                val sprintDto = Json.decodeFromJsonElement(SprintDto.serializer(), newRecord)
                
                // Check if we have pending updates for this sprint
                val pendingOp = syncManager.getPendingOperation(sprintDto.id, "sprint")
                if (pendingOp != null) {
                    // We have a pending update, don't overwrite our local changes
                    Log.d(TAG, "Ignoring server update for sprint with pending op: ${sprintDto.id}")
                    return@launch
                }
                
                // Update the sprint in local database
                Log.d(TAG, "Updating sprint from server: ${sprintDto.id}")
                val entity = sprintDto.toEntity()
                sprintDao.updateSprint(entity)
                
                // Mark as synced
                syncManager.markSynced(sprintDto.id, "sprint")
            } catch (e: Exception) {
                Log.e(TAG, "Error handling sprint update: ${e.message}", e)
            }
        }
    }
    
    /**
     * Handle a sprint delete event from Supabase.
     */
    private fun handleSprintDelete(oldRecord: JsonElement, userId: String) {
        scope.launch {
            try {
                // Parse the JsonElement to get the sprint ID
                val sprintDto = Json.decodeFromJsonElement(SprintDto.serializer(), oldRecord)
                val sprintId = sprintDto.id
                
                // Check if we have pending operations for this sprint
                val pendingOp = syncManager.getPendingOperation(sprintId, "sprint")
                if (pendingOp != null) {
                    // We have a pending operation, don't delete if it's not a delete op
                    if (pendingOp == PendingOperation.DELETE) {
                        // This is our delete being confirmed, remove from pending ops
                        syncManager.markSynced(sprintId, "sprint")
                    } else {
                        Log.d(TAG, "Conflict: Server deleted sprint ${sprintId} but we have a pending ${pendingOp}")
                        // Delete the sprint from local database
                        sprintDao.deleteSprintById(sprintId)
                        // Clear pending ops
                        syncManager.markSynced(sprintId, "sprint")
                    }
                    return@launch
                }
                
                // No pending op, delete from local database
                Log.d(TAG, "Deleting sprint from server: $sprintId")
                sprintDao.deleteSprintById(sprintId)
            } catch (e: Exception) {
                Log.e(TAG, "Error handling sprint delete: ${e.message}", e)
            }
        }
    }
    
    /**
     * Handle a goal insert event from Supabase.
     */
    private fun handleGoalInsert(record: JsonElement, userId: String) {
        scope.launch {
            try {
                // Parse the JsonElement to a GoalDto
                val goalDto = Json.decodeFromJsonElement(GoalDto.serializer(), record)
                
                // Check if this goal already exists locally
                val existingGoal = goalDao.getGoalById(goalDto.id)
                
                if (existingGoal == null) {
                    // This is a new goal from the server, insert it locally
                    Log.d(TAG, "Inserting new goal from server: ${goalDto.id}")
                    
                    // Convert DTO to entity
                    val entity = goalDto.toEntity()
                    
                    // Insert into local database
                    goalDao.insert(entity)
                    
                    // Mark as synced in sync manager
                    syncManager.markSynced(goalDto.id, "goal")
                } else {
                    // Goal exists locally, check if we're waiting to sync it
                    val pendingOp = syncManager.getPendingOperation(goalDto.id, "goal")
                    if (pendingOp == null) {
                        // No pending op, might be a conflict - update with server version
                        Log.d(TAG, "Updating existing goal with server version: ${goalDto.id}")
                        goalDao.insert(goalDto.toEntity()) // Using insert with REPLACE strategy
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error handling goal insert: ${e.message}", e)
            }
        }
    }
    
    /**
     * Handle a goal update event from Supabase.
     */
    private fun handleGoalUpdate(oldRecord: JsonElement, newRecord: JsonElement, userId: String) {
        scope.launch {
            try {
                // Parse the JsonElement to a GoalDto
                val goalDto = Json.decodeFromJsonElement(GoalDto.serializer(), newRecord)
                
                // Check if we have pending updates for this goal
                val pendingOp = syncManager.getPendingOperation(goalDto.id, "goal")
                if (pendingOp != null) {
                    // We have a pending update, don't overwrite our local changes
                    Log.d(TAG, "Ignoring server update for goal with pending op: ${goalDto.id}")
                    return@launch
                }
                
                // Update the goal in local database
                Log.d(TAG, "Updating goal from server: ${goalDto.id}")
                val entity = goalDto.toEntity()
                goalDao.insert(entity) // Using insert with REPLACE strategy
                
                // Mark as synced
                syncManager.markSynced(goalDto.id, "goal")
            } catch (e: Exception) {
                Log.e(TAG, "Error handling goal update: ${e.message}", e)
            }
        }
    }
    
    /**
     * Handle a goal delete event from Supabase.
     */
    private fun handleGoalDelete(oldRecord: JsonElement, userId: String) {
        scope.launch {
            try {
                // Parse the JsonElement to get the goal ID
                val goalDto = Json.decodeFromJsonElement(GoalDto.serializer(), oldRecord)
                val goalId = goalDto.id
                
                // Check if we have pending operations for this goal
                val pendingOp = syncManager.getPendingOperation(goalId, "goal")
                if (pendingOp != null) {
                    // We have a pending operation, don't delete if it's not a delete op
                    if (pendingOp == PendingOperation.DELETE) {
                        // This is our delete being confirmed, remove from pending ops
                        syncManager.markSynced(goalId, "goal")
                    } else {
                        Log.d(TAG, "Conflict: Server deleted goal ${goalId} but we have a pending ${pendingOp}")
                        // Delete the goal from local database
                        goalDao.deleteGoalById(goalId)
                        // Clear pending ops
                        syncManager.markSynced(goalId, "goal")
                    }
                    return@launch
                }
                
                // No pending op, delete from local database
                Log.d(TAG, "Deleting goal from server: $goalId")
                goalDao.deleteGoalById(goalId)
            } catch (e: Exception) {
                Log.e(TAG, "Error handling goal delete: ${e.message}", e)
            }
        }
    }
    
    /**
     * Sync status enumeration.
     */
    enum class SyncStatus {
        IDLE,         // Not syncing
        CONNECTING,   // Establishing connections
        CONNECTED,    // Successfully connected
        SYNCING,      // Actively syncing data
        ERROR         // Error occurred
    }
}
