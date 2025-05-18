package com.example.agilelifemanagement.di

import com.example.agilelifemanagement.data.local.dao.*
import com.example.agilelifemanagement.data.local.source.DailyCheckupLocalDataSource
import com.example.agilelifemanagement.data.local.source.DayActivityLocalDataSource
import com.example.agilelifemanagement.data.local.source.DayScheduleLocalDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Module that provides local data sources for the application.
 * Following the May 15, 2025 architectural shift, we're rebuilding the data layer
 * focusing first on the day schedule functionality.
 */
@Module
@InstallIn(SingletonComponent::class)
object LocalDataModule {

    /**
     * Provides DayActivityLocalDataSource for accessing day activity data.
     */
    @Provides
    @Singleton
    fun provideDayActivityLocalDataSource(
        dayActivityDao: DayActivityDao,
        templateDao: DayActivityTemplateDao
    ): DayActivityLocalDataSource {
        return DayActivityLocalDataSource(dayActivityDao, templateDao)
    }
    
    /**
     * Provides DayScheduleLocalDataSource for accessing day schedule data.
     */
    @Provides
    @Singleton
    fun provideDayScheduleLocalDataSource(dayScheduleDao: DayScheduleDao): DayScheduleLocalDataSource {
        return DayScheduleLocalDataSource(dayScheduleDao)
    }
    
    /**
     * Provides DailyCheckupLocalDataSource for accessing daily checkup data.
     */
    @Provides
    @Singleton
    fun provideDailyCheckupLocalDataSource(dailyCheckupDao: DailyCheckupDao): DailyCheckupLocalDataSource {
        return DailyCheckupLocalDataSource(dailyCheckupDao)
    }

    // Add other local data source providers as needed
    
    // Note: Providers for TaskLocalDataSource, SprintLocalDataSource, and GoalLocalDataSource will be
    // added back when those implementations are rebuilt.
}
