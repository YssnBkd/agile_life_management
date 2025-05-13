package com.example.agilelifemanagement.data.repository

import com.example.agilelifemanagement.data.local.dao.TaskDao
import com.example.agilelifemanagement.data.local.dao.TaskSprintCrossRefDao
import com.example.agilelifemanagement.data.local.dao.TaskGoalCrossRefDao
import com.example.agilelifemanagement.data.local.dao.TaskTagCrossRefDao
import com.example.agilelifemanagement.data.local.dao.TaskDependencyDao
import com.example.agilelifemanagement.data.local.entity.PendingOperation
import com.example.agilelifemanagement.data.local.entity.TaskEntity
import com.example.agilelifemanagement.data.local.entity.TaskSprintCrossRefEntity
import com.example.agilelifemanagement.data.local.entity.TaskGoalCrossRefEntity
import com.example.agilelifemanagement.data.local.entity.TaskTagCrossRefEntity
import com.example.agilelifemanagement.data.local.entity.TaskDependencyEntity
import com.example.agilelifemanagement.data.mappers.toTask
import com.example.agilelifemanagement.data.remote.SupabaseManager
import com.example.agilelifemanagement.data.remote.SupabaseRealtimeManager
import com.example.agilelifemanagement.data.remote.api.TaskApiService
import com.example.agilelifemanagement.data.remote.api.TaskSprintCrossRefApiService
import com.example.agilelifemanagement.data.remote.api.TaskGoalCrossRefApiService
import com.example.agilelifemanagement.data.remote.api.TaskTagCrossRefApiService
import com.example.agilelifemanagement.data.remote.api.TaskDependencyApiService
import com.example.agilelifemanagement.data.remote.SyncManager
import com.example.agilelifemanagement.domain.model.Task
import com.example.agilelifemanagement.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.first
import java.util.UUID
import javax.inject.Inject
import com.example.agilelifemanagement.util.NetworkMonitor

private const val TAG = "TaskRepositoryImpl"

/**
 * Implementation of [TaskRepository] that follows the offline-first strategy.
 */
