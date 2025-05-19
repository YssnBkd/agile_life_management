package com.example.agilelifemanagement.data.repository

import com.example.agilelifemanagement.data.local.source.TaskLocalDataSource
import com.example.agilelifemanagement.data.mapper.TaskMapper
import com.example.agilelifemanagement.data.remote.source.TaskRemoteDataSource
import com.example.agilelifemanagement.di.IoDispatcher
import com.example.agilelifemanagement.domain.model.Task
import com.example.agilelifemanagement.domain.model.TaskStatus
import com.example.agilelifemanagement.domain.repository.TagRepository
import com.example.agilelifemanagement.domain.repository.TaskRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.time.LocalDate
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of [TaskRepository] that coordinates between local and remote data sources.
 * This implementation follows the offline-first approach, providing immediate access to local data
 * while syncing with remote sources in the background.
 */
@Singleton
class TaskRepositoryImpl @Inject constructor(
    private val localDataSource: TaskLocalDataSource,
    private val remoteDataSource: TaskRemoteDataSource,
    private val taskMapper: TaskMapper,
    private val tagRepository: TagRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : TaskRepository {
    
    // Repository-scoped coroutine scope for background operations
    private val repositoryScope = CoroutineScope(SupervisorJob() + ioDispatcher)

    override fun getAllTasks(): Flow<List<Task>> {
        // Launch coroutine to sync with remote in background
        repositoryScope.launch {
            try {
                syncAllTasks()
            } catch (e: Exception) {
                Timber.e(e, "Error syncing all tasks in background")
            }
        }
        
        return localDataSource.observeTasks()
            .map { entities -> entities.map { taskMapper.mapToDomain(it) } }
            .flowOn(ioDispatcher)
    }
    
    private suspend fun syncAllTasks() {
        // In the MVP implementation, we'll focus on local data only
        // and skip the synchronization with remote data sources
        Timber.d("MVP Implementation: Skipping remote sync")
    }

    override fun getTasksBySprintId(sprintId: String): Flow<List<Task>> {
        // Launch coroutine to sync with remote in background
        repositoryScope.launch {
            try {
                syncTasksBySprintId(sprintId)
            } catch (e: Exception) {
                Timber.e(e, "Error syncing tasks for sprint $sprintId in background")
            }
        }
        
        return localDataSource.observeTasksBySprintId(sprintId)
            .map { entities -> entities.map { taskMapper.mapToDomain(it) } }
            .flowOn(ioDispatcher)
    }
    
    private suspend fun syncTasksBySprintId(sprintId: String) {
        // For MVP, we're focusing on local data only
        Timber.d("MVP Implementation: Skipping remote sync for sprint $sprintId")
    }

    override suspend fun getTaskById(taskId: String): Result<Task> = withContext(ioDispatcher) {
        try {
            // Try to get from local database first
            val localTask = localDataSource.getTaskById(taskId)
            
            if (localTask != null) {
                Result.success(taskMapper.mapToDomain(localTask))
            } else {
                // If not in local database, try to get from remote
                val remoteTask = remoteDataSource.getTaskById(taskId)
                if (remoteTask != null) {
                    // Save to local database for future access
                    localDataSource.insertTask(taskMapper.mapToEntity(remoteTask))
                    Result.success(remoteTask)
                } else {
                    Result.failure(NoSuchElementException("Task not found with ID: $taskId"))
                }
            }
        } catch (e: Exception) {
            Timber.e(e, "Error getting task by ID: $taskId")
            Result.failure(e)
        }
    }

    override fun getTasksByDate(date: LocalDate): Flow<List<Task>> {
        // Launch coroutine to sync with remote in background
        repositoryScope.launch {
            try {
                syncTasksByDate(date)
            } catch (e: Exception) {
                Timber.e(e, "Error syncing tasks for date $date in background")
            }
        }
        
        return localDataSource.observeTasksByDate(date)
            .map { entities -> entities.map { taskMapper.mapToDomain(it) } }
            .flowOn(ioDispatcher)
    }
    
    private suspend fun syncTasksByDate(date: LocalDate) {
        withContext(ioDispatcher) {
            try {
                val remoteTasks = remoteDataSource.getTasksByDate(date)
                val entities = remoteTasks.map { taskMapper.mapToEntity(it) }
                localDataSource.insertTasks(entities)
                Timber.d("Successfully synced ${remoteTasks.size} tasks for date $date")
            } catch (e: Exception) {
                Timber.e(e, "Error syncing tasks for date $date")
                // Handle error but don't propagate
            }
        }
    }

    override suspend fun createTask(task: Task): Result<Task> = withContext(ioDispatcher) {
        try {
            // Generate ID if not provided
            val taskWithId = if (task.id.isBlank()) {
                // Create a proper ID and ensure createdDate is set
                task.copy(
                    id = UUID.randomUUID().toString(),
                    createdDate = task.createdDate ?: LocalDate.now()
                )
            } else {
                task
            }
            
            // Save to local database
            val entity = taskMapper.mapToEntity(taskWithId)
            localDataSource.insertTask(entity)
            
            // For MVP, we'll skip the remote sync and just log the operation
            Timber.d("MVP Implementation: Created task ${taskWithId.id} (local only)")
            
            Result.success(taskWithId)
        } catch (e: Exception) {
            Timber.e(e, "Error creating task: ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun updateTask(task: Task): Result<Task> = withContext(ioDispatcher) {
        try {
            // Update in local database only for MVP
            val taskEntity = taskMapper.mapToEntity(task)
            val updateCount = localDataSource.updateTask(taskEntity)
            
            if (updateCount <= 0) {
                return@withContext Result.failure(Exception("Failed to update task: No records affected"))
            }
            
            // For MVP, we'll skip the remote sync
            Timber.d("MVP Implementation: Updated task ${task.id} (local only)")
            
            Result.success(task)
        } catch (e: Exception) {
            Timber.e(e, "Error updating task: ${task.id} - ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun updateTaskStatus(taskId: String, status: TaskStatus): Result<Task> = withContext(ioDispatcher) {
        try {
            // Update status in local database only for MVP
            val updateCount = localDataSource.updateTaskStatus(taskId, status)
            
            if (updateCount <= 0) {
                return@withContext Result.failure(NoSuchElementException("Task not found with ID: $taskId"))
            }
            
            // Get updated task to return
            val updatedTask = localDataSource.getTaskById(taskId)?.let { taskMapper.mapToDomain(it) }
                ?: return@withContext Result.failure(NoSuchElementException("Task not found after update: $taskId"))
            
            // For MVP, we'll skip the remote sync
            Timber.d("MVP Implementation: Updated task status $taskId to $status (local only)")
            
            Result.success(updatedTask)
        } catch (e: Exception) {
            Timber.e(e, "Error updating task status: $taskId - ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun deleteTask(taskId: String): Result<Boolean> = withContext(ioDispatcher) {
        try {
            // Delete from local database only for MVP
            val deleteResult = localDataSource.deleteTask(taskId)
            
            if (deleteResult <= 0) {
                return@withContext Result.failure(NoSuchElementException("Task not found with ID: $taskId"))
            }
            
            // For MVP, we'll skip the remote sync
            Timber.d("MVP Implementation: Deleted task $taskId (local only)")
            
            Result.success(true)
        } catch (e: Exception) {
            Timber.e(e, "Error deleting task: $taskId - ${e.message}")
            Result.failure(e)
        }
    }

    override fun getTasksByTags(tags: List<String>): Flow<List<Task>> {
        // Convert tag names to IDs
        val tagIds = tags.map { it } // In a real implementation, we would map tag names to IDs
        
        return localDataSource.observeTasksByTags(tagIds)
            .map { entities -> entities.map { taskMapper.mapToDomain(it) } }
            .flowOn(ioDispatcher)
    }

    override fun getTasksApproachingDeadline(days: Int): Flow<List<Task>> {
        // Calculate the target date range (today to X days from now)
        val today = LocalDate.now()
        val targetDate = today.plusDays(days.toLong())
        
        return localDataSource.observeTasksWithDeadlineBetween(today, targetDate)
            .map { entities -> entities.map { taskMapper.mapToDomain(it) } }
            .flowOn(ioDispatcher)
    }

    override fun getOverdueTasks(): Flow<List<Task>> {
        // Get tasks with deadline before today and not completed
        val today = LocalDate.now()
        
        return localDataSource.observeOverdueTasks(today)
            .map { entities -> entities.map { taskMapper.mapToDomain(it) } }
            .flowOn(ioDispatcher)
    }

    override fun getTasksByPriority(priority: String): Flow<List<Task>> {
        return localDataSource.observeTasksByPriority(priority)
            .map { entities -> entities.map { taskMapper.mapToDomain(it) } }
            .flowOn(ioDispatcher)
    }

    override fun getTaskCountByStatus(): Flow<Map<TaskStatus, Int>> {
        return localDataSource.observeTaskCountByStatus()
            .map { statusCountMap ->
                // Convert the string status to enum - explicitly define types for compiler
                val result = mutableMapOf<TaskStatus, Int>()
                statusCountMap.forEach { (statusStr, count) ->
                    try {
                        val taskStatus = TaskStatus.valueOf(statusStr)
                        result[taskStatus] = count
                    } catch (e: IllegalArgumentException) {
                        Timber.w("Unknown task status: $statusStr")
                    }
                }
                result
            }
            .flowOn(ioDispatcher)
    }

    override fun getRecentlyCompletedTasks(limit: Int): Flow<List<Task>> {
        return localDataSource.observeRecentlyCompletedTasks(limit)
            .map { entities -> entities.map { taskMapper.mapToDomain(it) } }
            .flowOn(ioDispatcher)
    }
}
