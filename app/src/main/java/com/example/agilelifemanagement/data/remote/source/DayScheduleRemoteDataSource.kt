package com.example.agilelifemanagement.data.remote.source

import com.example.agilelifemanagement.data.remote.api.DayApiService
import com.example.agilelifemanagement.domain.model.DaySchedule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate
import javax.inject.Inject

/**
 * Remote data source for day schedules.
 * Handles API communication for schedules.
 */
class DayScheduleRemoteDataSource @Inject constructor(
    private val dayApiService: DayApiService
) {
    /**
     * Get a schedule for a specific date from the remote API.
     */
    suspend fun getScheduleForDate(date: LocalDate): DaySchedule? = withContext(Dispatchers.IO) {
        try {
            dayApiService.getScheduleByDate(date)
        } catch (e: Exception) {
            // Log error and return null on failure
            null
        }
    }

    /**
     * Get schedules for a date range from the remote API.
     */
    suspend fun getSchedulesInRange(startDate: LocalDate, endDate: LocalDate): List<DaySchedule> = withContext(Dispatchers.IO) {
        try {
            dayApiService.getSchedulesForDateRange(startDate, endDate)
        } catch (e: Exception) {
            // Log error and return empty list on failure
            emptyList()
        }
    }

    /**
     * Get a specific schedule by ID from the remote API.
     */
    suspend fun getScheduleById(scheduleId: String): DaySchedule? = withContext(Dispatchers.IO) {
        try {
            dayApiService.getScheduleById(scheduleId)
        } catch (e: Exception) {
            // Log error and return null on failure
            null
        }
    }

    /**
     * Create a new schedule in the remote API.
     */
    suspend fun createSchedule(schedule: DaySchedule): DaySchedule = withContext(Dispatchers.IO) {
        dayApiService.createSchedule(schedule)
    }

    /**
     * Update an existing schedule in the remote API.
     */
    suspend fun updateSchedule(schedule: DaySchedule): DaySchedule = withContext(Dispatchers.IO) {
        dayApiService.updateSchedule(schedule)
    }

    /**
     * Delete a schedule from the remote API.
     */
    suspend fun deleteSchedule(scheduleId: String): Boolean = withContext(Dispatchers.IO) {
        try {
            dayApiService.deleteSchedule(scheduleId)
            true
        } catch (e: Exception) {
            // Log error and return false on failure
            false
        }
    }
}
