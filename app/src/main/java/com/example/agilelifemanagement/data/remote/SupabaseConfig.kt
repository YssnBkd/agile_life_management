package com.example.agilelifemanagement.data.remote

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Configuration for Supabase credentials.
 * Implements secure storage of credentials using DataStore.
 * Following security implementation guidelines from global rules.
 */
@Singleton
class SupabaseConfig @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "supabase_config")
    
    private val supabaseUrlKey = stringPreferencesKey("supabase_url")
    private val supabaseKeyKey = stringPreferencesKey("supabase_key")
    
    /**
     * Get the Supabase URL from secure storage.
     */
    val supabaseUrl: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[supabaseUrlKey] ?: DEFAULT_SUPABASE_URL
    }
    
    /**
     * Get the Supabase key from secure storage.
     */
    val supabaseKey: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[supabaseKeyKey] ?: DEFAULT_SUPABASE_KEY
    }
    
    /**
     * Save the Supabase URL to secure storage.
     */
    suspend fun saveSupabaseUrl(url: String) {
        context.dataStore.edit { preferences ->
            preferences[supabaseUrlKey] = url
        }
    }
    
    /**
     * Save the Supabase key to secure storage.
     */
    suspend fun saveSupabaseKey(key: String) {
        context.dataStore.edit { preferences ->
            preferences[supabaseKeyKey] = key
        }
    }
    
    companion object {
        // These default values should be replaced with your actual Supabase credentials
        // In a production app, these should be stored in a secure way (e.g., BuildConfig)
        const val DEFAULT_SUPABASE_URL = "https://bipbeeqoqmoccbgwlqls.supabase.co"
        const val DEFAULT_SUPABASE_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImJpcGJlZXFvcW1vY2NiZ3dscWxzIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDY0OTIxNjcsImV4cCI6MjA2MjA2ODE2N30.Og10FxndlupYvnjym5_8DK_MI--Qrs5_V0F_HuvFpok"
    }
}
