package com.example.agilelifemanagement.di

import com.example.agilelifemanagement.data.remote.api.DayApiService
import com.example.agilelifemanagement.data.remote.api.DayApiServiceStub
import com.example.agilelifemanagement.data.remote.api.TagApiService
import com.example.agilelifemanagement.data.remote.api.TagApiServiceStub
import com.example.agilelifemanagement.data.remote.source.*
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import javax.inject.Singleton

/**
 * Hilt module that provides remote data source implementations.
 * This module binds concrete implementations to their interfaces.
 */
/**
 * Reimplemented version of the RemoteDataSourceModule after data layer rebuild (May 15, 2025)
 * This module provides bindings for remote data sources using temporary implementations
 * that will be replaced with real API integrations later.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RemoteDataSourceModule {
    
    @Singleton
    @Binds
    abstract fun bindWellnessRemoteDataSource(
        wellnessRemoteDataSourceImpl: WellnessRemoteDataSourceImpl
    ): WellnessRemoteDataSource
    
    @Singleton
    @Binds
    abstract fun bindDayActivityRemoteDataSource(
        dayActivityRemoteDataSourceImpl: DayActivityRemoteDataSourceImpl
    ): DayActivityRemoteDataSource
    
    @Singleton
    @Binds
    abstract fun bindSprintRemoteDataSource(
        sprintRemoteDataSourceImpl: SprintRemoteDataSourceImpl
    ): SprintRemoteDataSource
    
    @Singleton
    @Binds
    abstract fun bindGoalRemoteDataSource(
        goalRemoteDataSourceImpl: GoalRemoteDataSourceImpl
    ): GoalRemoteDataSource
    
    @Singleton
    @Binds
    abstract fun bindTaskRemoteDataSource(
        taskRemoteDataSourceImpl: TaskRemoteDataSourceImpl
    ): TaskRemoteDataSource
}

/**
 * API module to provide remote API interfaces.
 * These are temporary stub implementations until the real API integrations are built.
 */
@Module
@InstallIn(SingletonComponent::class)
object ApiModule {
    
    @Singleton
    @Provides
    fun provideDayApiService(): DayApiService {
        return DayApiServiceStub()
    }
    
    @Singleton
    @Provides
    fun provideTagApiService(@UnauthenticatedClient httpClient: HttpClient): TagApiService {
        return TagApiServiceStub(httpClient)
    }
}
