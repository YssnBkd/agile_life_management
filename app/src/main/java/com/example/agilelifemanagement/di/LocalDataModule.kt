package com.example.agilelifemanagement.di

import android.content.Context
import androidx.room.Room
import com.example.agilelifemanagement.data.local.AgileLifeDatabase
import com.example.agilelifemanagement.data.local.dao.*
import com.example.agilelifemanagement.data.local.source.*
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Module that provides local data sources and Room database for the application.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class LocalDataSourceBindings {

    @Singleton
    @Binds
    abstract fun bindTaskLocalDataSource(
        taskLocalDataSourceImpl: TaskLocalDataSourceImpl
    ): TaskLocalDataSource

    @Singleton
    @Binds
    abstract fun bindSprintLocalDataSource(
        sprintLocalDataSourceImpl: SprintLocalDataSourceImpl
    ): SprintLocalDataSource

    @Singleton
    @Binds
    abstract fun bindGoalLocalDataSource(
        goalLocalDataSourceImpl: GoalLocalDataSourceImpl
    ): GoalLocalDataSource

    @Singleton
    @Binds
    abstract fun bindDayActivityLocalDataSource(
        dayActivityLocalDataSourceImpl: DayActivityLocalDataSourceImpl
    ): DayActivityLocalDataSource

    @Singleton
    @Binds
    abstract fun bindCategoryLocalDataSource(
        categoryLocalDataSourceImpl: CategoryLocalDataSourceImpl
    ): CategoryLocalDataSource

    @Singleton
    @Binds
    abstract fun bindWellnessLocalDataSource(
        wellnessLocalDataSourceImpl: WellnessLocalDataSourceImpl
    ): WellnessLocalDataSource
}

/**
 * Module that provides Room database and DAOs for the application.
 */
@Module
@InstallIn(SingletonComponent::class)
object LocalDataModule {

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context): AgileLifeDatabase {
        return Room.databaseBuilder(
            context,
            AgileLifeDatabase::class.java,
            "agile_life_management_db"
        )
            .fallbackToDestructiveMigration() // For now, during development
            .build()
    }

    @Singleton
    @Provides
    fun provideTaskDao(database: AgileLifeDatabase): TaskDao {
        return database.taskDao()
    }

    @Singleton
    @Provides
    fun provideSprintDao(database: AgileLifeDatabase): SprintDao {
        return database.sprintDao()
    }

    @Singleton
    @Provides
    fun provideGoalDao(database: AgileLifeDatabase): GoalDao {
        return database.goalDao()
    }

    @Singleton
    @Provides
    fun provideDayActivityDao(database: AgileLifeDatabase): DayActivityDao {
        return database.dayActivityDao()
    }

    @Singleton
    @Provides
    fun provideActivityCategoryDao(database: AgileLifeDatabase): ActivityCategoryDao {
        return database.activityCategoryDao()
    }

    @Singleton
    @Provides
    fun provideDailyCheckupDao(database: AgileLifeDatabase): DailyCheckupDao {
        return database.dailyCheckupDao()
    }
}
