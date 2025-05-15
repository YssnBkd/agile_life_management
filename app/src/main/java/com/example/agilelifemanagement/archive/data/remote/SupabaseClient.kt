package com.example.agilelifemanagement.data.remote

import android.util.Log
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.AuthConfig
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.status.SessionStatus
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.realtime.channel
import io.github.jan.supabase.realtime.postgresChangeFlow
import io.github.jan.supabase.realtime.realtime
import io.github.jan.supabase.realtime.RealtimeChannel
import io.github.jan.supabase.realtime.PostgresAction
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton
import com.example.agilelifemanagement.data.remote.auth.SupabaseConfig
import com.example.agilelifemanagement.domain.model.Result
import com.example.agilelifemanagement.util.NetworkMonitor

/**
 * Supabase client for handling remote data operations.
 * This class manages the connection to Supabase and provides methods for data synchronization.
 * Implements security best practices according to the app's security implementation guidelines.
 */
import io.github.jan.supabase.realtime.Realtime

@Singleton
class SupabaseManager @Inject constructor(
    private val supabaseConfig: SupabaseConfig,
    private val networkMonitor: NetworkMonitor
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    // Authentication state
    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    // Client initialization
    private var _client: SupabaseClient? = null

    // Initialize the client lazily
    suspend fun getClient(): SupabaseClient {
        return _client ?: createClient().also { _client = it }
    }

    /**
     * Listen to realtime changes for the 'tasks' table using Supabase v3.1.2 APIs.
     * This implementation uses the new channel-based approach in Supabase v3.1.2.
     */
    private fun listenToTaskChanges(client: SupabaseClient) {
        scope.launch {
            try {
                // Create a channel for tasks table
                val channel = client.channel("tasks-channel")

                // Listen for INSERT events
                val insertFlow = channel.postgresChangeFlow<PostgresAction.Insert>(
                    schema = "agile_life"
                ) {
                    table = "tasks"
                }

                // Listen for UPDATE events
                val updateFlow = channel.postgresChangeFlow<PostgresAction.Update>(
                    schema = "agile_life"
                ) {
                    table = "tasks"
                }

                // Listen for DELETE events
                val deleteFlow = channel.postgresChangeFlow<PostgresAction.Delete>(
                    schema = "agile_life"
                ) {
                    table = "tasks"
                }

                // Subscribe to the channel
                channel.subscribe()

                // Launch coroutines to collect changes
                scope.launch {
                    try {
                        insertFlow.collect { action ->
                            Log.d(TAG, "Task inserted: ${action.record}")
                            // Handle insert - you can parse action.record to your data model
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Error in insert flow: ${e.message}", e)
                    }
                }

                scope.launch {
                    try {
                        updateFlow.collect { action ->
                            Log.d(TAG, "Task updated: New=${action.record}, Old=${action.oldRecord}")
                            // Handle update - you can access both the new record and old record
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Error in update flow: ${e.message}", e)
                    }
                }

                scope.launch {
                    try {
                        deleteFlow.collect { action ->
                            Log.d(TAG, "Task deleted: ${action.oldRecord}")
                            // Handle delete - action.oldRecord contains the deleted record
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Error in delete flow: ${e.message}", e)
                    }
                }

                Log.d(TAG, "Started listening to task changes")
            } catch (e: Exception) {
                Log.e(TAG, "Error setting up realtime listeners: ${e.message}", e)
            }
        }
    }

    // Create a new Supabase client with the current configuration
    private suspend fun createClient(): SupabaseClient {
        val url = supabaseConfig.supabaseUrl.first()
        val key = supabaseConfig.supabaseKey.first()
        Log.d(TAG, "Creating Supabase client with URL: $url")
        return createSupabaseClient(
            supabaseUrl = url,
            supabaseKey = key
        ) {
            install(Auth) {
                autoSaveToStorage = true
                alwaysAutoRefresh = true
            }
            install(Postgrest) {
                defaultSchema = "agile_life"
            }
            install(Realtime) {
                // Configure Realtime here if needed
            }
            install(Storage)
        }.also {
            // Monitor auth state changes
            monitorAuthState(it)
            // Example: Start listening to realtime changes for the 'tasks' table
            listenToTaskChanges(it)
        }
    }

    // Monitor authentication state changes
    private fun monitorAuthState(client: SupabaseClient) {
        scope.launch {
            client.auth.sessionStatus.collect { status ->
                when (status) {
                    is SessionStatus.Authenticated -> {
                        Log.d(TAG, "User authenticated")
                        // Save tokens securely
                        supabaseConfig.saveAccessToken(status.session.accessToken)
                        supabaseConfig.saveRefreshToken(status.session.refreshToken)
                        val user = client.auth.retrieveUserForCurrentSession()
                        _authState.value = AuthState.Authenticated(user?.id ?: "")
                    }
                    is SessionStatus.NotAuthenticated -> {
                        Log.d(TAG, "User not authenticated")
                        // Clear tokens
                        supabaseConfig.clearTokens()
                        _authState.value = AuthState.NotAuthenticated
                    }
                    is SessionStatus.Initializing -> {
                        Log.d(TAG, "Loading auth state from storage")
                        _authState.value = AuthState.Loading
                    }
                    is SessionStatus.RefreshFailure -> {
                        Log.e(TAG, "Auth refresh failure: ${status.cause}")
                        _authState.value = AuthState.Error("Auth refresh failure: ${status.cause}")
                    }
                    // All cases are now handled above
                }
            }
        }
    }

    // Sign up with email and password
    suspend fun signUp(email: String, password: String): Result<String> {
        return try {
            if (!networkMonitor.isOnline()) {
                return Result.Error("No internet connection")
            }

            val client = getClient()
            // v3.0.0 changed the sign-up API
            val response = client.auth.signUpWith(Email) {
                this.email = email
                this.password = password
            }

            // In v3.0.0 user might be null in some auth flows
            val user = client.auth.retrieveUserForCurrentSession()
            val userId = user?.id
            Log.d(TAG, "Sign up successful: $userId")
            Result.Success(userId ?: "")
        } catch (e: Exception) {
            Log.e(TAG, "Sign up error: ${e.message}", e)
            Result.Error("Sign up failed: ${e.message}")
        }
    }

    // Sign in with email and password
    suspend fun signIn(email: String, password: String): Result<String> {
        return try {
            if (!networkMonitor.isOnline()) {
                return Result.Error("No internet connection")
            }

            val client = getClient()
            // v3.0.0 changed the sign-in API
            val response = client.auth.signInWith(Email) {
                this.email = email
                this.password = password
            }

            // In v3.0.0 user might be null in some auth flows
            val user = client.auth.retrieveUserForCurrentSession()
            val userId = user?.id
            Log.d(TAG, "Sign in successful: $userId")
            Result.Success(userId ?: "")
        } catch (e: Exception) {
            Log.e(TAG, "Sign in error: ${e.message}", e)
            Result.Error("Sign in failed: ${e.message}")
        }
    }

    // Sign out
    suspend fun signOut(): Result<Unit> {
        return try {
            if (!networkMonitor.isOnline()) {
                return Result.Error("No internet connection")
            }

            val client = getClient()
            client.auth.signOut()

            Log.d(TAG, "Sign out successful")
            Result.Success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Sign out error: ${e.message}", e)
            Result.Error("Sign out failed: ${e.message}")
        }
    }

    // Reset password
    suspend fun resetPassword(email: String): Result<Unit> {
        return try {
            if (!networkMonitor.isOnline()) {
                return Result.Error("No internet connection")
            }

            val client = getClient()
            client.auth.resetPasswordForEmail(email)

            Log.d(TAG, "Password reset email sent to $email")
            Result.Success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Password reset error: ${e.message}", e)
            Result.Error("Password reset failed: ${e.message}")
        }
    }

    // Get current user ID
    fun getCurrentUserId(): Flow<String?> {
        return authState.map { state ->
            when (state) {
                is AuthState.Authenticated -> state.userId
                else -> null
            }
        }
    }

    // Close all realtime subscriptions (channels)
    suspend fun closeAllSubscriptions() {
        getClient().realtime.removeAllChannels()
    }
    
    /**
     * Subscribe to a specific table for real-time updates.
     * 
     * @param schema The database schema name
     * @param table The table name
     * @param filter Optional filter string in PostgresREST format (e.g. "user_id=eq.123")
     * @param onInsert Callback function for INSERT events
     * @param onUpdate Callback function for UPDATE events
     * @param onDelete Callback function for DELETE events
     * @return Flow of RealtimeChannel.Status representing the connection status
     */
    suspend fun subscribeToTable(
        schema: String,
        table: String,
        filter: String? = null,
        onInsert: (record: kotlinx.serialization.json.JsonElement) -> Unit = {},
        onUpdate: (old: kotlinx.serialization.json.JsonElement, new: kotlinx.serialization.json.JsonElement) -> Unit = { _, _ -> },
        onDelete: (old: kotlinx.serialization.json.JsonElement) -> Unit = {}
    ): Flow<RealtimeChannel.Status> {
        val client = getClient()
        
        // Create a unique channel name based on schema, table and filter
        val channelName = "${schema}_${table}_${filter?.hashCode() ?: "all"}"
        
        // Create and configure channel
        val channel = client.channel(channelName)
        
        // Set up listeners for different PostgreSQL actions using the PostgresChangeFlow builder API
        // Note: The event type is determined by the generic type parameter (Insert/Update/Delete)
        
        // INSERT events
        val insertFlow = channel.postgresChangeFlow<PostgresAction.Insert>(
            schema = schema
        ) {
            this.table = table
            // Filter will be manually applied in the collect lambda
        }
        
        // UPDATE events
        val updateFlow = channel.postgresChangeFlow<PostgresAction.Update>(
            schema = schema
        ) {
            this.table = table
            // Filter will be manually applied in the collect lambda
        }
        
        // DELETE events
        val deleteFlow = channel.postgresChangeFlow<PostgresAction.Delete>(
            schema = schema
        ) {
            this.table = table
            // Filter will be manually applied in the collect lambda
        }
        
        // Start collecting from flows in separate coroutines
        scope.launch {
            insertFlow.collect { action ->
                try {
                    // Apply filter manually if needed
                    if (filter == null || matchesFilter(action.record, filter)) {
                        onInsert(action.record)
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error handling insert event for $schema.$table: ${e.message}", e)
                }
            }
        }
        
        scope.launch {
            updateFlow.collect { action ->
                try {
                    // Apply filter manually if needed
                    if (filter == null || matchesFilter(action.record, filter)) {
                        onUpdate(action.oldRecord, action.record)
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error handling update event for $schema.$table: ${e.message}", e)
                }
            }
        }
        
        scope.launch {
            deleteFlow.collect { action ->
                try {
                    // Apply filter manually if needed
                    if (filter == null || matchesFilter(action.oldRecord, filter)) {
                        onDelete(action.oldRecord)
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error handling delete event for $schema.$table: ${e.message}", e)
                }
            }
        }
        
        // Subscribe to the channel
        channel.subscribe()
        
        // Return the channel status as a Flow
        return channel.status
    }
    
    /**
     * Helper method to check if a JSON record matches the given filter
     */
    private fun matchesFilter(record: kotlinx.serialization.json.JsonElement, filter: String): Boolean {
        try {
            // Parse filter in format like "user_id=eq.123"
            val parts = filter.split("=")
            if (parts.size != 2) return false
            
            val fieldName = parts[0]
            val operatorValue = parts[1]
            
            // Parse operator and value (e.g., "eq.123" -> operator="eq", value="123")
            val operatorValueParts = operatorValue.split(".", limit = 2)
            if (operatorValueParts.size != 2) return false
            
            val operator = operatorValueParts[0]
            val value = operatorValueParts[1]
            
            // Extract the field value from the record
            val recordObj = record as? kotlinx.serialization.json.JsonObject ?: return false
            val fieldElement = recordObj[fieldName] ?: return false
            val fieldValue = when (fieldElement) {
                is kotlinx.serialization.json.JsonPrimitive -> fieldElement.content
                else -> return false // Can only compare primitive values
            }
            
            // Compare based on operator
            return when (operator) {
                "eq" -> fieldValue == value
                // Add more operators as needed
                else -> false
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error applying filter: ${e.message}", e)
            return false
        }
    }

    companion object {
        private const val TAG = "SupabaseManager"
    }
}

/**
 * Authentication state for the app.
 */
sealed class AuthState {
    object Loading : AuthState()
    data class Authenticated(val userId: String) : AuthState()
    object NotAuthenticated : AuthState()
    data class Error(val message: String) : AuthState()
}