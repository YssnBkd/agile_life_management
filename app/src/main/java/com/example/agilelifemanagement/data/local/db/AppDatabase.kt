package com.example.agilelifemanagement.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.agilelifemanagement.data.local.converter.Converters
import com.example.agilelifemanagement.data.local.dao.*
import com.example.agilelifemanagement.data.local.entity.*

/**
 * Main Room database configuration for AgileLifeManagement.
 * 
 * This database follows Material 3 Expressive design principles by providing
 * a central data store for all app components with proper separation of concerns.
 * 
 * Features:
 * - Centralized offline data storage
 * - Type converters for complex data types
 * - Export schema for version tracking
 * - Abstract DAO accessors for clean architecture compliance
 */
@Database(
    entities = [
        DayActivityEntity::class,
        TaskEntity::class,
        TimeBlockEntity::class,
        ActivityCategoryEntity::class,
        TagEntity::class,
        SprintEntity::class,
        GoalEntity::class,
        TaskTagCrossRef::class,
        DayScheduleEntity::class,
        SprintReviewEntity::class,
        DayActivityTemplateEntity::class,
        DailyCheckupEntity::class
    ],
    version = 1,
    exportSchema = true,
    autoMigrations = []
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    
    /**
     * Provides access to the DayActivity data access operations.
     */
    abstract fun dayActivityDao(): DayActivityDao
    
    /**
     * Provides access to the Task data access operations.
     */
    abstract fun taskDao(): TaskDao
    
    /**
     * Provides access to the TimeBlock data access operations.
     */
    abstract fun timeBlockDao(): TimeBlockDao
    
    /**
     * Provides access to the ActivityCategory data access operations.
     */
    abstract fun activityCategoryDao(): ActivityCategoryDao
    
    /**
     * Provides access to the Tag data access operations.
     */
    abstract fun tagDao(): TagDao
    
    /**
     * Provides access to the Sprint data access operations.
     */
    abstract fun sprintDao(): SprintDao
    
    /**
     * Provides access to the Goal data access operations.
     */
    abstract fun goalDao(): GoalDao
    
    /**
     * Provides access to the TaskTag cross-reference operations.
     */
    abstract fun taskTagCrossRefDao(): TaskTagCrossRefDao
    
    /**
     * Provides access to the DaySchedule data access operations.
     */
    abstract fun dayScheduleDao(): DayScheduleDao
    
    /**
     * Provides access to the SprintReview data access operations.
     */
    abstract fun sprintReviewDao(): SprintReviewDao
    
    /**
     * Provides access to the DayActivityTemplate data access operations.
     */
    abstract fun dayActivityTemplateDao(): DayActivityTemplateDao
    
    /**
     * Provides access to the DailyCheckup data access operations.
     */
    abstract fun dailyCheckupDao(): DailyCheckupDao
    
    companion object {
        const val DATABASE_NAME = "agile_life_db"
    }
}
