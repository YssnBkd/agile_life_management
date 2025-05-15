package com.example.agilelifemanagement.di

import com.example.agilelifemanagement.data.remote.api.*
import com.example.agilelifemanagement.data.remote.source.*
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import javax.inject.Singleton

/**
 * Module that provides remote data sources and API services for the application.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RemoteDataSourceBindings {

    @Singleton
    @Binds
    abstract fun bindTaskRemoteDataSource(
        taskRemoteDataSourceImpl: TaskRemoteDataSourceImpl
    ): TaskRemoteDataSource

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
    abstract fun bindDayActivityRemoteDataSource(
        dayActivityRemoteDataSourceImpl: DayActivityRemoteDataSourceImpl
    ): DayActivityRemoteDataSource

    @Singleton
    @Binds
    abstract fun bindCategoryRemoteDataSource(
        categoryRemoteDataSourceImpl: CategoryRemoteDataSourceImpl
    ): CategoryRemoteDataSource

    @Singleton
    @Binds
    abstract fun bindWellnessRemoteDataSource(
        wellnessRemoteDataSourceImpl: WellnessRemoteDataSourceImpl
    ): WellnessRemoteDataSource
}

/**
 * Module that provides API services for the application.
 * These are placeholder implementations that will be expanded when 
 * remote functionality is fully implemented.
 */
@Module
@InstallIn(SingletonComponent::class)
object RemoteServiceProviders {

    @Singleton
    @Provides
    fun provideTaskApiService(httpClient: HttpClient): TaskApiService {
        return TaskApiServiceImpl(httpClient)
    }

    @Singleton
    @Provides
    fun provideSprintApiService(httpClient: HttpClient): SprintApiService {
        return SprintApiServiceImpl(httpClient)
    }

    @Singleton
    @Provides
    fun provideGoalApiService(httpClient: HttpClient): GoalApiService {
        return GoalApiServiceImpl(httpClient)
    }

    @Singleton
    @Provides
    fun provideDayActivityApiService(httpClient: HttpClient): DayActivityApiService {
        return DayActivityApiServiceImpl(httpClient)
    }

    @Singleton
    @Provides
    fun provideCategoryApiService(httpClient: HttpClient): CategoryApiService {
        return CategoryApiServiceImpl(httpClient)
    }

    @Singleton
    @Provides
    fun provideWellnessApiService(httpClient: HttpClient): WellnessApiService {
        return WellnessApiServiceImpl(httpClient)
    }
}
