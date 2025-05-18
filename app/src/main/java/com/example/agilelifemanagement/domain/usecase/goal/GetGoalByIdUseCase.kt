package com.example.agilelifemanagement.domain.usecase.goal

import com.example.agilelifemanagement.domain.model.Goal
import com.example.agilelifemanagement.domain.repository.GoalRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import timber.log.Timber
import javax.inject.Inject

/**
 * Use case for retrieving a specific goal by ID.
 */
class GetGoalByIdUseCase @Inject constructor(
    private val goalRepository: GoalRepository
) {
    // Converting from the repository's suspend function returning Result<Goal> 
    // to a Flow<Goal?> for reactive UI consumption
    operator fun invoke(goalId: String): Flow<Goal?> = flow {
        // Execute the repository call
        val result = goalRepository.getGoalById(goalId)
        
        // Emit the goal if successful, otherwise emit null
        emit(result.getOrNull())
    }.catch { exception ->
        // Handle any exceptions during the flow and emit null
        Timber.e(exception, "Error getting goal: $goalId")
        emit(null)
    }
}
