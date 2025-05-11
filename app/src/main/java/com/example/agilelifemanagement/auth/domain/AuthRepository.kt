package com.example.agilelifemanagement.auth.domain

import com.example.agilelifemanagement.auth.domain.model.User
import com.example.agilelifemanagement.auth.domain.model.AuthError
import com.example.agilelifemanagement.domain.model.Result

/**
 * Repository interface for authentication operations.
 * All methods return the custom Result class for consistency.
 */
interface AuthRepository {
    suspend fun login(email: String, password: String): Result<User>
    suspend fun signUp(email: String, password: String): Result<User>
    suspend fun logout(): Result<Unit>
    suspend fun getCurrentUser(): Result<User>
    suspend fun isLoggedIn(): Boolean
}
