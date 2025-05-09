package com.example.agilelifemanagement.domain.usecase.goal

import com.example.agilelifemanagement.domain.model.Goal
import com.example.agilelifemanagement.domain.repository.GoalRepository
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import javax.inject.Inject

/**
 * Use case for retrieving all goals with optional filtering.
 */
class GetGoalsUseCase @Inject constructor(
    private val goalRepository: GoalRepository
) {
    /**
     * Get all goals.
     */
    operator fun invoke(): Flow<List<Goal>> {
        return goalRepository.getGoals()
    }
    
    /**
     * Get goals filtered by category.
     */
    fun byCategory(category: Goal.Category): Flow<List<Goal>> {
        return goalRepository.getGoalsByCategory(category)
    }
    
    /**
     * Get goals with a deadline on or before the specified date.
     */
    fun byDeadline(deadline: LocalDate): Flow<List<Goal>> {
        return goalRepository.getGoalsByDeadline(deadline)
    }
    
    /**
     * Get goals for a specific sprint.
     */
    fun bySprintId(sprintId: String): Flow<List<Goal>> {
        return goalRepository.getGoalsBySprintId(sprintId)
    }
}
