package com.example.agilelifemanagement.data.local.dao

import androidx.room.*
import com.example.agilelifemanagement.data.local.entity.DayActivityEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

/**
 * Room DAO for accessing and manipulating day activity data in the database.
 */
@Dao
interface DayActivityDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActivity(activity: DayActivityEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActivities(activities: List<DayActivityEntity>)
    
    @Update
    suspend fun updateActivity(activity: DayActivityEntity): Int
    
    @Query("UPDATE day_activities SET completed = :completed WHERE id = :activityId")
    suspend fun updateActivityCompletion(activityId: String, completed: Boolean): Int
    
    @Query("DELETE FROM day_activities WHERE id = :activityId")
    suspend fun deleteActivity(activityId: String): Int
    
    @Query("SELECT * FROM day_activities WHERE id = :activityId")
    suspend fun getActivityById(activityId: String): DayActivityEntity?
    
    @Query("SELECT * FROM day_activities WHERE date = :date ORDER BY scheduledTime")
    fun getActivitiesByDate(date: LocalDate): Flow<List<DayActivityEntity>>
    
    @Query("SELECT * FROM day_activities WHERE date BETWEEN :startDate AND :endDate ORDER BY date, scheduledTime")
    fun getActivitiesForDateRange(startDate: LocalDate, endDate: LocalDate): Flow<List<DayActivityEntity>>
    
    @Query("SELECT * FROM day_activities WHERE categoryId = :categoryId")
    fun getActivitiesByCategory(categoryId: String): Flow<List<DayActivityEntity>>
    
    @Query("SELECT * FROM day_activities WHERE completed = :completed AND date = :date ORDER BY scheduledTime")
    fun getActivitiesByCompletionStatus(completed: Boolean, date: LocalDate): Flow<List<DayActivityEntity>>
}
