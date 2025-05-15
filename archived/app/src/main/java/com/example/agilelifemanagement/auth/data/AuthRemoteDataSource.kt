package com.example.agilelifemanagement.auth.data

import com.example.agilelifemanagement.auth.domain.model.User
import com.example.agilelifemanagement.domain.model.Result

/**
 * Handles remote authentication (Supabase Auth API).
 * All methods return the custom Result class.
 */
interface AuthRemoteDataSource {
    suspend fun login(email: String, password: String): Result<User>
suspend fun signUp(email: String, password: String): Result<User>
    suspend fun logout(): Result<Unit>
    suspend fun getCurrentUser(): Result<User>
}
