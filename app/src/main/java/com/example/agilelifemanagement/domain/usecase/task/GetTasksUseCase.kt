package com.example.agilelifemanagement.domain.usecase.task

import com.example.agilelifemanagement.domain.model.Task
import com.example.agilelifemanagement.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for retrieving all tasks with optional filtering.
 */
class GetTasksUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    /**
     * Get all tasks.
     */
    operator fun invoke(): Flow<List<Task>> {
        return taskRepository.getTasks()
    }
    
    /**
     * Get tasks filtered by status.
     */
    fun byStatus(status: Task.Status): Flow<List<Task>> {
        return taskRepository.getTasksByStatus(status)
    }
    
    /**
     * Get tasks filtered by priority.
     */
    fun byPriority(priority: Task.Priority): Flow<List<Task>> {
        return taskRepository.getTasksByPriority(priority)
    }
    
    /**
     * Get tasks for a specific sprint.
     */
    fun bySprintId(sprintId: String): Flow<List<Task>> {
        return taskRepository.getTasksBySprintId(sprintId)
    }
    
    /**
     * Get tasks for a specific goal.
     */
    fun byGoalId(goalId: String): Flow<List<Task>> {
        return taskRepository.getTasksByGoalId(goalId)
    }
    
    /**
     * Get tasks with a specific tag.
     */
    fun byTag(tagId: String): Flow<List<Task>> {
        return taskRepository.getTasksByTag(tagId)
    }
}
