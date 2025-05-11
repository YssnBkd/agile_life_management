package com.example.agilelifemanagement.data.remote.auth

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.agilelifemanagement.BuildConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Secure configuration for Supabase credentials.
 * This follows security best practices by not hardcoding API keys in the code.
 */
@Singleton
class SupabaseConfig @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    // Keys for DataStore
    // DataStore keys for secure storage, aligned with main SupabaseConfig
    private val supabaseUrlKey = stringPreferencesKey("supabase_url")
    private val supabaseKeyKey = stringPreferencesKey("supabase_key")
    private val supabaseAccessTokenKey = stringPreferencesKey("supabase_access_token")
    private val supabaseRefreshTokenKey = stringPreferencesKey("supabase_refresh_token")

    // Following security implementation guidelines from global rules.
    // Implements secure storage of credentials using DataStore.
    
    // Default values for development only (these should be replaced in production)
    companion object {
        // Default values are now referenced from BuildConfig for security and flexibility
        // These cannot be 'const val' because BuildConfig values are only available at runtime
        val DEFAULT_SUPABASE_URL = BuildConfig.SUPABASE_URL
        val DEFAULT_SUPABASE_KEY = BuildConfig.SUPABASE_KEY
    }
    
    // Get Supabase URL from DataStore or use default for development
    /**
     * Get the Supabase URL from secure storage.
     */
    val supabaseUrl: Flow<String> = dataStore.data.map { preferences ->
        preferences[supabaseUrlKey] ?: DEFAULT_SUPABASE_URL
    }
    
    // Get Supabase key from DataStore or use default for development
    val supabaseKey: Flow<String> = dataStore.data.map { preferences ->
        preferences[supabaseKeyKey] ?: DEFAULT_SUPABASE_KEY
    }
    
    // Get access token from DataStore
    val accessToken: Flow<String?> = dataStore.data.map { preferences ->
        preferences[supabaseAccessTokenKey]
    }
    
    // Get refresh token from DataStore
    val refreshToken: Flow<String?> = dataStore.data.map { preferences ->
        preferences[supabaseRefreshTokenKey]
    }
    
    // Save Supabase URL to DataStore
    suspend fun saveSupabaseUrl(url: String) {
        dataStore.edit { preferences ->
            preferences[supabaseUrlKey] = url
        }
    }
    
    // Save Supabase key to DataStore
    suspend fun saveSupabaseKey(key: String) {
        dataStore.edit { preferences ->
            preferences[supabaseKeyKey] = key
        }
    }
    
    // Save access token to DataStore
    suspend fun saveAccessToken(token: String?) {
        dataStore.edit { preferences ->
            if (token != null) {
                preferences[supabaseAccessTokenKey] = token
            } else {
                preferences.remove(supabaseAccessTokenKey)
            }
        }
    }
    
    // Save refresh token to DataStore
    suspend fun saveRefreshToken(token: String?) {
        dataStore.edit { preferences ->
            if (token != null) {
                preferences[supabaseRefreshTokenKey] = token
            } else {
                preferences.remove(supabaseRefreshTokenKey)
            }
        }
    }
    
    // Clear all tokens (for logout)
    suspend fun clearTokens() {
        dataStore.edit { preferences ->
            preferences.remove(supabaseAccessTokenKey)
            preferences.remove(supabaseRefreshTokenKey)
        }
    }
}
