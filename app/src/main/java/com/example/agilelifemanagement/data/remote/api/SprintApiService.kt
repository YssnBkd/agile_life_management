package com.example.agilelifemanagement.data.remote.api

import com.example.agilelifemanagement.data.remote.model.SprintDto
import com.example.agilelifemanagement.data.remote.model.SprintReviewDto
import java.time.LocalDate

/**
 * Service interface for sprint-related API operations.
 */
interface SprintApiService {
    
    /**
     * Get all sprints from the API.
     * @return List of sprint DTOs
     */
    suspend fun getAllSprints(): List<SprintDto>
    
    /**
     * Get a specific sprint by ID from the API.
     * @param sprintId The sprint identifier
     * @return The sprint DTO if found, or null
     */
    suspend fun getSprintById(sprintId: String): SprintDto?
    
    /**
     * Get the active sprint at a specific date from the API.
     * @param date The date to filter by
     * @return The active sprint DTO, or null if none is active on that date
     */
    suspend fun getActiveSprintAtDate(date: LocalDate): SprintDto?
    
    /**
     * Get sprints within a date range from the API.
     * @param startDate The start date (inclusive)
     * @param endDate The end date (inclusive)
     * @return List of sprint DTOs in the specified date range
     */
    suspend fun getSprintsInRange(startDate: LocalDate, endDate: LocalDate): List<SprintDto>
    
    /**
     * Create a new sprint in the API.
     * @param sprint The sprint DTO to create
     * @return The created sprint DTO with its assigned ID
     */
    suspend fun createSprint(sprint: SprintDto): SprintDto
    
    /**
     * Update an existing sprint in the API.
     * @param sprint The updated sprint DTO
     * @return The updated sprint DTO
     */
    suspend fun updateSprint(sprint: SprintDto): SprintDto
    
    /**
     * Delete a sprint from the API.
     * @param sprintId The ID of the sprint to delete
     * @return True if the sprint was successfully deleted
     */
    suspend fun deleteSprint(sprintId: String): Boolean
    
    /**
     * Create a sprint review in the API.
     * @param sprintId The ID of the sprint to review
     * @param review The sprint review DTO to create
     * @return The created review DTO
     */
    suspend fun createSprintReview(sprintId: String, review: SprintReviewDto): SprintReviewDto
    
    /**
     * Get the sprint review for a specific sprint from the API.
     * @param sprintId The ID of the sprint to get the review for
     * @return The sprint review DTO, or null if not found
     */
    suspend fun getSprintReview(sprintId: String): SprintReviewDto?
}
