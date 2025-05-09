package com.example.agilelifemanagement.domain.usecase.goal

import com.example.agilelifemanagement.domain.model.Goal
import com.example.agilelifemanagement.domain.model.Result
import com.example.agilelifemanagement.domain.repository.GoalRepository
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import javax.inject.Inject

/**
 * Use case for updating an existing goal with validation.
 */
class UpdateGoalUseCase @Inject constructor(
    private val goalRepository: GoalRepository
) {
    /**
     * Update an existing goal with validation.
     *
     * @param id The ID of the goal to update (required).
     * @param title The updated title of the goal (required).
     * @param description The updated description of the goal (optional).
     * @param category The updated category of the goal.
     * @param deadline The updated deadline of the goal (optional).
     * @param progress The updated progress of the goal (0.0 to 1.0).
     * @param isCompleted The updated completion status of the goal.
     * @return Result indicating success or an error.
     */
    suspend operator fun invoke(
        id: String,
        title: String,
        tags: List<String> = emptyList(),
        description: List<String>,
        category: Goal.Category,
        deadline: LocalDate?,
        progress: Float,
        isCompleted: Boolean
    ): Result<Unit> {
        // Validation
        if (title.isBlank()) {
            return Result.Error("Goal title cannot be empty")
        }
        
        if (progress < 0f || progress > 1f) {
            return Result.Error("Progress must be between 0 and 1")
        }
        
        // Check if goal exists
        val existingGoal = goalRepository.getGoalById(id).first()
            ?: return Result.Error("Goal not found")
        
        // Create updated goal
        val updatedGoal = Goal(
            id = id,
            title = title,
            description = description,
            category = category,
            deadline = deadline,
            progress = if (isCompleted) 1f else progress,
            isCompleted = isCompleted
        )
        
        return try {
            goalRepository.updateGoal(updatedGoal)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error("Failed to update goal: ${e.message}", e)
        }
    }
}
