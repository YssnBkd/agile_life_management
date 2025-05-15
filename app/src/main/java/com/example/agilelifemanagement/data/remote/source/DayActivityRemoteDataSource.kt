package com.example.agilelifemanagement.data.remote.source

import com.example.agilelifemanagement.domain.model.DayActivity
import java.time.LocalDate

/**
 * Remote data source interface for day activities.
 * Defines the contract for accessing and manipulating day activity data from remote sources.
 */
interface DayActivityRemoteDataSource {
    
    /**
     * Get all activities for a specific date from the remote source.
     * @param date The date to get activities for
     * @return List of activities for the specified date
     */
    suspend fun getActivitiesForDate(date: LocalDate): List<DayActivity>
    
    /**
     * Get all activities in a date range from the remote source.
     * @param startDate The start date (inclusive)
     * @param endDate The end date (inclusive)
     * @return List of activities in the specified date range
     */
    suspend fun getActivitiesInRange(startDate: LocalDate, endDate: LocalDate): List<DayActivity>
    
    /**
     * Get a specific activity by ID from the remote source.
     * @param activityId The activity identifier
     * @return The activity if found, or null
     */
    suspend fun getActivityById(activityId: String): DayActivity?
    
    /**
     * Create a new activity in the remote source.
     * @param activity The activity to create
     * @return The created activity with its assigned ID
     */
    suspend fun createActivity(activity: DayActivity): DayActivity
    
    /**
     * Update an existing activity in the remote source.
     * @param activity The updated activity
     * @return The updated activity
     */
    suspend fun updateActivity(activity: DayActivity): DayActivity
    
    /**
     * Delete an activity from the remote source.
     * @param activityId The ID of the activity to delete
     * @return True if the activity was successfully deleted
     */
    suspend fun deleteActivity(activityId: String): Boolean
    
    /**
     * Toggle the completion status of an activity in the remote source.
     * @param activityId The ID of the activity to toggle
     * @param completed The new completion status
     * @return The updated activity
     */
    suspend fun toggleActivityCompletion(activityId: String, completed: Boolean): DayActivity
}
