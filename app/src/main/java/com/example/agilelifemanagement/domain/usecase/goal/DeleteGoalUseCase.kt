package com.example.agilelifemanagement.domain.usecase.goal

import com.example.agilelifemanagement.domain.repository.GoalRepository
import javax.inject.Inject

/**
 * Use case for deleting a goal.
 * 
 * This use case follows the offline-first approach, ensuring the goal is deleted
 * from local storage immediately and then synchronizing with remote storage in the background.
 */
class DeleteGoalUseCase @Inject constructor(
    private val goalRepository: GoalRepository
) {
    /**
     * Deletes a goal with the given ID.
     * 
     * @param goalId The unique identifier of the goal to delete
     * @return Result indicating success or error with details
     */
    suspend operator fun invoke(goalId: String): Result<Unit> {
        // Map the Boolean result to Unit for clients that don't need the boolean value
        return goalRepository.deleteGoal(goalId).map { Unit }
    }
}
