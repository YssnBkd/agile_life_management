package com.example.agilelifemanagement.data.local.source

import com.example.agilelifemanagement.data.local.dao.DayScheduleDao
import com.example.agilelifemanagement.data.local.entity.DayScheduleEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import javax.inject.Inject

/**
 * Local data source for day schedules.
 * Uses Room DAOs to perform database operations.
 */
class DayScheduleLocalDataSource @Inject constructor(
    private val dayScheduleDao: DayScheduleDao
) {
    /**
     * Get a schedule for a specific date.
     */
    fun observeScheduleByDate(date: LocalDate): Flow<DayScheduleEntity?> = 
        dayScheduleDao.getScheduleByDate(date)
    
    /**
     * Get schedules for a date range.
     */
    fun observeSchedulesForDateRange(startDate: LocalDate, endDate: LocalDate): Flow<List<DayScheduleEntity>> = 
        dayScheduleDao.getSchedulesForDateRange(startDate, endDate)
    
    /**
     * Get a specific schedule by ID.
     */
    suspend fun getScheduleById(scheduleId: String): DayScheduleEntity? = 
        dayScheduleDao.getScheduleById(scheduleId)
    
    /**
     * Insert a schedule.
     */
    suspend fun insertSchedule(schedule: DayScheduleEntity) {
        dayScheduleDao.insertSchedule(schedule)
    }
    
    /**
     * Insert multiple schedules.
     */
    suspend fun insertSchedules(schedules: List<DayScheduleEntity>) {
        dayScheduleDao.insertSchedules(schedules)
    }
    
    /**
     * Update a schedule.
     */
    suspend fun updateSchedule(schedule: DayScheduleEntity): Int =
        dayScheduleDao.updateSchedule(schedule)
    
    /**
     * Delete a schedule.
     */
    suspend fun deleteSchedule(scheduleId: String): Int =
        dayScheduleDao.deleteSchedule(scheduleId)
}
