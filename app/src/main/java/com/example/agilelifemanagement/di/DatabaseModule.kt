package com.example.agilelifemanagement.di

import android.content.Context
import androidx.room.Room
import com.example.agilelifemanagement.data.local.AgileLifeDatabase
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
     */
    @Provides
    @Singleton
    fun provideAgileLifeDatabase(@ApplicationContext context: Context): AgileLifeDatabase {
        return Room.databaseBuilder(
            context,
            AgileLifeDatabase::class.java,
            "agile_life_db"
        )
            .fallbackToDestructiveMigration() // For development only, remove in production
            .build()
    }

    /**
     * Provides the TaskDao for accessing task data.
     */
    @Provides
    @Singleton
    fun provideTaskDao(database: AgileLifeDatabase): TaskDao {
        return database.taskDao()
    }

    /**
     * Provides the TagDao for accessing tag data.
     */
    @Provides
    @Singleton
    fun provideTagDao(database: AgileLifeDatabase): TagDao {
        return database.tagDao()
    }

    /**
     * Provides the TaskTagCrossRefDao for managing task-tag relationships.
     */
    @Provides
    @Singleton
    fun provideTaskTagCrossRefDao(database: AgileLifeDatabase): TaskTagCrossRefDao {
        return database.taskTagCrossRefDao()
    }

    /**
     * Provides the SprintDao for accessing sprint data.
     */
    @Provides
    @Singleton
    fun provideSprintDao(database: AgileLifeDatabase): SprintDao {
        return database.sprintDao()
    }

    /**
     * Provides the SprintReviewDao for accessing sprint review data.
     */
    @Provides
    @Singleton
    fun provideSprintReviewDao(database: AgileLifeDatabase): SprintReviewDao {
        return database.sprintReviewDao()
    }

    /**
     * Provides the GoalDao for accessing goal data.
     */
    @Provides
    @Singleton
    fun provideGoalDao(database: AgileLifeDatabase): GoalDao {
        return database.goalDao()
    }

    /**
     * Provides the DayActivityDao for accessing day activity data.
     */
    @Provides
    @Singleton
    fun provideDayActivityDao(database: AgileLifeDatabase): DayActivityDao {
        return database.dayActivityDao()
    }

    /**
     * Provides the DayActivityTemplateDao for accessing activity template data.
     */
    @Provides
    @Singleton
    fun provideDayActivityTemplateDao(database: AgileLifeDatabase): DayActivityTemplateDao {
        return database.dayActivityTemplateDao()
    }

    /**
     * Provides the ActivityCategoryDao for accessing activity category data.
     */
    @Provides
    @Singleton
    fun provideActivityCategoryDao(database: AgileLifeDatabase): ActivityCategoryDao {
        return database.activityCategoryDao()
    }

    /**
     * Provides the DailyCheckupDao for accessing daily wellness checkup data.
     */
    @Provides
    @Singleton
    fun provideDailyCheckupDao(database: AgileLifeDatabase): DailyCheckupDao {
        return database.dailyCheckupDao()
    }
}
