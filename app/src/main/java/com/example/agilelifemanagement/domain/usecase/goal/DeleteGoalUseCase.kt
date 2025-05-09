package com.example.agilelifemanagement.domain.usecase.goal

import com.example.agilelifemanagement.domain.model.Result
import com.example.agilelifemanagement.domain.repository.GoalRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/**
 * Use case for deleting a goal and handling associated entities.
 */
class DeleteGoalUseCase @Inject constructor(
    private val goalRepository: GoalRepository
) {
    /**
     * Delete a goal by its ID.
     *
     * @param id The ID of the goal to delete.
     * @return Result indicating success or an error.
     */
    suspend operator fun invoke(id: String): Result<Unit> {
        return try {
            // Check if goal exists
            val goal = goalRepository.getGoalById(id).first()
                ?: return Result.Error("Goal not found")
            
            // Delete the goal (repository will handle associations)
            goalRepository.deleteGoal(id)
            
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error("Failed to delete goal: ${e.message}", e)
        }
    }
}
