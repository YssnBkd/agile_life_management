package com.example.agilelifemanagement

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

/**
 * Application class for AgileLifeManagement app.
 * This class initializes key app-wide components and configurations.
 * 
 * The app follows Material 3 Expressive design principles for a cohesive,
 * visually consistent user experience with support for dynamic color theming.
 * 
 * @deprecated This class is deprecated. Use AgileLifeApplication instead, which provides
 * expanded functionality including dependency injection through Hilt.
 */
@Deprecated("Use AgileLifeApplication instead")
class AgileLifeApp : Application() {

    override fun onCreate() {
        super.onCreate()
        
        // Initialize Timber for logging
        if (isDebugBuild()) {
            Timber.plant(Timber.DebugTree())
        }
        
        // Initialize other app-wide components here
    }
    
    private fun isDebugBuild(): Boolean {
        return BuildConfig.DEBUG
    }
}
