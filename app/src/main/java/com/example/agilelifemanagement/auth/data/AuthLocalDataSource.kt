package com.example.agilelifemanagement.auth.data

import com.example.agilelifemanagement.auth.domain.model.User
import com.example.agilelifemanagement.domain.model.Result

/**
 * Handles local authentication/session storage (Encrypted DataStore).
 * All methods return the custom Result class.
 */
interface AuthLocalDataSource {
    suspend fun saveUser(user: User): Result<Unit>
    suspend fun getUser(): Result<User>
    suspend fun clearUser(): Result<Unit>
    suspend fun saveSessionToken(token: String): Result<Unit>
    suspend fun getSessionToken(): Result<String>
    suspend fun clearSessionToken(): Result<Unit>
}
