package com.example.agilelifemanagement.data.remote.api

import com.example.agilelifemanagement.data.remote.model.DayActivityDto
import java.time.LocalDate

/**
 * Service interface for day activity-related API operations.
 */
interface DayActivityApiService {
    
    /**
     * Get all activities for a specific date from the API.
     * @param date The date to get activities for
     * @return List of activity DTOs for the specified date
     */
    suspend fun getActivitiesForDate(date: LocalDate): List<DayActivityDto>
    
    /**
     * Get all activities in a date range from the API.
     * @param startDate The start date (inclusive)
     * @param endDate The end date (inclusive)
     * @return List of activity DTOs in the specified date range
     */
    suspend fun getActivitiesInRange(startDate: LocalDate, endDate: LocalDate): List<DayActivityDto>
    
    /**
     * Get a specific activity by ID from the API.
     * @param activityId The activity identifier
     * @return The activity DTO if found, or null
     */
    suspend fun getActivityById(activityId: String): DayActivityDto?
    
    /**
     * Create a new activity in the API.
     * @param activity The activity DTO to create
     * @return The created activity DTO with its assigned ID
     */
    suspend fun createActivity(activity: DayActivityDto): DayActivityDto
    
    /**
     * Update an existing activity in the API.
     * @param activity The updated activity DTO
     * @return The updated activity DTO
     */
    suspend fun updateActivity(activity: DayActivityDto): DayActivityDto
    
    /**
     * Delete an activity from the API.
     * @param activityId The ID of the activity to delete
     * @return True if the activity was successfully deleted
     */
    suspend fun deleteActivity(activityId: String): Boolean
    
    /**
     * Toggle the completion status of an activity in the API.
     * @param activityId The ID of the activity to toggle
     * @param completed The new completion status
     * @return The updated activity DTO
     */
    suspend fun toggleActivityCompletion(activityId: String, completed: Boolean): DayActivityDto
}
