package com.example.agilelifemanagement.domain.usecase.goal

import com.example.agilelifemanagement.domain.model.Goal
import com.example.agilelifemanagement.domain.model.Result
import com.example.agilelifemanagement.domain.repository.GoalRepository
import java.time.LocalDate
import javax.inject.Inject

/**
 * Use case for creating a new goal with validation.
 */
class CreateGoalUseCase @Inject constructor(
    private val goalRepository: GoalRepository
) {
    /**
     * Create a new goal with validation.
     *
     * @param title The title of the goal (required).
     * @param description The description of the goal (optional).
     * @param category The category of the goal (default: PERSONAL).
     * @param deadline The deadline of the goal (optional).
     * @param isCompleted Whether the goal is completed (default: false).
     * @return Result containing the ID of the created goal or an error.
     */
    suspend operator fun invoke(
        title: String,
        description: List<String> = emptyList(),
        tags: List<String> = emptyList(),
        category: Goal.Category = Goal.Category.PERSONAL,
        deadline: LocalDate? = null,
        isCompleted: Boolean = false
    ): Result<String> {
        // Validation
        if (title.isBlank()) {
            return Result.Error("Goal title cannot be empty")
        }
        
        // Create goal
        val goal = Goal(
            title = title,
            description = description,
            category = category,
            deadline = deadline,
            progress = if (isCompleted) 1f else 0f,
            isCompleted = isCompleted
        )
        
        return try {
            val goalId = goalRepository.insertGoal(goal)
            Result.Success(goalId)
        } catch (e: Exception) {
            Result.Error("Failed to create goal: ${e.message}", e)
        }
    }
}
