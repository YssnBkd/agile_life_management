package com.example.agilelifemanagement.data.remote.source

import com.example.agilelifemanagement.domain.model.Goal
import com.example.agilelifemanagement.domain.model.GoalStatus
import timber.log.Timber
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of [GoalRemoteDataSource] that serves as a stub for future remote integration.
 * This implementation provides placeholder functionality that will be replaced with
 * actual remote API calls in future iterations.
 */
@Singleton
class GoalRemoteDataSourceImpl @Inject constructor() : GoalRemoteDataSource {
    
    override suspend fun getAllGoals(): List<Goal> {
        Timber.d("Remote: getAllGoals called (stub)")
        return emptyList() // Stub implementation
    }
    
    override suspend fun getGoalById(goalId: String): Goal? {
        Timber.d("Remote: getGoalById called (stub) for goal: $goalId")
        return null // Stub implementation
    }
    
    override suspend fun getGoalsByStatus(status: GoalStatus): List<Goal> {
        Timber.d("Remote: getGoalsByStatus called (stub) for status: $status")
        return emptyList() // Stub implementation
    }
    
    override suspend fun getUpcomingGoals(date: LocalDate): List<Goal> {
        Timber.d("Remote: getUpcomingGoals called (stub) for date: $date")
        return emptyList() // Stub implementation
    }
    
    override suspend fun createGoal(goal: Goal): Goal {
        Timber.d("Remote: createGoal called (stub) for goal: ${goal.title}")
        return goal // Stub implementation, just returns the input goal
    }
    
    override suspend fun updateGoal(goal: Goal): Goal {
        Timber.d("Remote: updateGoal called (stub) for goal: ${goal.id}")
        return goal // Stub implementation, just returns the input goal
    }
    
    override suspend fun deleteGoal(goalId: String): Boolean {
        Timber.d("Remote: deleteGoal called (stub) for goal: $goalId")
        return true // Stub implementation, pretend it always succeeds
    }
    
    override suspend fun updateGoalStatus(goalId: String, status: GoalStatus): Goal {
        Timber.d("Remote: updateGoalStatus called (stub) for goal: $goalId to status: $status")
        throw NotImplementedError("Remote updateGoalStatus not implemented yet") // Demonstrate error handling
    }
}
