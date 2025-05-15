package com.example.agilelifemanagement.domain.usecase.goal

import com.example.agilelifemanagement.domain.model.Goal
import com.example.agilelifemanagement.domain.repository.GoalRepository
import javax.inject.Inject

/**
 * Use case for creating a new goal.
 * 
 * This use case follows the offline-first approach by immediately saving to local storage
 * and synchronizing with remote sources in the background.
 */
class CreateGoalUseCase @Inject constructor(
    private val goalRepository: GoalRepository
) {
    /**
     * Creates a new goal in the system.
     * 
     * @param goal The goal to be created
     * @return Result containing the created goal with its generated ID if successful, or an error
     */
    suspend operator fun invoke(goal: Goal): Result<Goal> {
        return goalRepository.createGoal(goal)
    }
}
