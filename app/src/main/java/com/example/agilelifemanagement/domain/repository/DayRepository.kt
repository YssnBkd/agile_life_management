package com.example.agilelifemanagement.domain.repository

import com.example.agilelifemanagement.domain.model.DayActivity
import com.example.agilelifemanagement.domain.model.DayActivityTemplate
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

/**
 * Repository interface for day activity operations.
 * Manages day activities and related operations.
 */
interface DayRepository {
    /**
     * Get activities for a specific date.
     * @param date The date to get activities for
     * @return A Flow emitting lists of activities scheduled for the specified date
     */
    fun getActivitiesByDate(date: LocalDate): Flow<List<DayActivity>>
    
    /**
     * Get activities for a date range.
     * @param startDate The start date (inclusive)
     * @param endDate The end date (inclusive)
     * @return A Flow emitting lists of activities within the date range
     */
    fun getActivitiesForDateRange(startDate: LocalDate, endDate: LocalDate): Flow<List<DayActivity>>
    
    /**
     * Get a specific activity by ID.
     * @param activityId The activity identifier
     * @return A Result containing the activity if found, or an error if not
     */
    suspend fun getActivityById(activityId: String): Result<DayActivity>
    
    /**
     * Add a new activity.
     * @param activity The activity to add
     * @return A Result containing the added activity with its assigned ID
     */
    suspend fun addActivity(activity: DayActivity): Result<DayActivity>
    
    /**
     * Update an existing activity.
     * @param activity The updated activity
     * @return A Result containing the updated activity if successful
     */
    suspend fun updateActivity(activity: DayActivity): Result<DayActivity>
    
    /**
     * Delete an activity.
     * @param activityId The ID of the activity to delete
     * @return A Result containing a boolean indicating success
     */
    suspend fun deleteActivity(activityId: String): Result<Boolean>
    
    /**
     * Toggle the completion status of an activity.
     * @param activityId The ID of the activity to toggle
     * @return A Result containing the updated activity
     */
    suspend fun toggleActivityCompletion(activityId: String): Result<DayActivity>
    
    /**
     * Get all activity templates.
     * @return A Flow emitting lists of all activity templates
     */
    fun getAllActivityTemplates(): Flow<List<DayActivityTemplate>>
    
    /**
     * Get a specific activity template by ID.
     * @param templateId The template identifier
     * @return A Result containing the template if found, or an error if not
     */
    suspend fun getActivityTemplateById(templateId: String): Result<DayActivityTemplate>
    
    /**
     * Create a new activity template.
     * @param template The template to create
     * @return A Result containing the created template with its assigned ID
     */
    suspend fun createActivityTemplate(template: DayActivityTemplate): Result<DayActivityTemplate>
    
    /**
     * Update an existing activity template.
     * @param template The updated template
     * @return A Result containing the updated template if successful
     */
    suspend fun updateActivityTemplate(template: DayActivityTemplate): Result<DayActivityTemplate>
    
    /**
     * Delete an activity template.
     * @param templateId The ID of the template to delete
     * @return A Result containing a boolean indicating success
     */
    suspend fun deleteActivityTemplate(templateId: String): Result<Boolean>
}
