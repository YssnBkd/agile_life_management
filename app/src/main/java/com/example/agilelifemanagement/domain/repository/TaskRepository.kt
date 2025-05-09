package com.example.agilelifemanagement.domain.repository

import com.example.agilelifemanagement.domain.model.Task
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

/**
 * Repository interface for Task operations.
 */
interface TaskRepository {
    fun getTasks(): Flow<List<Task>>
    fun getTaskById(id: String): Flow<Task?>
    fun getTasksByStatus(status: Task.Status): Flow<List<Task>>
    fun getTasksByPriority(priority: Task.Priority): Flow<List<Task>>
    fun getTasksByDueDate(dueDate: LocalDate): Flow<List<Task>>
    fun getTasksBySprintId(sprintId: String): Flow<List<Task>>
    fun getTasksByGoalId(goalId: String): Flow<List<Task>>
    fun getTasksByTag(tagId: String): Flow<List<Task>>
    suspend fun insertTask(task: Task): String
    suspend fun updateTask(task: Task)
    suspend fun deleteTask(id: String)
    suspend fun addTaskToSprint(taskId: String, sprintId: String)
    suspend fun removeTaskFromSprint(taskId: String, sprintId: String)
    suspend fun addTaskToGoal(taskId: String, goalId: String)
    suspend fun removeTaskFromGoal(taskId: String, goalId: String)
    suspend fun addTagToTask(taskId: String, tagId: String)
    suspend fun removeTagFromTask(taskId: String, tagId: String)
    suspend fun addTaskDependency(taskId: String, dependsOnTaskId: String)
    suspend fun removeTaskDependency(taskId: String, dependsOnTaskId: String)
}
