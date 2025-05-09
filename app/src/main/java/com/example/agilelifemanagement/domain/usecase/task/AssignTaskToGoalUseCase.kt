package com.example.agilelifemanagement.domain.usecase.task

import com.example.agilelifemanagement.domain.model.Result
import com.example.agilelifemanagement.domain.repository.GoalRepository
import com.example.agilelifemanagement.domain.repository.TaskRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/**
 * Use case for assigning a task to a goal with validation.
 */
class AssignTaskToGoalUseCase @Inject constructor(
    private val taskRepository: TaskRepository,
    private val goalRepository: GoalRepository
) {
    /**
     * Assign a task to a goal with validation.
     *
     * @param taskId The ID of the task to assign.
     * @param goalId The ID of the goal to assign the task to.
     * @return Result indicating success or an error.
     */
    suspend operator fun invoke(taskId: String, goalId: String): Result<Unit> {
        return try {
            // Validate task
            val task = taskRepository.getTaskById(taskId).first()
                ?: return Result.Error("Task not found")
            
            // Validate goal
            val goal = goalRepository.getGoalById(goalId).first()
                ?: return Result.Error("Goal not found")
            
            // Check if task is already assigned to a goal
            val currentGoalId = task.goalId
            if (currentGoalId != null) {
                // Remove from current goal if different
                if (currentGoalId != goalId) {
                    taskRepository.removeTaskFromGoal(taskId, currentGoalId)
                } else {
                    // Already assigned to this goal
                    return Result.Success(Unit)
                }
            }
            
            // Assign task to goal
            taskRepository.addTaskToGoal(taskId, goalId)
            
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error("Failed to assign task to goal: ${e.message}", e)
        }
    }
}
