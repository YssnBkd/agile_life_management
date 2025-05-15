package com.example.agilelifemanagement.auth.data

import com.example.agilelifemanagement.auth.domain.model.User
import com.example.agilelifemanagement.domain.model.Result
import javax.inject.Inject
import com.example.agilelifemanagement.data.remote.SupabaseManager
import io.github.jan.supabase.auth.auth

/**
 * Implementation of AuthRemoteDataSource using Supabase Auth API.
 * Handles login, logout, and user fetch operations.
 * All methods return the custom Result class.
 */
class AuthRemoteDataSourceImpl @Inject constructor(
    private val supabaseManager: SupabaseManager
) : AuthRemoteDataSource {
    override suspend fun login(email: String, password: String): Result<User> {
        return try {
            val loginResult = supabaseManager.signIn(email, password)
            when (loginResult) {
                is Result.Success -> {
                    val client = supabaseManager.getClient()
                    val supabaseUser = client.auth.retrieveUserForCurrentSession()
                    val user = User(
    id = supabaseUser.id,
    name = supabaseUser.userMetadata?.get("name") as? String,
    email = supabaseUser.email ?: email,
    profileImageUrl = supabaseUser.userMetadata?.get("avatar_url") as? String,
    createdAt = supabaseUser.createdAt?.toString(),
    updatedAt = supabaseUser.updatedAt?.toString()
)
                    Result.Success<User>(user)
                }
                is Result.Error -> Result.Error(loginResult.message, loginResult.exception)
                is Result.Loading -> Result.Loading
            }
        } catch (e: Exception) {
            Result.Error("Login failed: ${e.message}", e)
        }
    }

    override suspend fun signUp(email: String, password: String): Result<User> {
        return try {
            val signUpResult = supabaseManager.signUp(email, password)
            when (signUpResult) {
                is Result.Success -> {
                    val client = supabaseManager.getClient()
                    val supabaseUser = client.auth.retrieveUserForCurrentSession()
                    val user = User(
    id = supabaseUser.id,
    name = supabaseUser.userMetadata?.get("name") as? String,
    email = supabaseUser.email ?: email,
    profileImageUrl = supabaseUser.userMetadata?.get("avatar_url") as? String,
    createdAt = supabaseUser.createdAt?.toString(),
    updatedAt = supabaseUser.updatedAt?.toString()
)
                    Result.Success<User>(user)
                }
                is Result.Error -> Result.Error(signUpResult.message, signUpResult.exception)
                is Result.Loading -> Result.Loading
            }
        } catch (e: Exception) {
            Result.Error("Sign up failed: ${e.message}", e)
        }
    }

    override suspend fun logout(): Result<Unit> {
        return try {
            val result = supabaseManager.signOut()
            when (result) {
                is Result.Success -> Result.Success<Unit>(Unit)
                is Result.Error -> Result.Error(result.message, result.exception)
                is Result.Loading -> Result.Loading
            }
        } catch (e: Exception) {
            Result.Error("Logout failed: ${e.message}", e)
        }
    }

    override suspend fun getCurrentUser(): Result<User> {
        return try {
            val client = supabaseManager.getClient()
            val supabaseUser = client.auth.retrieveUserForCurrentSession()
            val user = User(
                id = supabaseUser.id,
                name = supabaseUser.userMetadata?.get("name") as? String,
                email = supabaseUser.email ?: "",
                profileImageUrl = supabaseUser.userMetadata?.get("avatar_url") as? String,
                createdAt = supabaseUser.createdAt?.toString(),
                updatedAt = supabaseUser.updatedAt?.toString()
            )
            Result.Success<User>(user)
        } catch (e: Exception) {
            Result.Error("Failed to fetch current user: ${e.message}", e)
        }
    }
}
