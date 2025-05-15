package com.example.agilelifemanagement.domain.usecase.goal

import com.example.agilelifemanagement.domain.model.Goal
import com.example.agilelifemanagement.domain.repository.GoalRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for retrieving a specific goal by ID.
 */
class GetGoalByIdUseCase @Inject constructor(
    private val goalRepository: GoalRepository
) {
    /**
     * Get a goal by its ID.
     *
     * @param id The unique identifier of the goal.
     * @return Flow emitting the goal if found, or null if not found.
     */
    operator fun invoke(id: String): Flow<Goal?> {
        return goalRepository.getGoalById(id)
    }
}
