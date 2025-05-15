package com.example.agilelifemanagement.data.remote.api

import com.example.agilelifemanagement.data.remote.model.TaskDto
import com.example.agilelifemanagement.domain.model.TaskStatus
import io.ktor.client.HttpClient
import timber.log.Timber
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of [TaskApiService] that serves as a stub for future API integration.
 * This implementation provides placeholder functionality that will be replaced with
 * actual API calls in future iterations.
 */
@Singleton
class TaskApiServiceImpl @Inject constructor(
    private val httpClient: HttpClient
) : TaskApiService {
    
    override suspend fun getAllTasks(): List<TaskDto> {
        Timber.d("API: getAllTasks called (stub)")
        // In a real implementation, this would make an HTTP request
        // httpClient.get("${ApiConstants.BASE_URL}/tasks")
        return emptyList() // Stub implementation
    }
    
    override suspend fun getTaskById(taskId: String): TaskDto? {
        Timber.d("API: getTaskById called (stub) for task: $taskId")
        return null // Stub implementation
    }
    
    override suspend fun getTasksBySprintId(sprintId: String): List<TaskDto> {
        Timber.d("API: getTasksBySprintId called (stub) for sprint: $sprintId")
        return emptyList() // Stub implementation
    }
    
    override suspend fun getTasksByDate(date: LocalDate): List<TaskDto> {
        Timber.d("API: getTasksByDate called (stub) for date: $date")
        return emptyList() // Stub implementation
    }
    
    override suspend fun createTask(task: TaskDto): TaskDto {
        Timber.d("API: createTask called (stub) for task: ${task.title}")
        return task.copy(id = "generated-id-${System.currentTimeMillis()}") // Stub implementation
    }
    
    override suspend fun updateTask(task: TaskDto): TaskDto {
        Timber.d("API: updateTask called (stub) for task: ${task.id}")
        return task // Stub implementation
    }
    
    override suspend fun deleteTask(taskId: String): Boolean {
        Timber.d("API: deleteTask called (stub) for task: $taskId")
        return true // Stub implementation
    }
    
    override suspend fun updateTaskStatus(taskId: String, status: TaskStatus): TaskDto {
        Timber.d("API: updateTaskStatus called (stub) for task: $taskId to status: $status")
        throw NotImplementedError("API updateTaskStatus not implemented yet") // Demonstrate error handling
    }
}
