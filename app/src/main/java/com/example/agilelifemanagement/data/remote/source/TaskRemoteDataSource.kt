package com.example.agilelifemanagement.data.remote.source

import com.example.agilelifemanagement.domain.model.Task
import com.example.agilelifemanagement.domain.model.TaskStatus
import java.time.LocalDate

/**
 * Remote data source interface for tasks.
 * Defines the contract for accessing and manipulating task data from remote sources.
 */
interface TaskRemoteDataSource {
    
    /**
     * Get all tasks from the remote source.
     * @return List of tasks
     */
    suspend fun getAllTasks(): List<Task>
    
    /**
     * Get a specific task by ID from the remote source.
     * @param taskId The task identifier
     * @return The task if found, or null
     */
    suspend fun getTaskById(taskId: String): Task?
    
    /**
     * Get tasks for a specific sprint from the remote source.
     * @param sprintId The sprint identifier
     * @return List of tasks in the specified sprint
     */
    suspend fun getTasksBySprintId(sprintId: String): List<Task>
    
    /**
     * Get tasks due on a specific date from the remote source.
     * @param date The date to filter by
     * @return List of tasks due on the specified date
     */
    suspend fun getTasksByDate(date: LocalDate): List<Task>
    
    /**
     * Create a new task in the remote source.
     * @param task The task to create
     * @return The created task with its assigned ID
     */
    suspend fun createTask(task: Task): Task
    
    /**
     * Update an existing task in the remote source.
     * @param task The updated task
     * @return The updated task
     */
    suspend fun updateTask(task: Task): Task
    
    /**
     * Delete a task from the remote source.
     * @param taskId The ID of the task to delete
     * @return True if the task was successfully deleted
     */
    suspend fun deleteTask(taskId: String): Boolean
    
    /**
     * Update the status of a task in the remote source.
     * @param taskId The ID of the task to update
     * @param status The new status
     * @return The updated task
     */
    suspend fun updateTaskStatus(taskId: String, status: TaskStatus): Task
}
