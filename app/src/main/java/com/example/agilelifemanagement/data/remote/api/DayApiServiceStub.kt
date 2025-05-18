package com.example.agilelifemanagement.data.remote.api

import com.example.agilelifemanagement.domain.model.DayActivity
import com.example.agilelifemanagement.domain.model.DaySchedule
import timber.log.Timber
import java.time.LocalDate
import javax.inject.Inject

/**
 * Stub implementation of [DayApiService] for development and testing.
 * This provides temporary functionality until the real API integration is built.
 * Part of the data layer rebuild (May 15, 2025).
 */
class DayApiServiceStub @Inject constructor() : DayApiService {
    
    override suspend fun getScheduleByDate(date: LocalDate): DaySchedule? {
        Timber.d("Stub: getScheduleByDate called with date: $date")
        return null
    }
    
    override suspend fun getSchedulesForDateRange(startDate: LocalDate, endDate: LocalDate): List<DaySchedule> {
        Timber.d("Stub: getSchedulesForDateRange called with range: $startDate to $endDate")
        return emptyList()
    }
    
    override suspend fun getScheduleById(scheduleId: String): DaySchedule? {
        Timber.d("Stub: getScheduleById called with ID: $scheduleId")
        return null
    }
    
    override suspend fun createSchedule(schedule: DaySchedule): DaySchedule {
        Timber.d("Stub: createSchedule called")
        return schedule
    }
    
    override suspend fun updateSchedule(schedule: DaySchedule): DaySchedule {
        Timber.d("Stub: updateSchedule called")
        return schedule
    }
    
    override suspend fun deleteSchedule(scheduleId: String): Boolean {
        Timber.d("Stub: deleteSchedule called with ID: $scheduleId")
        return true
    }
    
    override suspend fun getActivitiesForDate(date: LocalDate): List<DayActivity> {
        Timber.d("Stub: getActivitiesForDate called with date: $date")
        return emptyList()
    }
    
    override suspend fun getActivitiesInRange(startDate: LocalDate, endDate: LocalDate): List<DayActivity> {
        Timber.d("Stub: getActivitiesInRange called with range: $startDate to $endDate")
        return emptyList()
    }
    
    override suspend fun getActivityById(activityId: String): DayActivity? {
        Timber.d("Stub: getActivityById called with ID: $activityId")
        return null
    }
    
    override suspend fun createActivity(activity: DayActivity): DayActivity {
        Timber.d("Stub: createActivity called")
        return activity
    }
    
    override suspend fun updateActivity(activity: DayActivity): DayActivity {
        Timber.d("Stub: updateActivity called")
        return activity
    }
    
    override suspend fun deleteActivity(activityId: String): Boolean {
        Timber.d("Stub: deleteActivity called with ID: $activityId")
        return true
    }
    
    override suspend fun toggleActivityCompletion(activityId: String, completed: Boolean): DayActivity {
        Timber.d("Stub: toggleActivityCompletion called with ID: $activityId, completed: $completed")
        return DayActivity(
            id = activityId,
            title = "Stub Activity",
            description = "Auto-generated stub activity",
            date = LocalDate.now(),
            scheduledTime = java.time.LocalTime.now(),
            duration = 30,
            completed = completed,
            categoryId = "default-category"
        )
    }
}