@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class TaskRepositoryImpl @Inject constructor(
    private val taskDao: TaskDao,
    private val taskApiService: TaskApiService,
    private val syncManager: SyncManager,
    private val supabaseManager: SupabaseManager,
    private val realtimeManager: SupabaseRealtimeManager,
    private val taskSprintCrossRefDao: TaskSprintCrossRefDao,
    private val taskGoalCrossRefDao: TaskGoalCrossRefDao,
    private val taskTagCrossRefDao: TaskTagCrossRefDao,
    private val taskDependencyDao: TaskDependencyDao,
    private val taskSprintCrossRefApiService: TaskSprintCrossRefApiService,
    private val taskGoalCrossRefApiService: TaskGoalCrossRefApiService,
    private val taskTagCrossRefApiService: TaskTagCrossRefApiService,
    private val taskDependencyApiService: TaskDependencyApiService,
    private val networkMonitor: NetworkMonitor
) : TaskRepository {


    override fun getTasks(): Flow<List<Task>> =
        supabaseManager.getCurrentUserId().flatMapLatest { userId: String? ->
            if (userId == null) emptyFlow() else try {
                taskDao.getAllTasks(userId)
                    .map { list -> list.map { it.toTask() } }
            } catch (e: Exception) {
                android.util.Log.e(TAG, "Error getting all tasks for user: $userId", e)
                emptyFlow()
            }
        }

    override fun getTaskById(id: String): Flow<Task?> =
        kotlinx.coroutines.flow.flow {
            try {
                emit(taskDao.getTaskById(id)?.toTask())
            } catch (e: Exception) {
                android.util.Log.e(TAG, "Error getting task by id: $id", e)
                emit(null)
            }
        }

    override fun getTasksByStatus(status: Task.Status): Flow<List<Task>> =
        supabaseManager.getCurrentUserId().flatMapLatest { userId: String? ->
            if (userId == null) emptyFlow() else try {
                taskDao.getTasksByStatus(userId, status.ordinal)
                    .map { list -> list.map { it.toTask() } }
            } catch (e: Exception) {
                android.util.Log.e(TAG, "Error getting tasks by status for user: $userId", e)
                emptyFlow()
            }
        }

    override fun getTasksByPriority(priority: Task.Priority): Flow<List<Task>> =
        supabaseManager.getCurrentUserId().flatMapLatest { userId: String? ->
            if (userId == null) emptyFlow() else try {
                taskDao.getTasksByPriority(userId, priority.ordinal)
                    .map { list -> list.map { it.toTask() } }
            } catch (e: Exception) {
                android.util.Log.e(TAG, "Error getting tasks by priority for user: $userId", e)
                emptyFlow()
            }
        }

    override fun getTasksByDueDate(dueDate: java.time.LocalDate): Flow<List<Task>> =
        supabaseManager.getCurrentUserId().flatMapLatest { userId: String? ->
            if (userId == null) emptyFlow() else try {
                taskDao.getTasksByDueDate(userId, dueDate.toEpochDay())
                    .map { list -> list.map { it.toTask() } }
            } catch (e: Exception) {
                android.util.Log.e(TAG, "Error getting tasks by due date for user: $userId", e)
                emptyFlow()
            }
        }

    override fun getTasksBySprintId(sprintId: String): Flow<List<Task>> =
        taskSprintCrossRefDao.getTasksForSprint(sprintId).flatMapLatest { crossRefs ->
            val taskIds = crossRefs.map { it.taskId }
            if (taskIds.isEmpty()) emptyFlow()
            else taskDao.getTasksByIds(taskIds).map { list -> list.map { it.toTask() } }
        }

    override fun getTasksByGoalId(goalId: String): Flow<List<Task>> =
        taskGoalCrossRefDao.getTasksForGoal(goalId).flatMapLatest { crossRefs ->
            val taskIds = crossRefs.map { it.taskId }
            if (taskIds.isEmpty()) emptyFlow()
            else taskDao.getTasksByIds(taskIds).map { list -> list.map { it.toTask() } }
        }

    override fun getTasksByTag(tagId: String): Flow<List<Task>> =
        taskTagCrossRefDao.getTasksForTag(tagId).flatMapLatest { crossRefs ->
            val taskIds = crossRefs.map { it.taskId }
            if (taskIds.isEmpty()) emptyFlow()
            else taskDao.getTasksByIds(taskIds).map { list -> list.map { it.toTask() } }
        }

    override suspend fun insertTask(task: Task): com.example.agilelifemanagement.domain.model.Result<String> {
        return try {
            val userId = supabaseManager.getCurrentUserId().first() 
                ?: return com.example.agilelifemanagement.domain.model.Result.Error("User not authenticated")
                
            val id = task.id.takeIf { it.isNotBlank() } ?: UUID.randomUUID().toString()
            
            val now = System.currentTimeMillis()
            val entity = TaskEntity(
                id = id,
                title = task.title,
                summary = task.summary ?: "",
                description = task.description ?: emptyList(),
                dueDate = task.dueDate?.toEpochDay(),
                priority = task.priority.ordinal,
                status = task.status.ordinal,
                estimatedEffort = task.estimatedEffort,
                actualEffort = null, // Not in the Task domain model
                isRecurring = false, // Not in the Task domain model
                recurringPattern = null, // Not in the Task domain model
                userId = userId,
                createdAt = now,
                updatedAt = now
            )
            
            taskDao.insert(entity)
            android.util.Log.i(TAG, "Inserted task $id for user $userId")
            syncManager.scheduleSyncOperation(id, "task", PendingOperation.CREATE)
            
            // If online, try immediate sync
            if (networkMonitor.isOnline()) {
                try {
                    // Convert entity to DTO - this would typically be done with a separate mapper
                    val dto = com.example.agilelifemanagement.data.remote.dto.TaskDto(
                        id = entity.id,
                        title = entity.title,
                        summary = entity.summary,
                        description = entity.description,
                        due_date = entity.dueDate,
                        priority = com.example.agilelifemanagement.domain.model.TaskPriority.values()[entity.priority],
                        status = com.example.agilelifemanagement.domain.model.TaskStatus.values()[entity.status],
                        estimated_effort = entity.estimatedEffort,
                        actual_effort = null, // Not present in Task domain model
                        is_recurring = false, // Not present in Task domain model
                        recurring_pattern = null, // Not present in Task domain model
                        user_id = entity.userId,
                        created_at = entity.createdAt,
                        updated_at = entity.updatedAt
                    )
                    val apiResult = taskApiService.upsertTask(dto)
                    when (apiResult) {
                        is com.example.agilelifemanagement.domain.model.Result.Success -> {
                            syncManager.markSynced(id, "task")
                            android.util.Log.i(TAG, "Task created and synced immediately: $id")
                        }
                        is com.example.agilelifemanagement.domain.model.Result.Error -> {
                            android.util.Log.e(TAG, "Failed immediate sync for new task: ${apiResult.message}")
                        }
                        else -> { /* Loading state, not expected here */ }
                    }
                } catch (e: Exception) {
                    android.util.Log.e(TAG, "Error in immediate sync for new task: ${e.message}", e)
                    // Error is logged but not propagated to the caller as local operation succeeded
                }
            }
            
            com.example.agilelifemanagement.domain.model.Result.Success(id)
        } catch (e: Exception) {
            android.util.Log.e(TAG, "Error inserting task: ${e.message}", e)
            com.example.agilelifemanagement.domain.model.Result.Error("Failed to create task: ${e.message}")
        }
    }

    override suspend fun updateTask(task: Task): com.example.agilelifemanagement.domain.model.Result<Unit> {
        return try {
            val userId = supabaseManager.getCurrentUserId().first() 
                ?: return com.example.agilelifemanagement.domain.model.Result.Error("User not authenticated")
                
            val existing = taskDao.getTaskById(task.id) 
                ?: return com.example.agilelifemanagement.domain.model.Result.Error("Task not found: ${task.id}")
                
            val entity = existing.copy(
                title = task.title,
                summary = task.summary,
                description = task.description,
                dueDate = task.dueDate?.toEpochDay(),
                priority = task.priority.ordinal,
                status = task.status.ordinal,
                estimatedEffort = task.estimatedEffort,
                updatedAt = System.currentTimeMillis()
            )
            taskDao.update(entity)
            android.util.Log.i(TAG, "Updated task ${entity.id} for user $userId")
            syncManager.scheduleSyncOperation(entity.id, "task", PendingOperation.UPDATE)
            
            com.example.agilelifemanagement.domain.model.Result.Success(Unit)
        } catch (e: Exception) {
            android.util.Log.e(TAG, "Error updating task: ${e.message}", e)
            com.example.agilelifemanagement.domain.model.Result.Error("Failed to update task: ${e.message}")
        }
    }

    override suspend fun deleteTask(id: String): com.example.agilelifemanagement.domain.model.Result<Unit> {
        return try {
            val entity = taskDao.getTaskById(id)
                ?: return com.example.agilelifemanagement.domain.model.Result.Error("Task not found: $id")
            
            taskDao.delete(entity)
            android.util.Log.i(TAG, "Deleted task $id")
            syncManager.scheduleSyncOperation(id, "task", PendingOperation.DELETE)
            
            // If online, try immediate sync
            if (networkMonitor.isOnline()) {
                try {
                    val apiResult = taskApiService.deleteTask(id)
                    when (apiResult) {
                        is com.example.agilelifemanagement.domain.model.Result.Success -> {
                            syncManager.markSynced(id, "task")
                            android.util.Log.i(TAG, "Task deleted and synced immediately: $id")
                        }
                        is com.example.agilelifemanagement.domain.model.Result.Error -> {
                            android.util.Log.e(TAG, "Failed immediate sync for deleted task: ${apiResult.message}")
                        }
                        else -> { /* Loading state, not expected here */ }
                    }
                } catch (e: Exception) {
                    android.util.Log.e(TAG, "Error in immediate sync for deleted task: ${e.message}", e)
                    // Error is logged but not propagated to the caller as local operation succeeded
                }
            }
            
            com.example.agilelifemanagement.domain.model.Result.Success(Unit)
        } catch (e: Exception) {
            android.util.Log.e(TAG, "Error deleting task: ${e.message}", e)
            com.example.agilelifemanagement.domain.model.Result.Error("Failed to delete task: ${e.message}")
        }
    }

    override suspend fun addTaskToSprint(taskId: String, sprintId: String): com.example.agilelifemanagement.domain.model.Result<Unit> {
        return try {
            // Check if the task exists
            val task = taskDao.getTaskById(taskId)
                ?: return com.example.agilelifemanagement.domain.model.Result.Error("Task not found: $taskId")
            
            // Check if the relation already exists
            val existingRelation = taskSprintCrossRefDao.getTaskSprintCrossRef(taskId, sprintId)
            if (existingRelation != null) {
                return com.example.agilelifemanagement.domain.model.Result.Success(Unit) // Already exists, consider it a success
            }
            
            val id = UUID.randomUUID().toString()
            val crossRef = TaskSprintCrossRefEntity(
                id = id,
                taskId = taskId,
                sprintId = sprintId,
                createdAt = java.time.OffsetDateTime.now()
            )
            taskSprintCrossRefDao.insert(crossRef)
            android.util.Log.i(TAG, "Added task $taskId to sprint $sprintId")
            syncManager.scheduleSyncOperation(id, "task_sprint_cross_ref", PendingOperation.CREATE)
            
            // If online, try immediate sync
            if (networkMonitor.isOnline()) {
                try {
                    // Convert entity to DTO - assuming proper DTO structure
                    val dto = com.example.agilelifemanagement.data.remote.dto.TaskSprintCrossRefDto(
                        id = crossRef.id,
                        task_id = crossRef.taskId,
                        sprint_id = crossRef.sprintId,
                        created_at = crossRef.createdAt.toInstant().toEpochMilli()
                    )
                    val apiResult = taskSprintCrossRefApiService.createTaskSprintRelation(dto)
                    when (apiResult) {
                        is com.example.agilelifemanagement.domain.model.Result.Success -> {
                            syncManager.markSynced(id, "task_sprint_cross_ref")
                            android.util.Log.i(TAG, "Task-Sprint relation created and synced immediately: $id")
                        }
                        is com.example.agilelifemanagement.domain.model.Result.Error -> {
                            android.util.Log.e(TAG, "Failed immediate sync for new task-sprint relation: ${apiResult.message}")
                        }
                        else -> { /* Loading state, not expected here */ }
                    }
                } catch (e: Exception) {
                    android.util.Log.e(TAG, "Error in immediate sync for new task-sprint relation: ${e.message}", e)
                    // Error is logged but not propagated to the caller as local operation succeeded
                }
            }
            
            com.example.agilelifemanagement.domain.model.Result.Success(Unit)
        } catch (e: Exception) {
            android.util.Log.e(TAG, "Error adding task to sprint: ${e.message}", e)
            com.example.agilelifemanagement.domain.model.Result.Error("Failed to add task to sprint: ${e.message}")
        }
    }

    override suspend fun removeTaskFromSprint(taskId: String, sprintId: String): com.example.agilelifemanagement.domain.model.Result<Unit> {
        return try {
            val crossRef = taskSprintCrossRefDao.getTaskSprintCrossRef(taskId, sprintId)
                ?: return com.example.agilelifemanagement.domain.model.Result.Error("Relation not found: $taskId, $sprintId")
            
            taskSprintCrossRefDao.delete(taskId, sprintId)
            android.util.Log.i(TAG, "Removed task $taskId from sprint $sprintId")
            syncManager.scheduleSyncOperation(crossRef.id, "task_sprint_cross_ref", PendingOperation.DELETE)
            
            // If online, try immediate sync
            if (networkMonitor.isOnline()) {
                try {
                    val apiResult = taskSprintCrossRefApiService.deleteTaskSprintRelation(crossRef.id)
                    when (apiResult) {
                        is com.example.agilelifemanagement.domain.model.Result.Success -> {
                            syncManager.markSynced(crossRef.id, "task_sprint_cross_ref")
                            android.util.Log.i(TAG, "Task-Sprint relation deleted and synced immediately: ${crossRef.id}")
                        }
                        is com.example.agilelifemanagement.domain.model.Result.Error -> {
                            android.util.Log.e(TAG, "Failed immediate sync for deleted task-sprint relation: ${apiResult.message}")
                        }
                        else -> { /* Loading state, not expected here */ }
                    }
                } catch (e: Exception) {
                    android.util.Log.e(TAG, "Error in immediate sync for deleted task-sprint relation: ${e.message}", e)
                    // Error is logged but not propagated to the caller as local operation succeeded
                }
            }
            
            com.example.agilelifemanagement.domain.model.Result.Success(Unit)
        } catch (e: Exception) {
            android.util.Log.e(TAG, "Error removing task from sprint: ${e.message}", e)
            com.example.agilelifemanagement.domain.model.Result.Error("Failed to remove task from sprint: ${e.message}")
        }
    }

    override suspend fun addTaskToGoal(taskId: String, goalId: String): com.example.agilelifemanagement.domain.model.Result<Unit> {
        return try {
            // Check if the task exists
            val task = taskDao.getTaskById(taskId)
                ?: return com.example.agilelifemanagement.domain.model.Result.Error("Task not found: $taskId")
            
            // Check if the relation already exists (assuming there's a method to get TaskGoalCrossRef)
            val existingRelation = taskGoalCrossRefDao.getTaskGoalCrossRef(taskId, goalId)
            if (existingRelation != null) {
                return com.example.agilelifemanagement.domain.model.Result.Success(Unit) // Already exists, consider it a success
            }
            
            val id = UUID.randomUUID().toString()
            val crossRef = TaskGoalCrossRefEntity(
                id = id,
                taskId = taskId,
                goalId = goalId,
                createdAt = java.time.OffsetDateTime.now()
            )
            taskGoalCrossRefDao.insert(crossRef)
            android.util.Log.i(TAG, "Added task $taskId to goal $goalId")
            syncManager.scheduleSyncOperation(id, "task_goal_cross_ref", PendingOperation.CREATE)
            
            // If online, try immediate sync
            if (networkMonitor.isOnline()) {
                try {
                    // API call to sync with Supabase would go here
                    // This may require appropriate DTO conversion and API service method
                } catch (e: Exception) {
                    android.util.Log.e(TAG, "Error in immediate sync for new task-goal relation: ${e.message}", e)
                    // Error is logged but not propagated to the caller as local operation succeeded
                }
            }
            
            com.example.agilelifemanagement.domain.model.Result.Success(Unit)
        } catch (e: Exception) {
            android.util.Log.e(TAG, "Error adding task to goal: ${e.message}", e)
            com.example.agilelifemanagement.domain.model.Result.Error("Failed to add task to goal: ${e.message}")
        }
    }

    override suspend fun removeTaskFromGoal(taskId: String, goalId: String): com.example.agilelifemanagement.domain.model.Result<Unit> {
        return try {
            val crossRef = taskGoalCrossRefDao.getTaskGoalCrossRef(taskId, goalId)
                ?: return com.example.agilelifemanagement.domain.model.Result.Error("Relation not found: $taskId, $goalId")
            
            taskGoalCrossRefDao.delete(taskId, goalId)
            android.util.Log.i(TAG, "Removed task $taskId from goal $goalId")
            syncManager.scheduleSyncOperation(crossRef.id, "task_goal_cross_ref", PendingOperation.DELETE)
            
            // If online, try immediate sync
            if (networkMonitor.isOnline()) {
                try {
                    // API call to sync with Supabase would go here
                    // This may require appropriate DTO conversion and API service method
                } catch (e: Exception) {
                    android.util.Log.e(TAG, "Error in immediate sync for deleted task-goal relation: ${e.message}", e)
                    // Error is logged but not propagated to the caller as local operation succeeded
                }
            }
            
            com.example.agilelifemanagement.domain.model.Result.Success(Unit)
        } catch (e: Exception) {
            android.util.Log.e(TAG, "Error removing task from goal: ${e.message}", e)
            com.example.agilelifemanagement.domain.model.Result.Error("Failed to remove task from goal: ${e.message}")
        }
    }

    override suspend fun addTagToTask(taskId: String, tagId: String): com.example.agilelifemanagement.domain.model.Result<Unit> {
        return try {
            // Check if the task exists
            val task = taskDao.getTaskById(taskId)
                ?: return com.example.agilelifemanagement.domain.model.Result.Error("Task not found: $taskId")
            
            // Check if the relation already exists (assuming there's a method to get TaskTagCrossRef)
            // If there's no such method, you might need to add one to the DAO
            val existingRelation = try {
                taskTagCrossRefDao.getTaskTagCrossRef(taskId, tagId)
            } catch (e: Exception) {
                null // Method might not exist yet
            }
            
            if (existingRelation != null) {
                return com.example.agilelifemanagement.domain.model.Result.Success(Unit) // Already exists, consider it a success
            }
            
            val id = UUID.randomUUID().toString()
            val crossRef = TaskTagCrossRefEntity(
                id = id,
                taskId = taskId,
                tagId = tagId,
                createdAt = java.time.OffsetDateTime.now()
            )
            taskTagCrossRefDao.insert(crossRef)
            android.util.Log.i(TAG, "Added tag $tagId to task $taskId")
            syncManager.scheduleSyncOperation(id, "task_tag_cross_ref", PendingOperation.CREATE)
            
            // If online, try immediate sync
            if (networkMonitor.isOnline()) {
                try {
                    // API call to sync with Supabase would go here
                    // This may require appropriate DTO conversion and API service method
                } catch (e: Exception) {
                    android.util.Log.e(TAG, "Error in immediate sync for new task-tag relation: ${e.message}", e)
                    // Error is logged but not propagated to the caller as local operation succeeded
                }
            }
            
            com.example.agilelifemanagement.domain.model.Result.Success(Unit)
        } catch (e: Exception) {
            android.util.Log.e(TAG, "Error adding tag to task: ${e.message}", e)
            com.example.agilelifemanagement.domain.model.Result.Error("Failed to add tag to task: ${e.message}")
        }
    }

    override suspend fun removeTagFromTask(taskId: String, tagId: String): com.example.agilelifemanagement.domain.model.Result<Unit> {
        return try {
            val crossRef = taskTagCrossRefDao.getTaskTagCrossRef(taskId, tagId)
                ?: return com.example.agilelifemanagement.domain.model.Result.Error("Relation not found: $taskId, $tagId")
            
            taskTagCrossRefDao.delete(taskId, tagId)
            android.util.Log.i(TAG, "Removed tag $tagId from task $taskId")
            syncManager.scheduleSyncOperation(crossRef.id, "task_tag_cross_ref", PendingOperation.DELETE)
            
            // If online, try immediate sync
            if (networkMonitor.isOnline()) {
                try {
                    // API call to sync with Supabase would go here
                    // This may require appropriate DTO conversion and API service method
                } catch (e: Exception) {
                    android.util.Log.e(TAG, "Error in immediate sync for deleted task-tag relation: ${e.message}", e)
                    // Error is logged but not propagated to the caller as local operation succeeded
                }
            }
            
            com.example.agilelifemanagement.domain.model.Result.Success(Unit)
        } catch (e: Exception) {
            android.util.Log.e(TAG, "Error removing tag from task: ${e.message}", e)
            com.example.agilelifemanagement.domain.model.Result.Error("Failed to remove tag from task: ${e.message}")
        }
    }

    override suspend fun addTaskDependency(taskId: String, dependsOnTaskId: String): com.example.agilelifemanagement.domain.model.Result<Unit> {
        return try {
            // Check if the tasks exist
            val task = taskDao.getTaskById(taskId)
                ?: return com.example.agilelifemanagement.domain.model.Result.Error("Task not found: $taskId")
            
            val dependsOnTask = taskDao.getTaskById(dependsOnTaskId)
                ?: return com.example.agilelifemanagement.domain.model.Result.Error("Dependency task not found: $dependsOnTaskId")
            
            // Check if the relation already exists
            val existingDependency = taskDependencyDao.getTaskDependency(taskId, dependsOnTaskId)
            if (existingDependency != null) {
                return com.example.agilelifemanagement.domain.model.Result.Success(Unit) // Already exists, consider it a success
            }
            
            // Check for circular dependency
            val circularCheck = taskDependencyDao.getTaskDependency(dependsOnTaskId, taskId)
            if (circularCheck != null) {
                return com.example.agilelifemanagement.domain.model.Result.Error("Circular dependency detected: $taskId depends on $dependsOnTaskId and vice versa")
            }
            
            val dependencyId = UUID.randomUUID().toString()
            val dependency = TaskDependencyEntity(
                id = dependencyId,
                taskId = taskId,
                dependsOnTaskId = dependsOnTaskId,
                createdAt = java.time.OffsetDateTime.now()
            )
            taskDependencyDao.insert(dependency)
            android.util.Log.i(TAG, "Added dependency: Task $taskId depends on $dependsOnTaskId")
            syncManager.scheduleSyncOperation(dependencyId, "task_dependency", PendingOperation.CREATE)
            
            // If online, try immediate sync
            if (networkMonitor.isOnline()) {
                try {
                    // API call to sync with Supabase would go here
                    // This may require appropriate DTO conversion and API service method
                } catch (e: Exception) {
                    android.util.Log.e(TAG, "Error in immediate sync for new task dependency: ${e.message}", e)
                    // Error is logged but not propagated to the caller as local operation succeeded
                }
            }
            
            com.example.agilelifemanagement.domain.model.Result.Success(Unit)
        } catch (e: Exception) {
            android.util.Log.e(TAG, "Error adding task dependency: ${e.message}", e)
            com.example.agilelifemanagement.domain.model.Result.Error("Failed to add task dependency: ${e.message}")
        }
    }

    override suspend fun removeTaskDependency(taskId: String, dependsOnTaskId: String): com.example.agilelifemanagement.domain.model.Result<Unit> {
        return try {
            val dependency = taskDependencyDao.getTaskDependency(taskId, dependsOnTaskId)
                ?: return com.example.agilelifemanagement.domain.model.Result.Error("Relation not found: $taskId, $dependsOnTaskId")
            
            taskDependencyDao.delete(taskId, dependsOnTaskId)
            android.util.Log.i(TAG, "Removed dependency: Task $taskId depends on $dependsOnTaskId")
            syncManager.scheduleSyncOperation(dependency.id, "task_dependency", PendingOperation.DELETE)
            
            // If online, try immediate sync
            if (networkMonitor.isOnline()) {
                try {
                    // API call to sync with Supabase would go here
                    // This may require appropriate DTO conversion and API service method
                } catch (e: Exception) {
                    android.util.Log.e(TAG, "Error in immediate sync for deleted task dependency: ${e.message}", e)
                    // Error is logged but not propagated to the caller as local operation succeeded
                }
            }
            
            com.example.agilelifemanagement.domain.model.Result.Success(Unit)
        } catch (e: Exception) {
            android.util.Log.e(TAG, "Error removing task dependency: ${e.message}", e)
            com.example.agilelifemanagement.domain.model.Result.Error("Failed to remove task dependency: ${e.message}")
        }
    }
}
