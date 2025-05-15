package com.example.agilelifemanagement.data.remote.api

import com.example.agilelifemanagement.data.remote.model.GoalDto
import com.example.agilelifemanagement.domain.model.GoalStatus
import java.time.LocalDate

/**
 * Service interface for goal-related API operations.
 */
interface GoalApiService {
    
    /**
     * Get all goals from the API.
     * @return List of goal DTOs
     */
    suspend fun getAllGoals(): List<GoalDto>
    
    /**
     * Get a specific goal by ID from the API.
     * @param goalId The goal identifier
     * @return The goal DTO if found, or null
     */
    suspend fun getGoalById(goalId: String): GoalDto?
    
    /**
     * Get goals with a specific status from the API.
     * @param status The status to filter by
     * @return List of goal DTOs with the specified status
     */
    suspend fun getGoalsByStatus(status: GoalStatus): List<GoalDto>
    
    /**
     * Get upcoming goals with deadlines before or on a specific date from the API.
     * @param date The date to filter by
     * @return List of goal DTOs with deadlines before or on the specified date
     */
    suspend fun getUpcomingGoals(date: LocalDate): List<GoalDto>
    
    /**
     * Create a new goal in the API.
     * @param goal The goal DTO to create
     * @return The created goal DTO with its assigned ID
     */
    suspend fun createGoal(goal: GoalDto): GoalDto
    
    /**
     * Update an existing goal in the API.
     * @param goal The updated goal DTO
     * @return The updated goal DTO
     */
    suspend fun updateGoal(goal: GoalDto): GoalDto
    
    /**
     * Delete a goal from the API.
     * @param goalId The ID of the goal to delete
     * @return True if the goal was successfully deleted
     */
    suspend fun deleteGoal(goalId: String): Boolean
    
    /**
     * Update the status of a goal in the API.
     * @param goalId The ID of the goal to update
     * @param status The new status
     * @return The updated goal DTO
     */
    suspend fun updateGoalStatus(goalId: String, status: GoalStatus): GoalDto
}
