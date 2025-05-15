package com.example.agilelifemanagement.data.remote.api

import com.example.agilelifemanagement.data.remote.model.GoalDto
import com.example.agilelifemanagement.domain.model.GoalStatus
import io.ktor.client.HttpClient
import timber.log.Timber
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of [GoalApiService] that serves as a stub for future API integration.
 * This implementation provides placeholder functionality that will be replaced with
 * actual API calls in future iterations.
 */
@Singleton
class GoalApiServiceImpl @Inject constructor(
    private val httpClient: HttpClient
) : GoalApiService {
    
    override suspend fun getAllGoals(): List<GoalDto> {
        Timber.d("API: getAllGoals called (stub)")
        return emptyList() // Stub implementation
    }
    
    override suspend fun getGoalById(goalId: String): GoalDto? {
        Timber.d("API: getGoalById called (stub) for goal: $goalId")
        return null // Stub implementation
    }
    
    override suspend fun getGoalsByStatus(status: GoalStatus): List<GoalDto> {
        Timber.d("API: getGoalsByStatus called (stub) for status: $status")
        return emptyList() // Stub implementation
    }
    
    override suspend fun getUpcomingGoals(date: LocalDate): List<GoalDto> {
        Timber.d("API: getUpcomingGoals called (stub) for date: $date")
        return emptyList() // Stub implementation
    }
    
    override suspend fun createGoal(goal: GoalDto): GoalDto {
        Timber.d("API: createGoal called (stub) for goal: ${goal.title}")
        return goal.copy(id = "generated-id-${System.currentTimeMillis()}") // Stub implementation
    }
    
    override suspend fun updateGoal(goal: GoalDto): GoalDto {
        Timber.d("API: updateGoal called (stub) for goal: ${goal.id}")
        return goal // Stub implementation
    }
    
    override suspend fun deleteGoal(goalId: String): Boolean {
        Timber.d("API: deleteGoal called (stub) for goal: $goalId")
        return true // Stub implementation
    }
    
    override suspend fun updateGoalStatus(goalId: String, status: GoalStatus): GoalDto {
        Timber.d("API: updateGoalStatus called (stub) for goal: $goalId to status: $status")
        throw NotImplementedError("API updateGoalStatus not implemented yet") // Demonstrate error handling
    }
}
