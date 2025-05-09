package com.example.agilelifemanagement

import android.app.Application
import com.example.agilelifemanagement.BuildConfig
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * Application class for the Agile Life Management app.
 * Initializes Hilt for dependency injection and sets up global components.
 */
@HiltAndroidApp
class AgileLifeApplication : Application() {

    // Application-wide coroutine scope
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    override fun onCreate() {
        super.onCreate()
        
        // Initialize Timber for logging
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        
        // Initialize application-wide components
        applicationScope.launch {
            initializeComponents()
        }
    }
    
    private suspend fun initializeComponents() {
        // Initialize any application-wide components here
        // For example, sync manager, work manager jobs, etc.
    }
}
