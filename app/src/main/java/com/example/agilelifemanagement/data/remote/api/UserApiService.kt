package com.example.agilelifemanagement.data.remote.api

import android.util.Log
import com.example.agilelifemanagement.data.remote.SupabaseManager
import com.example.agilelifemanagement.data.remote.dto.UserDto
import com.example.agilelifemanagement.domain.model.Result
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * API service for User operations with Supabase.
 * Implements security best practices according to the app's security implementation guidelines.
 */
@Singleton
class UserApiService @Inject constructor(
    private val supabaseManager: SupabaseManager
) {
    private val tableName = "users"
    
    /**
     * Get a user by ID from Supabase.
     */
    suspend fun getUserById(userId: String): Result<UserDto> = withContext(Dispatchers.IO) {
        return@withContext try {
            val client = supabaseManager.getClient()
            val user = client.postgrest[tableName]
                .select() {
                    filter {
                        eq("id", userId)
                    }
                }
                .decodeSingle<UserDto>()
            
            Result.Success(user)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting user by ID: ${e.message}", e)
            Result.Error("Failed to get user: ${e.message}", e)
        }
    }
    
    /**
     * Create or update a user in Supabase.
     */
    suspend fun upsertUser(userDto: UserDto): Result<UserDto> = withContext(Dispatchers.IO) {
        return@withContext try {
            val client = supabaseManager.getClient()
            
            // Check if user exists
            val exists = client.postgrest[tableName]
                .select() {
                    filter {
                        eq("id", userDto.id)
                    }
                }
                .decodeList<UserDto>()
                .isNotEmpty()
            
            if (exists) {
                // Update existing user
                client.postgrest[tableName]
                    .update(mapOf(
                        "name" to userDto.name,
                        "email" to userDto.email,
                        "profile_image_url" to userDto.profile_image_url,
                        "updated_at" to System.currentTimeMillis()
                    )) {
                        filter {
                            eq("id", userDto.id)
                        }
                    }
            } else {
                // Insert new user
                client.postgrest[tableName]
                    .insert(userDto)
            }
            
            Result.Success(userDto)
        } catch (e: Exception) {
            Log.e(TAG, "Error upserting user: ${e.message}", e)
            Result.Error("Failed to save user: ${e.message}", e)
        }
    }
    
    /**
     * Delete a user from Supabase.
     */
    suspend fun deleteUser(userId: String): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            val client = supabaseManager.getClient()
            client.postgrest[tableName]
                .delete {
                    filter {
                        eq("id", userId)
                    }
                }
            
            Result.Success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting user: ${e.message}", e)
            Result.Error("Failed to delete user: ${e.message}", e)
        }
    }
    
    companion object {
        private const val TAG = "UserApiService"
    }
}
