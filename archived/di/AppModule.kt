package com.example.agilelifemanagement.di

import android.content.Context
import com.example.agilelifemanagement.data.local.AgileLifeDatabase
import com.example.agilelifemanagement.data.local.dao.*
import com.example.agilelifemanagement.data.remote.EdgeFunctionsManager
import com.example.agilelifemanagement.data.remote.StorageSyncManager
import com.example.agilelifemanagement.data.remote.SupabaseConfig
import com.example.agilelifemanagement.data.remote.SupabaseManager
import com.example.agilelifemanagement.data.remote.api.*
import com.example.agilelifemanagement.util.ApplicationStartup
import com.example.agilelifemanagement.util.ErrorHandler
import com.example.agilelifemanagement.util.NetworkMonitor
import com.example.agilelifemanagement.data.remote.SyncManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.json.Json
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
    fun provideJson(): Json {
        return Json {
            ignoreUnknownKeys = true
            isLenient = true
            encodeDefaults = true
            prettyPrint = false
            coerceInputValues = true
        }
    }

    @Provides
    @Singleton
    fun provideSyncManager(
        userApiService: UserApiService,
        sprintApiService: SprintApiService,
        goalApiService: GoalApiService,
        taskApiService: TaskApiService,
        syncStatusDao: SyncStatusDao,
        userDao: UserDao,
        sprintDao: SprintDao,
        goalDao: GoalDao,
        taskDao: TaskDao,
        networkMonitor: NetworkMonitor,
        supabaseManager: SupabaseManager,
        json: Json
    ): SyncManager {
        return SyncManager(
            userApiService = userApiService,
            sprintApiService = sprintApiService,
            goalApiService = goalApiService,
            taskApiService = taskApiService,
            syncStatusDao = syncStatusDao,
            userDao = userDao,
            sprintDao = sprintDao,
            goalDao = goalDao,
            taskDao = taskDao,
            networkMonitor = networkMonitor,
            supabaseManager = supabaseManager,
            json = json
        )
    }

    @Provides
    @Singleton
    fun provideStorageSyncManager(
        @ApplicationContext context: Context,
        supabaseManager: SupabaseManager,
        networkMonitor: NetworkMonitor,
        syncManager: SyncManager
    ): StorageSyncManager {
        return StorageSyncManager(
            context = context,
            supabaseManager = supabaseManager,
            networkMonitor = networkMonitor,
            syncManager = syncManager
        )
    }
    
    /**
     * Provides ApplicationStartup singleton that handles initialization of real-time sync.
     * This is eagerly initialized when the application starts.
     */
    @Provides
    @Singleton
    fun provideApplicationStartup(
        supabaseManager: SupabaseManager,
        realtimeManager: com.example.agilelifemanagement.data.remote.SupabaseRealtimeManager
    ): ApplicationStartup {
        return ApplicationStartup(
            supabaseManager = supabaseManager,
            realtimeManager = realtimeManager
        )
    }
    
    @Provides
    @Singleton
    fun provideEdgeFunctionsManager(
        supabaseManager: SupabaseManager,
        networkMonitor: NetworkMonitor,
        json: Json
    ): EdgeFunctionsManager {
        return EdgeFunctionsManager(
            supabaseManager = supabaseManager,
            networkMonitor = networkMonitor,
            json = json
        )
    }
    
    @Provides
    @Singleton
    fun provideErrorHandler(): ErrorHandler {
        return ErrorHandler()
    }

    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): androidx.datastore.core.DataStore<androidx.datastore.preferences.core.Preferences> {
        return androidx.datastore.preferences.preferencesDataStore(name = "user_preferences").getValue(context, context::javaClass)
    }

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
