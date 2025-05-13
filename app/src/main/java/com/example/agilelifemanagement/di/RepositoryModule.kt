package com.example.agilelifemanagement.di


import com.example.agilelifemanagement.data.local.dao.*
import com.example.agilelifemanagement.data.remote.api.*
import com.example.agilelifemanagement.data.repository.*
import com.example.agilelifemanagement.data.remote.SupabaseManager
import com.example.agilelifemanagement.data.remote.SupabaseRealtimeManager
import com.example.agilelifemanagement.data.remote.SyncManager
import com.example.agilelifemanagement.util.NetworkMonitor
import com.example.agilelifemanagement.domain.repository.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideTaskRepository(
        taskDao: TaskDao,
        taskApiService: TaskApiService,
        taskSprintCrossRefDao: TaskSprintCrossRefDao,
        taskSprintCrossRefApiService: TaskSprintCrossRefApiService,
        taskGoalCrossRefDao: TaskGoalCrossRefDao,
        taskGoalCrossRefApiService: TaskGoalCrossRefApiService,
        taskDependencyDao: TaskDependencyDao,
        taskDependencyApiService: TaskDependencyApiService,
        taskTagCrossRefDao: TaskTagCrossRefDao,
        taskTagCrossRefApiService: TaskTagCrossRefApiService,
        syncManager: SyncManager,
        supabaseManager: SupabaseManager,
        realtimeManager: SupabaseRealtimeManager,
        networkMonitor: NetworkMonitor
    ): TaskRepository {
        return TaskRepositoryImpl(
            taskDao = taskDao,
            taskApiService = taskApiService,
            syncManager = syncManager,
            supabaseManager = supabaseManager,
            realtimeManager = realtimeManager,
            taskSprintCrossRefDao = taskSprintCrossRefDao,
            taskGoalCrossRefDao = taskGoalCrossRefDao,
            taskTagCrossRefDao = taskTagCrossRefDao,
            taskDependencyDao = taskDependencyDao,
            taskSprintCrossRefApiService = taskSprintCrossRefApiService,
            taskGoalCrossRefApiService = taskGoalCrossRefApiService,
            taskTagCrossRefApiService = taskTagCrossRefApiService,
            taskDependencyApiService = taskDependencyApiService,
            networkMonitor = networkMonitor
        )
    }

    @Provides
    @Singleton
    fun provideGoalRepository(
        goalDao: GoalDao,
        goalSprintCrossRefDao: GoalSprintCrossRefDao,
        goalApiService: GoalApiService,
        goalSprintCrossRefApiService: GoalSprintCrossRefApiService,
        syncManager: SyncManager,
        networkMonitor: NetworkMonitor,
        supabaseManager: SupabaseManager,
        realtimeManager: SupabaseRealtimeManager
    ): GoalRepository {
        return GoalRepositoryImpl(
            goalDao = goalDao,
            goalSprintCrossRefDao = goalSprintCrossRefDao,
            goalApiService = goalApiService,
            goalSprintCrossRefApiService = goalSprintCrossRefApiService,
            syncManager = syncManager,
            networkMonitor = networkMonitor,
            supabaseManager = supabaseManager,
            realtimeManager = realtimeManager
        )
    }

    @Provides
    @Singleton
    fun provideSprintRepository(
        sprintDao: SprintDao,
        sprintApiService: SprintApiService,
        sprintTagCrossRefDao: SprintTagCrossRefDao,
        sprintTagCrossRefApiService: SprintTagCrossRefApiService,
        syncManager: SyncManager,
        networkMonitor: NetworkMonitor,
        supabaseManager: SupabaseManager,
        realtimeManager: SupabaseRealtimeManager
    ): SprintRepository {
        return SprintRepositoryImpl(
            sprintDao = sprintDao,
            sprintApiService = sprintApiService,
            syncManager = syncManager,
            networkMonitor = networkMonitor,
            supabaseManager = supabaseManager,
            realtimeManager = realtimeManager
        )
    }

    @Provides
    @Singleton
    fun provideDailyCheckupRepository(
        dailyCheckupDao: DailyCheckupDao,
        dailyCheckupApiService: DailyCheckupApiService,
        syncManager: SyncManager,
        networkMonitor: NetworkMonitor
    ): DailyCheckupRepository {
        return DailyCheckupRepositoryImpl(
            dailyCheckupDao = dailyCheckupDao,
            dailyCheckupApiService = dailyCheckupApiService,
            syncManager = syncManager,
            networkMonitor = networkMonitor
        )
    }

    @Provides
    @Singleton
    fun provideSprintReviewRepository(
        sprintReviewDao: SprintReviewDao,
        sprintReviewApiService: SprintReviewApiService,
        syncManager: SyncManager,
        networkMonitor: NetworkMonitor
    ): SprintReviewRepository {
        return SprintReviewRepositoryImpl(
            sprintReviewDao = sprintReviewDao,
            sprintReviewApiService = sprintReviewApiService,
            syncManager = syncManager,
            networkMonitor = networkMonitor
        )
    }

    @Provides
    @Singleton
    fun provideTagRepository(
        tagDao: TagDao,
        tagApiService: TagApiService,
        syncManager: SyncManager,
        supabaseManager: SupabaseManager,
        networkMonitor: NetworkMonitor
    ): TagRepository {
        return TagRepositoryImpl(
            tagDao = tagDao,
            tagApiService = tagApiService,
            syncManager = syncManager,
            supabaseManager = supabaseManager,
            networkMonitor = networkMonitor
        )
    }

    @Provides
    @Singleton
    fun provideNotificationRepository(
        notificationDao: NotificationDao,
        notificationApiService: NotificationApiService,
        syncManager: SyncManager,
        supabaseManager: SupabaseManager,
        networkMonitor: NetworkMonitor
    ): NotificationRepository {
        return NotificationRepositoryImpl(
            notificationDao = notificationDao,
            notificationApiService = notificationApiService,
            syncManager = syncManager,
            supabaseManager = supabaseManager,
            networkMonitor = networkMonitor
        )
    }
}
