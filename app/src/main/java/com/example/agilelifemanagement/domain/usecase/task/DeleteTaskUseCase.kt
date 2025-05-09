package com.example.agilelifemanagement.domain.usecase.task

import com.example.agilelifemanagement.domain.model.Result
import com.example.agilelifemanagement.domain.repository.TaskRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/**
 * Use case for deleting a task and its associations.
 */
class DeleteTaskUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    /**
     * Delete a task by its ID.
     *
     * @param id The ID of the task to delete.
     * @return Result indicating success or an error.
     */
    suspend operator fun invoke(id: String): Result<Unit> {
        return try {
            // Check if task exists
            val task = taskRepository.getTaskById(id).first()
                ?: return Result.Error("Task not found")
            
            // Delete the task (repository will handle associations)
            taskRepository.deleteTask(id)
            
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error("Failed to delete task: ${e.message}", e)
        }
    }
}
