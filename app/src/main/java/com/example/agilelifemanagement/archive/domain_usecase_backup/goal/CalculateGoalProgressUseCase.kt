package com.example.agilelifemanagement.domain.usecase.goal

import com.example.agilelifemanagement.domain.model.Result
import com.example.agilelifemanagement.domain.model.Task
import com.example.agilelifemanagement.domain.repository.GoalRepository
import com.example.agilelifemanagement.domain.repository.TaskRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/**
 * Use case for calculating goal progress based on completed tasks.
 */
class CalculateGoalProgressUseCase @Inject constructor(
    private val goalRepository: GoalRepository,
    private val taskRepository: TaskRepository
) {
    /**
     * Calculate goal progress based on completed tasks and update the goal.
     *
     * @param goalId The ID of the goal to calculate progress for.
     * @return Result containing the calculated progress (0.0 to 1.0) or an error.
     */
    suspend operator fun invoke(goalId: String): Result<Float> {
        return try {
            // Get the goal
            val goal = goalRepository.getGoalById(goalId).first()
                ?: return Result.Error("Goal not found")
            
            // Get all tasks for the goal
            val tasks = taskRepository.getTasksByGoalId(goalId).first()
            
            // If there are no tasks, progress is 0
            if (tasks.isEmpty()) {
                // Update goal with 0 progress
                val updatedGoal = goal.copy(progress = 0f)
                goalRepository.updateGoal(updatedGoal)
                return Result.Success(0f)
            }
            
            // Calculate progress based on completed tasks
            val completedTasks = tasks.count { it.status == Task.Status.DONE }
            val progress = completedTasks.toFloat() / tasks.size
            
            // Update goal with calculated progress
            val updatedGoal = goal.copy(
                progress = progress,
                isCompleted = progress >= 1.0f
            )
            goalRepository.updateGoal(updatedGoal)
            
            Result.Success(progress)
        } catch (e: Exception) {
            Result.Error("Failed to calculate goal progress: ${e.message}", e)
        }
    }
}
