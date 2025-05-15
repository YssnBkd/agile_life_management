package com.example.agilelifemanagement.data.remote.source

import com.example.agilelifemanagement.domain.model.Sprint
import com.example.agilelifemanagement.domain.model.SprintReview
import java.time.LocalDate

/**
 * Remote data source interface for sprints.
 * Defines the contract for accessing and manipulating sprint data from remote sources.
 */
interface SprintRemoteDataSource {
    
    /**
     * Get all sprints from the remote source.
     * @return List of sprints
     */
    suspend fun getAllSprints(): List<Sprint>
    
    /**
     * Get a specific sprint by ID from the remote source.
     * @param sprintId The sprint identifier
     * @return The sprint if found, or null
     */
    suspend fun getSprintById(sprintId: String): Sprint?
    
    /**
     * Get the active sprint at a specific date from the remote source.
     * @param date The date to filter by
     * @return The active sprint, or null if none is active on that date
     */
    suspend fun getActiveSprintAtDate(date: LocalDate): Sprint?
    
    /**
     * Get sprints within a date range from the remote source.
     * @param startDate The start date (inclusive)
     * @param endDate The end date (inclusive)
     * @return List of sprints in the specified date range
     */
    suspend fun getSprintsInRange(startDate: LocalDate, endDate: LocalDate): List<Sprint>
    
    /**
     * Create a new sprint in the remote source.
     * @param sprint The sprint to create
     * @return The created sprint with its assigned ID
     */
    suspend fun createSprint(sprint: Sprint): Sprint
    
    /**
     * Update an existing sprint in the remote source.
     * @param sprint The updated sprint
     * @return The updated sprint
     */
    suspend fun updateSprint(sprint: Sprint): Sprint
    
    /**
     * Delete a sprint from the remote source.
     * @param sprintId The ID of the sprint to delete
     * @return True if the sprint was successfully deleted
     */
    suspend fun deleteSprint(sprintId: String): Boolean
    
    /**
     * Create a sprint review in the remote source.
     * @param sprintId The ID of the sprint to review
     * @param review The sprint review to create
     * @return The created review
     */
    suspend fun createSprintReview(sprintId: String, review: SprintReview): SprintReview
    
    /**
     * Get the sprint review for a specific sprint from the remote source.
     * @param sprintId The ID of the sprint to get the review for
     * @return The sprint review, or null if not found
     */
    suspend fun getSprintReview(sprintId: String): SprintReview?
}
