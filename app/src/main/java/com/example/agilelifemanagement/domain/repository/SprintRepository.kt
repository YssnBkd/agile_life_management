package com.example.agilelifemanagement.domain.repository

import com.example.agilelifemanagement.domain.model.Sprint
import com.example.agilelifemanagement.domain.model.SprintReview
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for Sprint operations.
 * Defines the contract for accessing and manipulating sprint data.
 */

interface SprintRepository {
    /**
     * Get all sprints as an observable Flow.
     * @return A Flow emitting lists of all sprints when changes occur
     */
    fun getAllSprints(): Flow<List<Sprint>>
    
    /**
     * Get a specific sprint by ID.
     * @param sprintId The sprint identifier
     * @return A Result containing the sprint if found, or an error if not
     */
    suspend fun getSprintById(sprintId: String): Result<Sprint>
    
    /**
     * Create a new sprint.
     * @param sprint The sprint to create
     * @return A Result containing the created sprint with its assigned ID
     */
    suspend fun createSprint(sprint: Sprint): Result<Sprint>
    
    /**
     * Update an existing sprint.
     * @param sprint The updated sprint
     * @return A Result containing the updated sprint if successful
     */
    suspend fun updateSprint(sprint: Sprint): Result<Sprint>
    
    /**
     * Delete a sprint.
     * @param sprintId The ID of the sprint to delete
     * @return A Result containing a boolean indicating success
     */
    suspend fun deleteSprint(sprintId: String): Result<Boolean>
    
    /**
     * Create a new sprint review.
     * @param sprintReview The sprint review to create
     * @return A Result containing the created sprint review with its assigned ID
     */
    suspend fun createSprintReview(sprintReview: SprintReview): Result<SprintReview>
    
    /**
     * Get a sprint review by sprint ID.
     * @param sprintId The ID of the sprint
     * @return A Flow emitting the sprint review for the specified sprint, or null if none exists
     */
    fun getSprintReviewBySprintId(sprintId: String): Flow<SprintReview?>
}
