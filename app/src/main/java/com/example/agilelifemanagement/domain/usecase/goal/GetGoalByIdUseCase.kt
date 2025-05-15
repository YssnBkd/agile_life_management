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
    operator fun invoke(goalId: String): Flow<Goal?> {
        return goalRepository.getGoalById(goalId)
    }
}
