package com.example.agilelifemanagement.domain.repository

import com.example.agilelifemanagement.domain.model.Goal
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for Goal operations.
 * Defines the contract for accessing and manipulating goal data.
 */
interface GoalRepository {
    /**
     * Get all goals as an observable Flow.
     * @return A Flow emitting lists of all goals when changes occur
     */
    fun getAllGoals(): Flow<List<Goal>>
    
    /**
     * Get a specific goal by ID.
     * @param goalId The goal identifier
     * @return A Result containing the goal if found, or an error if not
     */
    suspend fun getGoalById(goalId: String): Result<Goal>
    
    /**
     * Create a new goal.
     * @param goal The goal to create
     * @return A Result containing the created goal with its assigned ID
     */
    suspend fun createGoal(goal: Goal): Result<Goal>
    
    /**
     * Update an existing goal.
     * @param goal The updated goal
     * @return A Result containing the updated goal if successful
     */
    suspend fun updateGoal(goal: Goal): Result<Goal>
    
    /**
     * Delete a goal.
     * @param goalId The ID of the goal to delete
     * @return A Result containing a boolean indicating success
     */
    suspend fun deleteGoal(goalId: String): Result<Boolean>
}
