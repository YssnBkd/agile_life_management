package com.example.agilelifemanagement.di

import com.example.agilelifemanagement.data.repository.*
import com.example.agilelifemanagement.domain.repository.*
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Unified Hilt module that provides repository implementations.
 * This module binds concrete implementations to their interfaces.
 * 
 * NOTE: Repository bindings are being gradually added as the data layer
 * is being rebuilt (as per the May 15, 2025 project update).
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    
    // Implemented repository bindings
    
    @Singleton
    @Binds
    abstract fun bindTaskRepository(
        taskRepositoryImpl: TaskRepositoryImpl
    ): TaskRepository
    
    @Singleton
    @Binds
    abstract fun bindTagRepository(
        tagRepositoryImpl: TagRepositoryImpl
    ): TagRepository
    
    @Singleton
    @Binds
    abstract fun bindDayRepository(
        dayRepositoryImpl: DayRepositoryImpl
    ): DayRepository
    
    @Singleton
    @Binds
    abstract fun bindWellnessRepository(
        wellnessRepositoryImpl: WellnessRepositoryImpl
    ): WellnessRepository
    
    // Material 3 Expressive Design-focused repositories
    
    /**
     * Provides the TimeBlockRepository implementation.
     * This repository follows Material 3 Expressive design by providing reactive data for timelines
     * and timeline visualizations with proper theming support.
     */
    @Singleton
    @Binds
    abstract fun bindTimeBlockRepository(
        timeBlockRepositoryImpl: TimeBlockRepositoryImpl
    ): TimeBlockRepository
    
    /**
     * Provides the CategoryRepository implementation.
     * This repository follows Material 3 Expressive design principles by providing
     * support for color system and dynamic iconography.
     */
    @Singleton
    @Binds
    abstract fun bindCategoryRepository(
        categoryRepositoryImpl: CategoryRepositoryImpl
    ): CategoryRepository
    
    /**
     * Provides the SprintRepository implementation.
     * This is a temporary in-memory implementation until the full data layer is rebuilt.
     */
    @Singleton
    @Binds
    abstract fun bindSprintRepository(
        sprintRepositoryImpl: SprintRepositoryImpl
    ): SprintRepository
    
    /**
     * Provides the GoalRepository implementation.
     * This is a temporary in-memory implementation until the full data layer is rebuilt.
     */
    @Singleton
    @Binds
    abstract fun bindGoalRepository(
        goalRepositoryImpl: GoalRepositoryImpl
    ): GoalRepository
    
    /**
     * Provides the TempDayRepository implementation.
     * This is a temporary repository used by day activity-related components
     * until the full Day repository implementation is complete.
     */
    @Singleton
    @Binds
    abstract fun bindTempDayRepository(
        tempDayRepositoryImpl: com.example.agilelifemanagement.domain.repository.temporary.TempDayRepositoryImpl
    ): com.example.agilelifemanagement.domain.repository.temporary.TempDayRepository
}
