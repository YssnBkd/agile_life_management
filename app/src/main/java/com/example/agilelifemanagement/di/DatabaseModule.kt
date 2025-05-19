package com.example.agilelifemanagement.di

import android.content.Context
import androidx.room.Room
import com.example.agilelifemanagement.data.local.db.AppDatabase
import com.example.agilelifemanagement.data.local.dao.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module that provides database-related dependencies.
 * This module follows our offline-first approach, providing Room database and DAOs.
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    /**
     * Provides the Room database instance.
     * 
     * This database follows Material 3 Expressive design principles by providing
     * a central data store for all app components with proper separation of concerns.
     */
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME
        )
            // Since we're starting with no prior data, we can safely recreate the database
            .fallbackToDestructiveMigration()
            .build()
    }

    /**
     * Provides the TaskDao for accessing task data.
     * Supports Material 3 Expressive design with analytics and dashboard visualization queries.
     */
    @Provides
    @Singleton
    fun provideTaskDao(database: AppDatabase): TaskDao {
        return database.taskDao()
    }

    /**
     * Provides the TagDao for accessing tag data.
     */
    @Provides
    @Singleton
    fun provideTagDao(database: AppDatabase): TagDao {
        return database.tagDao()
    }

    /**
     * Provides the TaskTagCrossRefDao for managing task-tag relationships.
     */
    @Provides
    @Singleton
    fun provideTaskTagCrossRefDao(database: AppDatabase): TaskTagCrossRefDao {
        return database.taskTagCrossRefDao()
    }

    /**
     * Provides the SprintDao for accessing sprint data.
     */
    @Provides
    @Singleton
    fun provideSprintDao(database: AppDatabase): SprintDao {
        return database.sprintDao()
    }

    /**
     * Provides the SprintReviewDao for accessing sprint review data.
     */
    @Provides
    @Singleton
    fun provideSprintReviewDao(database: AppDatabase): SprintReviewDao {
        return database.sprintReviewDao()
    }

    /**
     * Provides the GoalDao for accessing goal data.
     */
    @Provides
    @Singleton
    fun provideGoalDao(database: AppDatabase): GoalDao {
        return database.goalDao()
    }

    /**
     * Provides the DayActivityDao for accessing day activity data.
     */
    @Provides
    @Singleton
    fun provideDayActivityDao(database: AppDatabase): DayActivityDao {
        return database.dayActivityDao()
    }
    
    /**
     * Provides the TimeBlockDao for accessing time block data.
     * This DAO supports Material 3 timeline visualizations and calendar integrations.
     */
    @Provides
    @Singleton
    fun provideTimeBlockDao(database: AppDatabase): TimeBlockDao {
        return database.timeBlockDao()
    }
    
    /**
     * Provides the ActivityCategoryDao for accessing category data.
     * This DAO supports Material 3 color system and iconography.
     */
    @Provides
    @Singleton
    fun provideActivityCategoryDao(database: AppDatabase): ActivityCategoryDao {
        return database.activityCategoryDao()
    }
    
    /**
     * Provides the DayActivityTemplateDao for accessing activity template data.
     */
    @Provides
    @Singleton
    fun provideDayActivityTemplateDao(database: AppDatabase): DayActivityTemplateDao {
        return database.dayActivityTemplateDao()
    }
    
    /**
     * Provides the DayScheduleDao for accessing day schedule data.
     */
    @Provides
    @Singleton
    fun provideDayScheduleDao(database: AppDatabase): DayScheduleDao {
        return database.dayScheduleDao()
    }
    
    /**
     * Provides the DailyCheckupDao for accessing daily checkup data.
     */
    @Provides
    @Singleton
    fun provideDailyCheckupDao(database: AppDatabase): DailyCheckupDao {
        return database.dailyCheckupDao()
    }

    // Note: The following providers have already been implemented above
    // with the correct AppDatabase reference
}
