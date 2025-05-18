package com.example.agilelifemanagement.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module that provides application-wide dependencies.
 * This module contains general dependencies that are used across the app.
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    // Dispatcher providers moved to DispatcherModule.kt
    // Add application-wide dependencies here
}

