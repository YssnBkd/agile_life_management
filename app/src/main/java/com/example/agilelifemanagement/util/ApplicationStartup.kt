package com.example.agilelifemanagement.util

import android.util.Log
import com.example.agilelifemanagement.data.remote.SupabaseManager
import com.example.agilelifemanagement.data.remote.SupabaseRealtimeManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Handles application startup tasks such as initializing real-time synchronization.
 */
@Singleton
class ApplicationStartup @Inject constructor(
    private val supabaseManager: SupabaseManager,
    private val realtimeManager: SupabaseRealtimeManager
) {
    private val TAG = "ApplicationStartup"
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    init {
        // Setup real-time sync when the application starts
        initializeRealtimeSync()
    }

    private fun initializeRealtimeSync() {
        scope.launch {
            try {
                // Monitor auth state to initialize/cleanup real-time subscriptions
                supabaseManager.authState.collectLatest { authState ->
                    when (authState) {
                        is com.example.agilelifemanagement.data.remote.AuthState.Authenticated -> {
                            Log.d(TAG, "User authenticated, initializing real-time subscriptions")
                            realtimeManager.initializeSubscriptions(authState.userId)
                        }
                        is com.example.agilelifemanagement.data.remote.AuthState.NotAuthenticated -> {
                            Log.d(TAG, "User not authenticated, cleaning up real-time subscriptions")
                            realtimeManager.unsubscribeAll()
                        }
                        else -> {
                            // Do nothing for loading or error states
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error initializing real-time sync: ${e.message}", e)
            }
        }
    }
}
