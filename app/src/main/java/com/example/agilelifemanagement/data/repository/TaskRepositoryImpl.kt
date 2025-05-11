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

    override suspend fun insertTask(task: Task): String {
        return try {
            val userId = supabaseManager.getCurrentUserId().first() ?: error("User ID must not be null")
            val id = task.id.ifEmpty { UUID.randomUUID().toString() }
            val currentTimeMillis = System.currentTimeMillis()
            val entity = TaskEntity(
                id = id,
                title = task.title,
                summary = task.summary,
                description = task.description,
                dueDate = task.dueDate?.toEpochDay(),
                priority = task.priority.ordinal,
                status = task.status.ordinal,
                estimatedEffort = task.estimatedEffort,
                actualEffort = null,
                isRecurring = false, // TODO: Support recurring
                recurringPattern = null, // TODO: Support recurring
                userId = userId,
                createdAt = currentTimeMillis,
                updatedAt = currentTimeMillis
            )
            taskDao.insert(entity)
            android.util.Log.i(TAG, "Inserted task $id for user $userId")
            syncManager.scheduleSync(id, "task", PendingOperation.CREATE)
            id
        } catch (e: Exception) {
            android.util.Log.e(TAG, "Error inserting task", e)
            throw e
        }
    }

    override suspend fun updateTask(task: Task) {
        try {
            val userId = supabaseManager.getCurrentUserId() ?: error("User ID must not be null")
            val existing = taskDao.getTaskById(task.id) ?: error("Task not found")
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
            syncManager.scheduleSync(entity.id, "task", PendingOperation.UPDATE)
        } catch (e: Exception) {
            android.util.Log.e(TAG, "Error updating task", e)
            throw e
        }
    }

    override suspend fun deleteTask(id: String) {
        try {
            val entity = taskDao.getTaskById(id)
            if (entity != null) {
                taskDao.delete(entity)
                android.util.Log.i(TAG, "Deleted task $id")
                syncManager.scheduleSync(id, "task", PendingOperation.DELETE)
            }
        } catch (e: Exception) {
            android.util.Log.e(TAG, "Error deleting task", e)
            throw e
        }
    }

    override suspend fun addTaskToSprint(taskId: String, sprintId: String) {
        try {
            val crossRef = TaskSprintCrossRefEntity(
                id = UUID.randomUUID().toString(),
                taskId = taskId,
                sprintId = sprintId,
                createdAt = java.time.OffsetDateTime.now()
            )
            taskSprintCrossRefDao.insert(crossRef)
            syncManager.scheduleSync(taskId, "task_sprint_cross_ref", PendingOperation.CREATE)
        } catch (e: Exception) {
            android.util.Log.e(TAG, "Error in cross-ref operation", e)
        }
    }

    override suspend fun removeTaskFromSprint(taskId: String, sprintId: String) {
        try {
            val crossRef = TaskSprintCrossRefEntity(
                id = UUID.randomUUID().toString(),
                taskId = taskId,
                sprintId = sprintId,
                createdAt = java.time.OffsetDateTime.now()
            )
            taskSprintCrossRefDao.delete(crossRef)
            syncManager.scheduleSync(taskId, "task_sprint_cross_ref", PendingOperation.DELETE)
        } catch (e: Exception) {
            android.util.Log.e(TAG, "Error in cross-ref operation", e)
        }
    }

    override suspend fun addTaskToGoal(taskId: String, goalId: String) {
        try {
            val crossRef = TaskGoalCrossRefEntity(
                id = UUID.randomUUID().toString(),
                taskId = taskId,
                goalId = goalId,
                createdAt = java.time.OffsetDateTime.now()
            )
            taskGoalCrossRefDao.insert(crossRef)
            syncManager.scheduleSync(taskId, "task_goal_cross_ref", PendingOperation.CREATE)
        } catch (e: Exception) {
            android.util.Log.e(TAG, "Error in cross-ref operation", e)
        }
    }

    override suspend fun removeTaskFromGoal(taskId: String, goalId: String) {
        try {
            val crossRef = TaskGoalCrossRefEntity(
                id = UUID.randomUUID().toString(),
                taskId = taskId,
                goalId = goalId,
                createdAt = java.time.OffsetDateTime.now()
            )
            taskGoalCrossRefDao.delete(crossRef)
            syncManager.scheduleSync(taskId, "task_goal_cross_ref", PendingOperation.DELETE)
        } catch (e: Exception) {
            android.util.Log.e(TAG, "Error in cross-ref operation", e)
        }
    }

    override suspend fun addTagToTask(taskId: String, tagId: String) {
        try {
            val crossRef = TaskTagCrossRefEntity(
                id = UUID.randomUUID().toString(),
                taskId = taskId,
                tagId = tagId,
                createdAt = java.time.OffsetDateTime.now()
            )
            taskTagCrossRefDao.insert(crossRef)
            syncManager.scheduleSync(taskId, "task_tag_cross_ref", PendingOperation.CREATE)
        } catch (e: Exception) {
            android.util.Log.e(TAG, "Error in cross-ref operation", e)
        }
    }

    override suspend fun removeTagFromTask(taskId: String, tagId: String) {
        try {
            val crossRef = TaskTagCrossRefEntity(
                id = UUID.randomUUID().toString(),
                taskId = taskId,
                tagId = tagId,
                createdAt = java.time.OffsetDateTime.now()
            )
            taskTagCrossRefDao.delete(crossRef)
            syncManager.scheduleSync(taskId, "task_tag_cross_ref", PendingOperation.DELETE)
        } catch (e: Exception) {
            android.util.Log.e(TAG, "Error in cross-ref operation", e)
        }
    }

    override suspend fun addTaskDependency(taskId: String, dependsOnTaskId: String) {
        try {
            val dependency = TaskDependencyEntity(
                id = UUID.randomUUID().toString(),
                taskId = taskId,
                dependsOnTaskId = dependsOnTaskId,
                createdAt = java.time.OffsetDateTime.now()
            )
            taskDependencyDao.insert(dependency)
            syncManager.scheduleSync(taskId, "task_dependency", PendingOperation.CREATE)
        } catch (e: Exception) {
            android.util.Log.e(TAG, "Error in cross-ref operation", e)
        }
    }

    override suspend fun removeTaskDependency(taskId: String, dependsOnTaskId: String) {
        try {
            val dependency = TaskDependencyEntity(
                id = UUID.randomUUID().toString(),
                taskId = taskId,
                dependsOnTaskId = dependsOnTaskId,
                createdAt = java.time.OffsetDateTime.now()
            )
            taskDependencyDao.delete(dependency)
            syncManager.scheduleSync(taskId, "task_dependency", PendingOperation.DELETE)
        } catch (e: Exception) {
            android.util.Log.e(TAG, "Error in cross-ref operation", e)
        }
    }
}
