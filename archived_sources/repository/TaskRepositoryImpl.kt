package com.example.agilelifemanagement.data.repository

import com.example.agilelifemanagement.data.local.source.TaskLocalDataSource
import com.example.agilelifemanagement.data.mapper.TaskMapper
import com.example.agilelifemanagement.data.remote.source.TaskRemoteDataSource
import com.example.agilelifemanagement.di.IoDispatcher
import com.example.agilelifemanagement.domain.model.Task
import com.example.agilelifemanagement.domain.model.TaskStatus
import com.example.agilelifemanagement.domain.repository.TaskRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of [TaskRepository] that coordinates between local and remote data sources.
 * This implementation follows the offline-first approach, providing immediate access to local data
 * while syncing with remote sources in the background.
 */
@Singleton
class TaskRepositoryImpl @Inject constructor(
    private val taskLocalDataSource: TaskLocalDataSource,
    private val taskRemoteDataSource: TaskRemoteDataSource,
    private val taskMapper: TaskMapper,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : TaskRepository {

    override fun getAllTasks(): Flow<List<Task>> {
        // Offline-first: Return local data immediately, then try to sync with remote
        syncTasksWithRemote()
        return taskLocalDataSource.observeTasks()
            .map { taskEntities -> taskEntities.map { taskMapper.mapToDomain(it) } }
            .flowOn(ioDispatcher)
    }
    
    /**
     * Synchronizes local task data with remote data source in the background.
     */
    private fun syncTasksWithRemote() {
        withContext(ioDispatcher) {
            launch {
                try {
                    val remoteTasks = taskRemoteDataSource.getAllTasks()
                    val entityTasks = remoteTasks.map { taskMapper.mapToEntity(it) }
                    taskLocalDataSource.insertTasks(entityTasks)
                    Timber.d("Successfully synced ${remoteTasks.size} tasks with remote")
                } catch (e: Exception) {
                    Timber.e(e, "Error syncing tasks with remote")
                    // Handle error but don't propagate - offline-first approach continues with local data
                }
            }
        }
    }

    override fun getTasksBySprintId(sprintId: String): Flow<List<Task>> {
        // Try to sync sprint-specific tasks in the background
        syncTasksBySprintId(sprintId)
        
        return taskLocalDataSource.observeTasksBySprintId(sprintId)
            .map { taskEntities -> taskEntities.map { taskMapper.mapToDomain(it) } }
            .flowOn(ioDispatcher)
    }
    
    private fun syncTasksBySprintId(sprintId: String) {
        withContext(ioDispatcher) {
            launch {
                try {
                    val remoteTasks = taskRemoteDataSource.getTasksBySprintId(sprintId)
                    val entityTasks = remoteTasks.map { taskMapper.mapToEntity(it) }
                    taskLocalDataSource.insertTasks(entityTasks)
                } catch (e: Exception) {
                    Timber.e(e, "Error syncing tasks for sprint $sprintId")
                }
            }
        }
    }

    override suspend fun getTaskById(taskId: String): Result<Task> = withContext(ioDispatcher) {
        try {
            // First try to get from local database
            var taskEntity = taskLocalDataSource.getTaskById(taskId)
            
            // If not found locally or we need the latest data, try remote
            if (taskEntity == null) {
                try {
                    val remoteTask = taskRemoteDataSource.getTaskById(taskId)
                    if (remoteTask != null) {
                        // Found on remote, save to local database
                        val entity = taskMapper.mapToEntity(remoteTask)
                        taskLocalDataSource.insertTask(entity)
                        taskEntity = entity
                    }
                } catch (e: Exception) {
                    Timber.e(e, "Error fetching task from remote: $taskId")
                    // Continue with local data (which might be null)
                }
            }
            
            taskEntity?.let {
                Result.success(taskMapper.mapToDomain(it))
            } ?: Result.failure(NoSuchElementException("Task not found with ID: $taskId"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getTasksByDate(date: LocalDate): Flow<List<Task>> {
        // Trigger background sync for tasks on this date
        syncTasksByDate(date)
        
        return taskLocalDataSource.observeTasksByDate(date)
            .map { taskEntities -> taskEntities.map { taskMapper.mapToDomain(it) } }
            .flowOn(ioDispatcher)
    }
    
    private fun syncTasksByDate(date: LocalDate) {
        withContext(ioDispatcher) {
            launch {
                try {
                    val remoteTasks = taskRemoteDataSource.getTasksByDate(date)
                    val entityTasks = remoteTasks.map { taskMapper.mapToEntity(it) }
                    taskLocalDataSource.insertTasks(entityTasks)
                } catch (e: Exception) {
                    Timber.e(e, "Error syncing tasks for date $date")
                    // Continue with local data
                }
            }
        }
    }

    override suspend fun createTask(task: Task): Result<Task> = withContext(ioDispatcher) {
        try {
            // First save to local database for immediate feedback
            val taskEntity = taskMapper.mapToEntity(task)
            val insertedId = taskLocalDataSource.insertTask(taskEntity)
            val insertedTask = task.copy(id = insertedId.toString())
            
            // Then try to save to remote in background
            launch {
                try {
                    taskRemoteDataSource.createTask(insertedTask)
                    Timber.d("Successfully synced new task to remote: ${insertedTask.id}")
                } catch (e: Exception) {
                    Timber.e(e, "Error syncing new task to remote: ${insertedTask.id}")
                    // Task will be synced later when connectivity is restored
                    // Could add to a sync queue for retry mechanism
                }
            }
            
            Result.success(insertedTask)
        } catch (e: Exception) {
            Timber.e(e, "Error creating task locally")
            Result.failure(e)
        }
    }

    override suspend fun updateTask(task: Task): Result<Task> = withContext(ioDispatcher) {
        try {
            // Update local database first for immediate feedback
            val taskEntity = taskMapper.mapToEntity(task)
            taskLocalDataSource.updateTask(taskEntity)
            
            // Then try to update remote in background
            launch {
                try {
                    taskRemoteDataSource.updateTask(task)
                    Timber.d("Successfully synced updated task to remote: ${task.id}")
                } catch (e: Exception) {
                    Timber.e(e, "Error syncing updated task to remote: ${task.id}")
                    // Task will be synced later when connectivity is restored
                    // Could add to a sync queue for retry mechanism
                }
            }
            
            Result.success(task)
        } catch (e: Exception) {
            Timber.e(e, "Error updating task locally: ${task.id}")
            Result.failure(e)
        }
    }

    override suspend fun deleteTask(taskId: String): Result<Boolean> = withContext(ioDispatcher) {
        try {
            // Delete from local database first
            taskLocalDataSource.deleteTask(taskId)
            
            // Then try to delete from remote in background
            launch {
                try {
                    taskRemoteDataSource.deleteTask(taskId)
                    Timber.d("Successfully deleted task from remote: $taskId")
                } catch (e: Exception) {
                    Timber.e(e, "Error deleting task from remote: $taskId")
                    // Could add to a deletion sync queue for retry mechanism
                }
            }
            
            Result.success(true)
        } catch (e: Exception) {
            Timber.e(e, "Error deleting task locally: $taskId")
            Result.failure(e)
        }
    }

    override suspend fun updateTaskStatus(taskId: String, status: TaskStatus): Result<Task> = withContext(ioDispatcher) {
        try {
            // Get task from local database
            val task = taskLocalDataSource.getTaskById(taskId)
                ?: return@withContext Result.failure(NoSuchElementException("Task not found with ID: $taskId"))
            
            // Update status locally first
            val updatedTask = task.copy(status = status)
            taskLocalDataSource.updateTask(updatedTask)
            
            // Then try to update in remote
            val domainTask = taskMapper.mapToDomain(updatedTask)
            launch {
                try {
                    taskRemoteDataSource.updateTaskStatus(taskId, status)
                    Timber.d("Successfully updated task status in remote: $taskId to $status")
                } catch (e: Exception) {
                    Timber.e(e, "Error updating task status in remote: $taskId")
                    // Could add to a sync queue for retry mechanism
                }
            }
            
            Result.success(domainTask)
        } catch (e: Exception) {
            Timber.e(e, "Error updating task status locally: $taskId")
            Result.failure(e)
        }
    }
    
    /**
     * Get tasks filtered by tags.
     * @param tags List of tag names to filter by
     * @return A Flow emitting lists of tasks with the specified tags
     */
    override fun getTasksByTags(tags: List<String>): Flow<List<Task>> {
        // Temporary implementation until full data layer is rebuilt
        return taskLocalDataSource.observeTasks()
            .map { entities ->
                entities
                    .map { taskMapper.mapToDomain(it) }
                    .filter { task -> 
                        tags.isEmpty() || task.tags.any { it in tags }
                    }
            }
            .flowOn(ioDispatcher)
    }
}
