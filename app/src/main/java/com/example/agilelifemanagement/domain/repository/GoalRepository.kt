package com.example.agilelifemanagement.domain.repository

import com.example.agilelifemanagement.domain.model.Goal
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

/**
 * Repository interface for Goal operations.
 */
interface GoalRepository {
    fun getGoals(): Flow<List<Goal>>
    fun getGoalById(id: String): Flow<Goal?>
    fun getGoalsByCategory(category: Goal.Category): Flow<List<Goal>>
    fun getGoalsByDeadline(deadline: LocalDate): Flow<List<Goal>>
    fun getGoalsBySprintId(sprintId: String): Flow<List<Goal>>
    suspend fun insertGoal(goal: Goal): String
    suspend fun updateGoal(goal: Goal)
    suspend fun deleteGoal(id: String)
    suspend fun addGoalToSprint(goalId: String, sprintId: String)
    suspend fun removeGoalFromSprint(goalId: String, sprintId: String)
}
