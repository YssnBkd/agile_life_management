package com.example.agilelifemanagement.data.local.source

import com.example.agilelifemanagement.data.local.dao.GoalDao
import com.example.agilelifemanagement.data.local.entity.GoalEntity
import com.example.agilelifemanagement.domain.model.GoalStatus
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import javax.inject.Inject

/**
 * Local data source for goals.
 * Uses Room DAO to perform database operations.
 */
class GoalLocalDataSource @Inject constructor(
    private val goalDao: GoalDao
) {
    /**
     * Get all goals as an observable flow.
     */
    fun observeGoals(): Flow<List<GoalEntity>> = goalDao.getAllGoals()
    
    /**
     * Get goals with a specific status.
     */
    fun observeGoalsByStatus(status: GoalStatus): Flow<List<GoalEntity>> = 
        goalDao.getGoalsByStatus(status)
    
    /**
     * Get upcoming goals with deadlines before or on a specific date.
     */
    fun observeUpcomingGoals(date: LocalDate): Flow<List<GoalEntity>> =
        goalDao.getUpcomingGoals(date)
    
    /**
     * Get a specific goal by ID.
     */
    suspend fun getGoalById(goalId: String): GoalEntity? = 
        goalDao.getGoalById(goalId)
    
    /**
     * Insert a goal.
     */
    suspend fun insertGoal(goal: GoalEntity) {
        goalDao.insertGoal(goal)
    }
    
    /**
     * Insert multiple goals.
     */
    suspend fun insertGoals(goals: List<GoalEntity>) {
        goalDao.insertGoals(goals)
    }
    
    /**
     * Update a goal.
     */
    suspend fun updateGoal(goal: GoalEntity): Int =
        goalDao.updateGoal(goal)
    
    /**
     * Update a goal's status.
     */
    suspend fun updateGoalStatus(goalId: String, status: GoalStatus): Int =
        goalDao.updateGoalStatus(goalId, status)
    
    /**
     * Delete a goal.
     */
    suspend fun deleteGoal(goalId: String): Int =
        goalDao.deleteGoal(goalId)
}
