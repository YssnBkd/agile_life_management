package com.example.agilelifemanagement.domain.repository

import com.example.agilelifemanagement.domain.model.Task
import com.example.agilelifemanagement.domain.model.TaskStatus
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

/**
 * Repository interface for Task operations.
 * Defines the contract for accessing and manipulating task data.
 */
interface TaskRepository {
    /**
     * Get all tasks as an observable Flow.
     * @return A Flow emitting lists of all tasks when changes occur
     */
    fun getAllTasks(): Flow<List<Task>>
    
    /**
     * Get tasks for a specific sprint.
     * @param sprintId The sprint identifier
     * @return A Flow emitting lists of tasks in the specified sprint
     */
    fun getTasksBySprintId(sprintId: String): Flow<List<Task>>
    
    /**
     * Get a specific task by ID.
     * @param taskId The task identifier
     * @return A Result containing the task if found, or an error if not
     */
    suspend fun getTaskById(taskId: String): Result<Task>
    
    /**
     * Get tasks due within a specific date range.
     * @param date The date to filter by
     * @return A Flow emitting lists of tasks due on the specified date
     */
    fun getTasksByDate(date: LocalDate): Flow<List<Task>>
    
    /**
     * Create a new task.
     * @param task The task to create
     * @return A Result containing the created task with its assigned ID
     */
    suspend fun createTask(task: Task): Result<Task>
    
    /**
     * Update an existing task.
     * @param task The updated task
     * @return A Result containing the updated task if successful
     */
    suspend fun updateTask(task: Task): Result<Task>
    
    /**
     * Update the status of a task.
     * @param taskId The ID of the task to update
     * @param status The new status
     * @return A Result containing the updated task if successful
     */
    suspend fun updateTaskStatus(taskId: String, status: TaskStatus): Result<Task>
    
    /**
     * Delete a task.
     * @param taskId The ID of the task to delete
     * @return A Result containing a boolean indicating success
     */
    suspend fun deleteTask(taskId: String): Result<Boolean>
    
    /**
     * Get tasks filtered by tags.
     * @param tags List of tag names to filter by
     * @return A Flow emitting lists of tasks with the specified tags
     */
    fun getTasksByTags(tags: List<String>): Flow<List<Task>>
}
