package com.example.agilelifemanagement.data.remote.api

import com.example.agilelifemanagement.data.remote.model.TaskDto
import com.example.agilelifemanagement.domain.model.TaskStatus
import java.time.LocalDate

/**
 * Service interface for task-related API operations.
 */
interface TaskApiService {
    
    /**
     * Get all tasks from the API.
     * @return List of task DTOs
     */
    suspend fun getAllTasks(): List<TaskDto>
    
    /**
     * Get a specific task by ID from the API.
     * @param taskId The task identifier
     * @return The task DTO if found, or null
     */
    suspend fun getTaskById(taskId: String): TaskDto?
    
    /**
     * Get tasks for a specific sprint from the API.
     * @param sprintId The sprint identifier
     * @return List of task DTOs in the specified sprint
     */
    suspend fun getTasksBySprintId(sprintId: String): List<TaskDto>
    
    /**
     * Get tasks due on a specific date from the API.
     * @param date The date to filter by
     * @return List of task DTOs due on the specified date
     */
    suspend fun getTasksByDate(date: LocalDate): List<TaskDto>
    
    /**
     * Create a new task in the API.
     * @param task The task DTO to create
     * @return The created task DTO with its assigned ID
     */
    suspend fun createTask(task: TaskDto): TaskDto
    
    /**
     * Update an existing task in the API.
     * @param task The updated task DTO
     * @return The updated task DTO
     */
    suspend fun updateTask(task: TaskDto): TaskDto
    
    /**
     * Delete a task from the API.
     * @param taskId The ID of the task to delete
     * @return True if the task was successfully deleted
     */
    suspend fun deleteTask(taskId: String): Boolean
    
    /**
     * Update the status of a task in the API.
     * @param taskId The ID of the task to update
     * @param status The new status
     * @return The updated task DTO
     */
    suspend fun updateTaskStatus(taskId: String, status: TaskStatus): TaskDto
}
