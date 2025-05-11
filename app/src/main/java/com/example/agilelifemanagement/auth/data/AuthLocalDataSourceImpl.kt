package com.example.agilelifemanagement.auth.data

import com.example.agilelifemanagement.auth.domain.model.User
import com.example.agilelifemanagement.domain.model.Result
import javax.inject.Inject

/**
 * Implementation of AuthLocalDataSource using Encrypted DataStore.
 * Handles secure local storage and retrieval of user/session data.
 * All methods return the custom Result class.
 */
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import kotlinx.coroutines.flow.first
import java.io.IOException

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth_prefs")

class AuthLocalDataSourceImpl @Inject constructor(
    private val context: Context,
    private val gson: Gson
) : AuthLocalDataSource {
    companion object {
        private val USER_KEY = stringPreferencesKey("user")
        private val SESSION_TOKEN_KEY = stringPreferencesKey("session_token")
    }
    override suspend fun saveUser(user: User): Result<Unit> {
        return try {
            context.dataStore.edit { prefs: MutablePreferences ->
                prefs[USER_KEY] = gson.toJson(user)
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error("Failed to save user", e)
        }
    }

    override suspend fun getUser(): Result<User> {
        return try {
            val prefs = context.dataStore.data.first()
            val userJson = prefs[USER_KEY]
            if (userJson != null) {
                val user: User = gson.fromJson(userJson, User::class.java)
                Result.Success(user)
            } else {
                Result.Error("No user found")
            }
        } catch (e: Exception) {
            Result.Error("Failed to retrieve user", e)
        }
    }

    override suspend fun clearUser(): Result<Unit> {
        return try {
            context.dataStore.edit { prefs: MutablePreferences ->
                prefs.remove(USER_KEY)
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error("Failed to clear user", e)
        }
    }

    override suspend fun saveSessionToken(token: String): Result<Unit> {
        return try {
            context.dataStore.edit { prefs: MutablePreferences ->
                prefs[SESSION_TOKEN_KEY] = token
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error("Failed to save session token", e)
        }
    }

    override suspend fun getSessionToken(): Result<String> {
        return try {
            val prefs = context.dataStore.data.first()
            val token: String? = prefs[SESSION_TOKEN_KEY]
            if (token != null) {
                Result.Success(token)
            } else {
                Result.Error("No session token found")
            }
        } catch (e: Exception) {
            Result.Error("Failed to retrieve session token", e)
        }
    }

    override suspend fun clearSessionToken(): Result<Unit> {
        return try {
            context.dataStore.edit { prefs: MutablePreferences ->
                prefs.remove(SESSION_TOKEN_KEY)
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error("Failed to clear session token", e)
        }
    }
}

