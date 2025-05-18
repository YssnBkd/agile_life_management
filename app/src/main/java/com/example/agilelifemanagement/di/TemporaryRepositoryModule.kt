package com.example.agilelifemanagement.di

import com.example.agilelifemanagement.domain.repository.temporary.*
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Temporary repository module for the transitional architecture.
 * 
 * This module provides temporary repository implementations while
 * the data layer is being rebuilt (as per May 15, 2025 architectural update).
 * These will be gradually replaced as the real repository implementations are completed.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class TemporaryRepositoryModule {
    
    @Singleton
    @Binds
    abstract fun bindTemplateRepository(
        impl: TempTemplateRepositoryImpl
    ): TempTemplateRepository
    
    // DayRepository has been fully implemented and moved to RepositoryModule
    // This binding has been removed as the real implementation is now available
    
    @Singleton
    @Binds
    abstract fun bindNotificationRepository(
        impl: TempNotificationRepositoryImpl
    ): TempNotificationRepository
    
    @Singleton
    @Binds
    abstract fun bindCategoryRepository(
        impl: TempCategoryRepositoryImpl
    ): TempCategoryRepository
    
    // WellnessRepository has been fully implemented and moved to RepositoryModule
    // This binding has been commented out as the real implementation is now available
    // @Singleton
    // @Binds
    // abstract fun bindWellnessRepository(
    //     impl: TempWellnessRepositoryImpl
    // ): TempWellnessRepository
}
