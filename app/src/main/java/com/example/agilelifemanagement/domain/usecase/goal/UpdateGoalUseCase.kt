package com.example.agilelifemanagement.domain.usecase.goal

import com.example.agilelifemanagement.domain.model.Goal
import com.example.agilelifemanagement.domain.repository.GoalRepository
import javax.inject.Inject

/**
 * Use case for updating an existing goal.
 * 
 * This use case follows the offline-first approach, updating the goal in local storage
 * immediately and then synchronizing with remote storage in the background.
 */
class UpdateGoalUseCase @Inject constructor(
    private val goalRepository: GoalRepository
) {
    /**
     * Updates an existing goal with new information.
     * 
     * @param goal The goal with updated information
     * @return Result containing the updated goal or error details
     */
    suspend operator fun invoke(goal: Goal): Result<Goal> {
        return goalRepository.updateGoal(goal)
    }
}
