package com.example.agilelifemanagement.auth.domain

import com.example.agilelifemanagement.auth.data.AuthRemoteDataSource
import com.example.agilelifemanagement.auth.data.AuthLocalDataSource
import com.example.agilelifemanagement.auth.domain.model.User
import com.example.agilelifemanagement.domain.model.Result
import com.example.agilelifemanagement.auth.domain.model.toUserEntity
import javax.inject.Inject

/**
 * Implementation of AuthRepository.
 * Coordinates between remote and local data sources.
 * Applies offline-first, security, and error-handling strategies.
 */
import com.example.agilelifemanagement.data.local.dao.UserDao
import com.example.agilelifemanagement.data.local.entity.UserEntity

class AuthRepositoryImpl @Inject constructor(
    private val remoteDataSource: AuthRemoteDataSource,
    private val localDataSource: AuthLocalDataSource,
    private val userDao: UserDao
) : AuthRepository {
    override suspend fun login(email: String, password: String): Result<User> {
        // Try remote login with Supabase
        val remoteResult = remoteDataSource.login(email, password)
        return when (remoteResult) {
            is Result.Success -> {
                // Save user locally (session token logic can be added if available)
                val user = remoteResult.data
                val userSaveResult = localDataSource.saveUser(user)
                // Save user in Room (upsert)
                userDao.upsert(user.toUserEntity())
                if (userSaveResult is Result.Success) {
                    Result.Success(user)
                } else {
                    Result.Error("Login succeeded, but failed to save user locally")
                }
            }
            is Result.Error -> Result.Error(remoteResult.message, remoteResult.exception)
            is Result.Loading -> Result.Loading
        }
    }
    override suspend fun signUp(email: String, password: String): Result<User> {
        val remoteResult = remoteDataSource.signUp(email, password)
        return when (remoteResult) {
            is Result.Success -> {
                val user = remoteResult.data
                val userSaveResult = localDataSource.saveUser(user)
                // Save user in Room (upsert)
                userDao.upsert(user.toUserEntity())
                if (userSaveResult is Result.Success) {
                    Result.Success(user)
                } else {
                    Result.Error("Sign up succeeded, but failed to save user locally")
                }
            }
            is Result.Error -> Result.Error(remoteResult.message, remoteResult.exception)
            is Result.Loading -> Result.Loading
        }
    }
    override suspend fun logout(): Result<Unit> {
        // Call remote logout (if implemented)
        val remoteResult = remoteDataSource.logout()
        // Always clear local user/session
        val clearUserResult = localDataSource.clearUser()
        val clearTokenResult = localDataSource.clearSessionToken()
        return if (clearUserResult is Result.Success && clearTokenResult is Result.Success) {
            Result.Success(Unit)
        } else {
            Result.Error("Failed to clear local session")
        }
    }
    override suspend fun getCurrentUser(): Result<User> {
        // Try to get user from local storage
        val localResult = localDataSource.getUser()
        return when (localResult) {
            is Result.Success -> localResult
            is Result.Error -> Result.Error("No user found locally", localResult.exception)
            is Result.Loading -> Result.Loading
        }
    }
    override suspend fun isLoggedIn(): Boolean {
        // User is considered logged in if both user and session token exist locally
        val userResult = localDataSource.getUser()
        val tokenResult = localDataSource.getSessionToken()
        return userResult is Result.Success && tokenResult is Result.Success
    }
}
