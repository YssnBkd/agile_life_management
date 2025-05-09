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
import io.github.jan.supabase.realtime.Realtime
import io.github.jan.supabase.realtime.realtime
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
                // v3.0.0 config: minimal setup
                autoSaveToStorage = true
                alwaysAutoRefresh = true
            }
            install(Postgrest) {
                defaultSchema = "agile_life"
            }
            install(Realtime)
            install(Storage)
        }.also {
            // Monitor auth state changes
            monitorAuthState(it)
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
                    else -> {
                        Log.e(TAG, "Unknown auth status: $status")
                        _authState.value = AuthState.Error("Unknown auth status: $status")
                    }
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
