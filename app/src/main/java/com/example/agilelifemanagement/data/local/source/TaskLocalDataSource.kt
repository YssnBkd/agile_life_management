package com.example.agilelifemanagement.data.local.source

import com.example.agilelifemanagement.data.local.dao.TaskDao
import com.example.agilelifemanagement.data.local.dao.TagDao
import com.example.agilelifemanagement.data.local.entity.TaskEntity
import com.example.agilelifemanagement.domain.model.TaskStatus
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import javax.inject.Inject

/**
 * Local data source for tasks.
 * Uses Room DAOs to perform database operations.
 */
class TaskLocalDataSource @Inject constructor(
    private val taskDao: TaskDao,
    private val tagDao: TagDao
) {
    /**
     * Get all tasks as an observable flow.
     */
    fun observeTasks(): Flow<List<TaskEntity>> = taskDao.getAllTasks()
    
    /**
     * Get tasks for a specific sprint.
     */
    fun observeTasksBySprintId(sprintId: String): Flow<List<TaskEntity>> = 
        taskDao.getTasksBySprintId(sprintId)
    
    /**
     * Get tasks for a specific date.
     */
    fun observeTasksByDate(date: LocalDate): Flow<List<TaskEntity>> = 
        taskDao.getTasksByDate(date)
    
    /**
     * Get tasks for a date range.
     */
    fun observeTasksByDateRange(startDate: LocalDate, endDate: LocalDate): Flow<List<TaskEntity>> = 
        taskDao.getTasksByDateRange(startDate, endDate)
    
    /**
     * Get tasks with specific tags.
     */
    fun observeTasksByTags(tagIds: List<String>): Flow<List<TaskEntity>> = 
        taskDao.getTasksByTags(tagIds)
    
    /**
     * Get a specific task by ID.
     */
    suspend fun getTaskById(taskId: String): TaskEntity? = 
        taskDao.getTaskById(taskId)
    
    /**
     * Insert a task.
     */
    suspend fun insertTask(task: TaskEntity, tagIds: List<String> = emptyList()) {
        taskDao.insertTaskWithTags(task, tagIds)
    }
    
    /**
     * Insert multiple tasks.
     */
    suspend fun insertTasks(tasks: List<TaskEntity>) {
        taskDao.insertTasks(tasks)
    }
    
    /**
     * Update a task.
     */
    suspend fun updateTask(task: TaskEntity, tagIds: List<String> = emptyList()): Int {
        taskDao.updateTaskWithTags(task, tagIds)
        return 1 // Return value to match repository signature
    }
    
    /**
     * Update a task's status.
     */
    suspend fun updateTaskStatus(taskId: String, status: TaskStatus): Int =
        taskDao.updateTaskStatus(taskId, status)
    
    /**
     * Delete a task.
     */
    suspend fun deleteTask(taskId: String): Int =
        taskDao.deleteTask(taskId)
}
