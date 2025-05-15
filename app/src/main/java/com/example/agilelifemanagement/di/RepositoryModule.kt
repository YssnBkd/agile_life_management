package com.example.agilelifemanagement.di

import com.example.agilelifemanagement.data.repository.*
import com.example.agilelifemanagement.domain.repository.*
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module that provides repository implementations.
 * This module binds concrete implementations to their interfaces.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    
    @Singleton
    @Binds
    abstract fun bindTaskRepository(
        taskRepositoryImpl: TaskRepositoryImpl
    ): TaskRepository
    
    @Singleton
    @Binds
    abstract fun bindSprintRepository(
        sprintRepositoryImpl: SprintRepositoryImpl
    ): SprintRepository
    
    @Singleton
    @Binds
    abstract fun bindGoalRepository(
        goalRepositoryImpl: GoalRepositoryImpl
    ): GoalRepository
    
    @Singleton
    @Binds
    abstract fun bindDayActivityRepository(
        dayActivityRepositoryImpl: DayActivityRepositoryImpl
    ): DayActivityRepository
    
    @Singleton
    @Binds
    abstract fun bindCategoryRepository(
        categoryRepositoryImpl: CategoryRepositoryImpl
    ): CategoryRepository
    
    @Singleton
    @Binds
    abstract fun bindWellnessRepository(
        wellnessRepositoryImpl: WellnessRepositoryImpl
    ): WellnessRepository
}
