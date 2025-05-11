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

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideUserDao(database: AgileLifeDatabase): UserDao {
        return database.userDao()
    }

    @Provides
    @Singleton
    fun provideAgileLifeDatabase(@ApplicationContext context: Context): AgileLifeDatabase {
        return Room.databaseBuilder(
            context,
            AgileLifeDatabase::class.java,
            "agile_life_db"
        )
            .fallbackToDestructiveMigration(true) // For development only, remove in production
            .build()
    }

    @Provides
    @Singleton
    fun provideTaskDao(database: AgileLifeDatabase): TaskDao {
        return database.taskDao()
    }

    @Provides
    @Singleton
    fun provideGoalDao(database: AgileLifeDatabase): GoalDao {
        return database.goalDao()
    }

    @Provides
    @Singleton
    fun provideSprintDao(database: AgileLifeDatabase): SprintDao {
        return database.sprintDao()
    }

    @Provides
    @Singleton
    fun provideDailyCheckupDao(database: AgileLifeDatabase): DailyCheckupDao {
        return database.dailyCheckupDao()
    }

    @Provides
    @Singleton
    fun provideSprintReviewDao(database: AgileLifeDatabase): SprintReviewDao {
        return database.sprintReviewDao()
    }

    @Provides
    @Singleton
    fun provideTagDao(database: AgileLifeDatabase): TagDao {
        return database.tagDao()
    }

    @Provides
    @Singleton
    fun provideNotificationDao(database: AgileLifeDatabase): NotificationDao {
        return database.notificationDao()
    }

    @Provides
    @Singleton
    fun provideTaskSprintCrossRefDao(database: AgileLifeDatabase): TaskSprintCrossRefDao {
        return database.taskSprintCrossRefDao()
    }

    @Provides
    @Singleton
    fun provideTaskGoalCrossRefDao(database: AgileLifeDatabase): TaskGoalCrossRefDao {
        return database.taskGoalCrossRefDao()
    }

    @Provides
    @Singleton
    fun provideGoalSprintCrossRefDao(database: AgileLifeDatabase): GoalSprintCrossRefDao {
        return database.goalSprintCrossRefDao()
    }

    @Provides
    @Singleton
    fun provideTaskDependencyDao(database: AgileLifeDatabase): TaskDependencyDao {
        return database.taskDependencyDao()
    }

    @Provides
    @Singleton
    fun provideTaskTagCrossRefDao(database: AgileLifeDatabase): TaskTagCrossRefDao {
        return database.taskTagCrossRefDao()
    }

    @Provides
    @Singleton
    fun provideGoalTagCrossRefDao(database: AgileLifeDatabase): GoalTagCrossRefDao {
        return database.goalTagCrossRefDao()
    }

    @Provides
    @Singleton
    fun provideSprintTagCrossRefDao(database: AgileLifeDatabase): SprintTagCrossRefDao {
        return database.sprintTagCrossRefDao()
    }


    @Provides
    @Singleton
    fun provideSyncStatusDao(database: AgileLifeDatabase): SyncStatusDao {
        return database.syncStatusDao()
    }
}
