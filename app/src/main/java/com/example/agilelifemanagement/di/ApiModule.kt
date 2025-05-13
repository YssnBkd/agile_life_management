package com.example.agilelifemanagement.di

import com.example.agilelifemanagement.data.remote.auth.SupabaseConfig
import com.example.agilelifemanagement.data.remote.SupabaseManager
import com.example.agilelifemanagement.data.remote.SupabaseRealtimeManager
import com.example.agilelifemanagement.data.remote.api.*
import com.example.agilelifemanagement.util.NetworkMonitor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {

    @Provides
    @Singleton
    fun provideSupabaseManager(
        supabaseConfig: SupabaseConfig,
        networkMonitor: NetworkMonitor
    ): SupabaseManager {
        return SupabaseManager(supabaseConfig, networkMonitor)
    }

    @Provides
    @Singleton
    fun provideTaskApiService(supabaseManager: SupabaseManager): TaskApiService {
        return TaskApiService(supabaseManager)
    }

    @Provides
    @Singleton
    fun provideGoalApiService(supabaseManager: SupabaseManager): GoalApiService {
        return GoalApiService(supabaseManager)
    }

    @Provides
    @Singleton
    fun provideSprintApiService(supabaseManager: SupabaseManager): SprintApiService {
        return SprintApiService(supabaseManager)
    }

    @Provides
    @Singleton
    fun provideDailyCheckupApiService(supabaseManager: SupabaseManager): DailyCheckupApiService {
        return DailyCheckupApiService(supabaseManager)
    }

    @Provides
    @Singleton
    fun provideSprintReviewApiService(supabaseManager: SupabaseManager): SprintReviewApiService {
        return SprintReviewApiService(supabaseManager)
    }

    @Provides
    @Singleton
    fun provideTagApiService(supabaseManager: SupabaseManager): TagApiService {
        return TagApiService(supabaseManager)
    }

    @Provides
    @Singleton
    fun provideNotificationApiService(supabaseManager: SupabaseManager): NotificationApiService {
        return NotificationApiService(supabaseManager)
    }

    @Provides
    @Singleton
    fun provideTaskSprintCrossRefApiService(supabaseManager: SupabaseManager): TaskSprintCrossRefApiService {
        return TaskSprintCrossRefApiService(supabaseManager)
    }

    @Provides
    @Singleton
    fun provideTaskGoalCrossRefApiService(supabaseManager: SupabaseManager): TaskGoalCrossRefApiService {
        return TaskGoalCrossRefApiService(supabaseManager)
    }

    @Provides
    @Singleton
    fun provideGoalSprintCrossRefApiService(supabaseManager: SupabaseManager): GoalSprintCrossRefApiService {
        return GoalSprintCrossRefApiService(supabaseManager)
    }

    @Provides
    @Singleton
    fun provideTaskDependencyApiService(supabaseManager: SupabaseManager): TaskDependencyApiService {
        return TaskDependencyApiService(supabaseManager)
    }

    @Provides
    @Singleton
    fun provideTaskTagCrossRefApiService(supabaseManager: SupabaseManager): TaskTagCrossRefApiService {
        return TaskTagCrossRefApiService(supabaseManager)
    }

    @Provides
    @Singleton
    fun provideGoalTagCrossRefApiService(supabaseManager: SupabaseManager): GoalTagCrossRefApiService {
        return GoalTagCrossRefApiService(supabaseManager)
    }

    @Provides
    @Singleton
    fun provideSprintTagCrossRefApiService(supabaseManager: SupabaseManager): SprintTagCrossRefApiService {
        return SprintTagCrossRefApiService(supabaseManager)
    }
    
    @Provides
    @Singleton
    fun provideSupabaseRealtimeManager(
        supabaseManager: SupabaseManager,
        taskDao: com.example.agilelifemanagement.data.local.dao.TaskDao,
        sprintDao: com.example.agilelifemanagement.data.local.dao.SprintDao,
        goalDao: com.example.agilelifemanagement.data.local.dao.GoalDao,
        taskSprintCrossRefDao: com.example.agilelifemanagement.data.local.dao.TaskSprintCrossRefDao,
        taskGoalCrossRefDao: com.example.agilelifemanagement.data.local.dao.TaskGoalCrossRefDao,
        goalSprintCrossRefDao: com.example.agilelifemanagement.data.local.dao.GoalSprintCrossRefDao,
        syncManager: com.example.agilelifemanagement.data.remote.SyncManager
    ): SupabaseRealtimeManager {
        return SupabaseRealtimeManager(
            supabaseManager = supabaseManager,
            taskDao = taskDao,
            sprintDao = sprintDao,
            goalDao = goalDao,
            taskSprintCrossRefDao = taskSprintCrossRefDao,
            taskGoalCrossRefDao = taskGoalCrossRefDao,
            goalSprintCrossRefDao = goalSprintCrossRefDao,
            syncManager = syncManager
        )
    }
}
