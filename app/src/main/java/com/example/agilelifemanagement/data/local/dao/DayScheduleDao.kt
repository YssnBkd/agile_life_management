package com.example.agilelifemanagement.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.agilelifemanagement.data.local.entity.DayScheduleEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

/**
 * Data Access Object for day schedule operations.
 */
@Dao
interface DayScheduleDao {
    /**
     * Get a schedule for a specific date.
     */
    @Query("SELECT * FROM day_schedules WHERE date = :date LIMIT 1")
    fun getScheduleByDate(date: LocalDate): Flow<DayScheduleEntity?>
    
    /**
     * Get schedules for a date range.
     */
    @Query("SELECT * FROM day_schedules WHERE date BETWEEN :startDate AND :endDate ORDER BY date ASC")
    fun getSchedulesForDateRange(startDate: LocalDate, endDate: LocalDate): Flow<List<DayScheduleEntity>>
    
    /**
     * Get a specific schedule by ID.
     */
    @Query("SELECT * FROM day_schedules WHERE id = :scheduleId LIMIT 1")
    suspend fun getScheduleById(scheduleId: String): DayScheduleEntity?
    
    /**
     * Insert a schedule, replacing if it already exists.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSchedule(schedule: DayScheduleEntity)
    
    /**
     * Insert multiple schedules, replacing if they already exist.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSchedules(schedules: List<DayScheduleEntity>)
    
    /**
     * Update a schedule.
     */
    @Update
    suspend fun updateSchedule(schedule: DayScheduleEntity): Int
    
    /**
     * Delete a schedule.
     */
    @Query("DELETE FROM day_schedules WHERE id = :scheduleId")
    suspend fun deleteSchedule(scheduleId: String): Int
}
