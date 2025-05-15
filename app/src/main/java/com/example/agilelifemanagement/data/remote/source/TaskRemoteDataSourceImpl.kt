package com.example.agilelifemanagement.data.remote.source

import com.example.agilelifemanagement.domain.model.Task
import com.example.agilelifemanagement.domain.model.TaskStatus
import timber.log.Timber
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of [TaskRemoteDataSource] that serves as a stub for future remote integration.
 * This implementation provides placeholder functionality that will be replaced with
 * actual remote API calls in future iterations.
 */
@Singleton
class TaskRemoteDataSourceImpl @Inject constructor() : TaskRemoteDataSource {
    
    override suspend fun getAllTasks(): List<Task> {
        Timber.d("Remote: getAllTasks called (stub)")
        return emptyList() // Stub implementation
    }
    
    override suspend fun getTaskById(taskId: String): Task? {
        Timber.d("Remote: getTaskById called (stub) for task: $taskId")
        return null // Stub implementation
    }
    
    override suspend fun getTasksBySprintId(sprintId: String): List<Task> {
        Timber.d("Remote: getTasksBySprintId called (stub) for sprint: $sprintId")
        return emptyList() // Stub implementation
    }
    
    override suspend fun getTasksByDate(date: LocalDate): List<Task> {
        Timber.d("Remote: getTasksByDate called (stub) for date: $date")
        return emptyList() // Stub implementation
    }
    
    override suspend fun createTask(task: Task): Task {
        Timber.d("Remote: createTask called (stub) for task: ${task.title}")
        return task // Stub implementation, just returns the input task
    }
    
    override suspend fun updateTask(task: Task): Task {
        Timber.d("Remote: updateTask called (stub) for task: ${task.id}")
        return task // Stub implementation, just returns the input task
    }
    
    override suspend fun deleteTask(taskId: String): Boolean {
        Timber.d("Remote: deleteTask called (stub) for task: $taskId")
        return true // Stub implementation, pretend it always succeeds
    }
    
    override suspend fun updateTaskStatus(taskId: String, status: TaskStatus): Task {
        Timber.d("Remote: updateTaskStatus called (stub) for task: $taskId to status: $status")
        throw NotImplementedError("Remote updateTaskStatus not implemented yet") // Demonstrate error handling
    }
}
