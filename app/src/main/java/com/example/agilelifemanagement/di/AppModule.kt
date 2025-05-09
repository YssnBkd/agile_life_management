package com.example.agilelifemanagement.di

import android.content.Context
import com.example.agilelifemanagement.data.local.AgileLifeDatabase
import com.example.agilelifemanagement.data.local.dao.*
import com.example.agilelifemanagement.data.remote.SupabaseConfig
import com.example.agilelifemanagement.data.remote.SupabaseManager
import com.example.agilelifemanagement.data.remote.api.*
import com.example.agilelifemanagement.util.NetworkMonitor
import com.example.agilelifemanagement.data.remote.SyncManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideNetworkMonitor(@ApplicationContext context: Context): NetworkMonitor {
        return NetworkMonitor(context)
    }

    @Provides
    @Singleton
    fun provideSupabaseConfig(@ApplicationContext context: Context): SupabaseConfig {
        return SupabaseConfig(context)
    }

    @Provides
    @Singleton
    fun provideSyncManager(
        userApiService: UserApiService,
        sprintApiService: SprintApiService,
        goalApiService: GoalApiService,
        taskApiService: TaskApiService,
        syncStatusDao: SyncStatusDao,
        networkMonitor: NetworkMonitor,
        supabaseManager: SupabaseManager
    ): SyncManager {
        return SyncManager(
            userApiService = userApiService,
            sprintApiService = sprintApiService,
            goalApiService = goalApiService,
            taskApiService = taskApiService,
            syncStatusDao = syncStatusDao,
            networkMonitor = networkMonitor,
            supabaseManager = supabaseManager
        )
    }

    // Removed duplicate DAO providers to resolve Dagger duplicate bindings.
    // TaskDao, GoalDao, SprintDao are now only provided in DatabaseModule.

    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): androidx.datastore.core.DataStore<androidx.datastore.preferences.core.Preferences> {
        return androidx.datastore.preferences.preferencesDataStore(name = "supabase_config").getValue(context, context::javaClass)
    }

    // Removed duplicate SyncStatusDao provider to resolve Dagger duplicate bindings.

    // Removed duplicate API service providers (TaskApiService, GoalApiService, SprintApiService) to resolve Dagger duplicate bindings.

    @Provides
    @Singleton
    fun provideUserApiService(supabaseManager: SupabaseManager): UserApiService = UserApiService(supabaseManager)

    @IoDispatcher
    @Provides
    fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO

    @MainDispatcher
    @Provides
    fun provideMainDispatcher(): CoroutineDispatcher = Dispatchers.Main

    @DefaultDispatcher
    @Provides
    fun provideDefaultDispatcher(): CoroutineDispatcher = Dispatchers.Default
}

@Retention(AnnotationRetention.BINARY)
@Qualifier
annotation class IoDispatcher

@Retention(AnnotationRetention.BINARY)
@Qualifier
annotation class MainDispatcher

@Retention(AnnotationRetention.BINARY)
@Qualifier
annotation class DefaultDispatcher
