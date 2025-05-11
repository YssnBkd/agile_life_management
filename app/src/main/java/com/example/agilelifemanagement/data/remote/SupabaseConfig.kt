package com.example.agilelifemanagement.data.remote

import com.example.agilelifemanagement.BuildConfig

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
        // Default values are now injected from BuildConfig (see build.gradle.kts). Do not commit real secrets!
        val DEFAULT_SUPABASE_URL: String = BuildConfig.SUPABASE_URL
        val DEFAULT_SUPABASE_KEY: String = BuildConfig.SUPABASE_KEY
    }
}
