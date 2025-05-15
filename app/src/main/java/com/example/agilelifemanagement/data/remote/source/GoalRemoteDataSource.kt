package com.example.agilelifemanagement.data.remote.source

import com.example.agilelifemanagement.domain.model.Goal
import com.example.agilelifemanagement.domain.model.GoalStatus
import java.time.LocalDate

/**
 * Remote data source interface for goals.
 * Defines the contract for accessing and manipulating goal data from remote sources.
 */
interface GoalRemoteDataSource {
    
    /**
     * Get all goals from the remote source.
     * @return List of goals
     */
    suspend fun getAllGoals(): List<Goal>
    
    /**
     * Get a specific goal by ID from the remote source.
     * @param goalId The goal identifier
     * @return The goal if found, or null
     */
    suspend fun getGoalById(goalId: String): Goal?
    
    /**
     * Get goals with a specific status from the remote source.
     * @param status The status to filter by
     * @return List of goals with the specified status
     */
    suspend fun getGoalsByStatus(status: GoalStatus): List<Goal>
    
    /**
     * Get upcoming goals with deadlines before or on a specific date from the remote source.
     * @param date The date to filter by
     * @return List of goals with deadlines before or on the specified date
     */
    suspend fun getUpcomingGoals(date: LocalDate): List<Goal>
    
    /**
     * Create a new goal in the remote source.
     * @param goal The goal to create
     * @return The created goal with its assigned ID
     */
    suspend fun createGoal(goal: Goal): Goal
    
    /**
     * Update an existing goal in the remote source.
     * @param goal The updated goal
     * @return The updated goal
     */
    suspend fun updateGoal(goal: Goal): Goal
    
    /**
     * Delete a goal from the remote source.
     * @param goalId The ID of the goal to delete
     * @return True if the goal was successfully deleted
     */
    suspend fun deleteGoal(goalId: String): Boolean
    
    /**
     * Update the status of a goal in the remote source.
     * @param goalId The ID of the goal to update
     * @param status The new status
     * @return The updated goal
     */
    suspend fun updateGoalStatus(goalId: String, status: GoalStatus): Goal
}
