package com.example.agilelifemanagement.domain.usecase.task

import com.example.agilelifemanagement.domain.model.Result
import com.example.agilelifemanagement.domain.repository.SprintRepository
import com.example.agilelifemanagement.domain.repository.TaskRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/**
 * Use case for assigning a task to a sprint with validation.
 */
class AssignTaskToSprintUseCase @Inject constructor(
    private val taskRepository: TaskRepository,
    private val sprintRepository: SprintRepository
) {
    /**
     * Assign a task to a sprint with validation.
     *
     * @param taskId The ID of the task to assign.
     * @param sprintId The ID of the sprint to assign the task to.
     * @return Result indicating success or an error.
     */
    suspend operator fun invoke(taskId: String, sprintId: String): Result<Unit> {
        return try {
            // Validate task
            val task = taskRepository.getTaskById(taskId).first()
                ?: return Result.Error("Task not found")
            
            // Validate sprint
            val sprint = sprintRepository.getSprintById(sprintId).first()
                ?: return Result.Error("Sprint not found")
            
            // Validate due date against sprint dates
            if (task.dueDate != null && sprint.endDate < task.dueDate) {
                return Result.Error("Task due date (${task.dueDate}) cannot be after sprint end date (${sprint.endDate})")
            }
            
            // Check if task is already in a sprint
            val currentSprintId = task.sprintId
            if (currentSprintId != null) {
                // Remove from current sprint if different
                if (currentSprintId != sprintId) {
                    taskRepository.removeTaskFromSprint(taskId, currentSprintId)
                } else {
                    // Already in this sprint
                    return Result.Success(Unit)
                }
            }
            
            // Assign task to sprint
            taskRepository.addTaskToSprint(taskId, sprintId)
            
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error("Failed to assign task to sprint: ${e.message}", e)
        }
    }
}
