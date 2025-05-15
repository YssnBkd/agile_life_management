package com.example.agilelifemanagement.di

import com.example.agilelifemanagement.data.remote.source.*
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module that provides remote data source implementations.
 * This module binds concrete implementations to their interfaces.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RemoteDataSourceModule {
    
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
