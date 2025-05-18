package com.example.agilelifemanagement.data.remote.api

import com.example.agilelifemanagement.domain.model.DayActivity
import com.example.agilelifemanagement.domain.model.DaySchedule
import java.time.LocalDate

/**
 * API service interface for day-related operations.
 * This interface defines the contract for communicating with the remote API
 * for day schedules and activities.
 */
interface DayApiService {
    /**
     * Get a schedule for a specific date.
     */
    suspend fun getScheduleByDate(date: LocalDate): DaySchedule?
    
    /**
     * Get schedules for a date range.
     */
    suspend fun getSchedulesForDateRange(startDate: LocalDate, endDate: LocalDate): List<DaySchedule>
    
    /**
     * Get a specific schedule by ID.
     */
    suspend fun getScheduleById(scheduleId: String): DaySchedule?
    
    /**
     * Create a new schedule.
     */
    suspend fun createSchedule(schedule: DaySchedule): DaySchedule
    
    /**
     * Update an existing schedule.
     */
    suspend fun updateSchedule(schedule: DaySchedule): DaySchedule
    
    /**
     * Delete a schedule.
     */
    suspend fun deleteSchedule(scheduleId: String): Boolean
    
    /**
     * Get activities for a specific date.
     */
    suspend fun getActivitiesForDate(date: LocalDate): List<DayActivity>
    
    /**
     * Get activities for a date range.
     */
    suspend fun getActivitiesInRange(startDate: LocalDate, endDate: LocalDate): List<DayActivity>
    
    /**
     * Get a specific activity by ID.
     */
    suspend fun getActivityById(activityId: String): DayActivity?
    
    /**
     * Create a new activity.
     */
    suspend fun createActivity(activity: DayActivity): DayActivity
    
    /**
     * Update an existing activity.
     */
    suspend fun updateActivity(activity: DayActivity): DayActivity
    
    /**
     * Delete an activity.
     */
    suspend fun deleteActivity(activityId: String): Boolean
    
    /**
     * Toggle the completion status of an activity.
     */
    suspend fun toggleActivityCompletion(activityId: String, completed: Boolean): DayActivity
}
