package com.example.agilelifemanagement.data.remote.auth

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
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
    private object PreferencesKeys {
        val SUPABASE_URL = stringPreferencesKey("supabase_url")
        val SUPABASE_KEY = stringPreferencesKey("supabase_key")
        val SUPABASE_ACCESS_TOKEN = stringPreferencesKey("supabase_access_token")
        val SUPABASE_REFRESH_TOKEN = stringPreferencesKey("supabase_refresh_token")
    }
    
    // Default values for development only (these should be replaced in production)
    companion object {
        const val DEFAULT_SUPABASE_URL = "https://YOUR_PROJECT_ID.supabase.co"
        const val DEFAULT_SUPABASE_KEY = "YOUR_ANON_KEY" // This is the public anon key, not the service role key
    }
    
    // Get Supabase URL from DataStore or use default for development
    val supabaseUrl: Flow<String> = dataStore.data.map { preferences ->
        preferences[PreferencesKeys.SUPABASE_URL] ?: DEFAULT_SUPABASE_URL
    }
    
    // Get Supabase key from DataStore or use default for development
    val supabaseKey: Flow<String> = dataStore.data.map { preferences ->
        preferences[PreferencesKeys.SUPABASE_KEY] ?: DEFAULT_SUPABASE_KEY
    }
    
    // Get access token from DataStore
    val accessToken: Flow<String?> = dataStore.data.map { preferences ->
        preferences[PreferencesKeys.SUPABASE_ACCESS_TOKEN]
    }
    
    // Get refresh token from DataStore
    val refreshToken: Flow<String?> = dataStore.data.map { preferences ->
        preferences[PreferencesKeys.SUPABASE_REFRESH_TOKEN]
    }
    
    // Save Supabase URL to DataStore
    suspend fun saveSupabaseUrl(url: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.SUPABASE_URL] = url
        }
    }
    
    // Save Supabase key to DataStore
    suspend fun saveSupabaseKey(key: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.SUPABASE_KEY] = key
        }
    }
    
    // Save access token to DataStore
    suspend fun saveAccessToken(token: String?) {
        dataStore.edit { preferences ->
            if (token != null) {
                preferences[PreferencesKeys.SUPABASE_ACCESS_TOKEN] = token
            } else {
                preferences.remove(PreferencesKeys.SUPABASE_ACCESS_TOKEN)
            }
        }
    }
    
    // Save refresh token to DataStore
    suspend fun saveRefreshToken(token: String?) {
        dataStore.edit { preferences ->
            if (token != null) {
                preferences[PreferencesKeys.SUPABASE_REFRESH_TOKEN] = token
            } else {
                preferences.remove(PreferencesKeys.SUPABASE_REFRESH_TOKEN)
            }
        }
    }
    
    // Clear all tokens (for logout)
    suspend fun clearTokens() {
        dataStore.edit { preferences ->
            preferences.remove(PreferencesKeys.SUPABASE_ACCESS_TOKEN)
            preferences.remove(PreferencesKeys.SUPABASE_REFRESH_TOKEN)
        }
    }
}
