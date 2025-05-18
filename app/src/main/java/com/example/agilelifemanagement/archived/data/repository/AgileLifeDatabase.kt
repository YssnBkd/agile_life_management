// package com.example.agilelifemanagement.archived.data.repository

/**
 * Room database for the AgileLifeManagement app.
 * This is a legacy database that is archived and not used in the current version of the app.
 * It is kept for reference purposes only.
 
package com.example.agilelifemanagement.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.agilelifemanagement.data.local.converter.Converters
import com.example.agilelifemanagement.data.local.dao.*
import com.example.agilelifemanagement.data.local.entity.TaskEntity
import com.example.agilelifemanagement.data.local.entity.SprintEntity
import com.example.agilelifemanagement.data.local.entity.SprintReviewEntity
import com.example.agilelifemanagement.data.local.entity.GoalEntity
import com.example.agilelifemanagement.data.local.entity.DayActivityEntity
import com.example.agilelifemanagement.data.local.entity.DayActivityTemplateEntity
import com.example.agilelifemanagement.data.local.entity.DayScheduleEntity
import com.example.agilelifemanagement.data.local.entity.ActivityCategoryEntity
import com.example.agilelifemanagement.data.local.entity.DailyCheckupEntity
import com.example.agilelifemanagement.data.local.entity.TagEntity
import com.example.agilelifemanagement.data.local.entity.TaskTagCrossRef

/**
 * Room database for the AgileLifeManagement app.
 * This is the main access point for the local SQLite database.
 */
@Database(
    entities = [
        TaskEntity::class,
        SprintEntity::class,
        SprintReviewEntity::class,
        GoalEntity::class,
        DayActivityEntity::class,
        DayActivityTemplateEntity::class,
        DayScheduleEntity::class,
        ActivityCategoryEntity::class,
        DailyCheckupEntity::class,
        TagEntity::class,
        TaskTagCrossRef::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AgileLifeDatabase : RoomDatabase() {
    
    // DAOs
    abstract fun taskDao(): TaskDao
    abstract fun sprintDao(): SprintDao
    abstract fun sprintReviewDao(): SprintReviewDao
    abstract fun goalDao(): GoalDao
    abstract fun dayActivityDao(): DayActivityDao
    abstract fun dayActivityTemplateDao(): DayActivityTemplateDao
    abstract fun dayScheduleDao(): DayScheduleDao
    abstract fun activityCategoryDao(): ActivityCategoryDao
    abstract fun dailyCheckupDao(): DailyCheckupDao
    abstract fun tagDao(): TagDao
    abstract fun taskTagCrossRefDao(): TaskTagCrossRefDao
    
    // Add additional DAOs here as needed for the offline-first implementation
    // Note: Instance management is handled by Hilt in DatabaseModule
}

**/