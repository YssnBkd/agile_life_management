package com.example.agilelifemanagement.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.agilelifemanagement.data.local.converter.Converters
import com.example.agilelifemanagement.data.local.dao.*
import com.example.agilelifemanagement.data.local.entity.*

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
    abstract fun activityCategoryDao(): ActivityCategoryDao
    abstract fun dailyCheckupDao(): DailyCheckupDao
    abstract fun tagDao(): TagDao
    abstract fun taskTagCrossRefDao(): TaskTagCrossRefDao
    
    // Add additional DAOs here as needed for the offline-first implementation
    
    companion object {
        private const val DATABASE_NAME = "agile_life_db"
        
        @Volatile
        private var INSTANCE: AgileLifeDatabase? = null
        
        fun getInstance(context: Context): AgileLifeDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AgileLifeDatabase::class.java,
                    DATABASE_NAME
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
