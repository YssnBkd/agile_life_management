package com.example.agilelifemanagement.domain.usecase.task

import com.example.agilelifemanagement.domain.model.Result
import com.example.agilelifemanagement.domain.repository.TaskRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/**
 * Use case for managing task dependencies with validation.
 */
class ManageTaskDependenciesUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    /**
     * Add a dependency between tasks with validation.
     *
     * @param taskId The ID of the task that depends on another task.
     * @param dependsOnTaskId The ID of the task that is depended upon.
     * @return Result indicating success or an error.
     */
    suspend fun addDependency(taskId: String, dependsOnTaskId: String): Result<Unit> {
        return try {
            // Validate tasks
            val task = taskRepository.getTaskById(taskId).first()
                ?: return Result.Error("Task not found")
            
            val dependsOnTask = taskRepository.getTaskById(dependsOnTaskId).first()
                ?: return Result.Error("Dependency task not found")
            
            // Prevent self-dependency
            if (taskId == dependsOnTaskId) {
                return Result.Error("A task cannot depend on itself")
            }
            
            // TODO: Check for circular dependencies (would require a more complex algorithm)
            
            // Add dependency
            taskRepository.addTaskDependency(taskId, dependsOnTaskId)
            
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error("Failed to add task dependency: ${e.message}", e)
        }
    }
    
    /**
     * Remove a dependency between tasks.
     *
     * @param taskId The ID of the task that depends on another task.
     * @param dependsOnTaskId The ID of the task that is depended upon.
     * @return Result indicating success or an error.
     */
    suspend fun removeDependency(taskId: String, dependsOnTaskId: String): Result<Unit> {
        return try {
            // Validate tasks
            val task = taskRepository.getTaskById(taskId).first()
                ?: return Result.Error("Task not found")
            
            val dependsOnTask = taskRepository.getTaskById(dependsOnTaskId).first()
                ?: return Result.Error("Dependency task not found")
            
            // Remove dependency
            taskRepository.removeTaskDependency(taskId, dependsOnTaskId)
            
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error("Failed to remove task dependency: ${e.message}", e)
        }
    }
}
