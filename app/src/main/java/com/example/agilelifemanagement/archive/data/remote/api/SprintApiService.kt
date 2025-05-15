package com.example.agilelifemanagement.data.remote.api

import android.util.Log
import com.example.agilelifemanagement.data.remote.SupabaseManager
import com.example.agilelifemanagement.data.remote.dto.SprintDto
import com.example.agilelifemanagement.domain.model.Result
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order
import io.github.jan.supabase.postgrest.query.PostgrestRequestBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * API service for Sprint operations with Supabase.
 * Implements security best practices according to the app's security implementation guidelines.
 */
@Singleton
class SprintApiService @Inject constructor(
    private val supabaseManager: SupabaseManager
) {
    private val tableName = "sprints"
    
    /**
     * Get a sprint by ID from Supabase.
     */
    suspend fun getSprintById(sprintId: String): Result<SprintDto> = withContext(Dispatchers.IO) {
        return@withContext try {
            val client = supabaseManager.getClient()
            val sprint = client.postgrest[tableName]
                .select() {
                    filter {
                        eq("id", sprintId)
                    }
                }
                .decodeSingle<SprintDto>()
            
            Result.Success(sprint)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting sprint by ID: ${e.message}", e)
            Result.Error("Failed to get sprint: ${e.message}", e)
        }
    }
    
    /**
     * Get all sprints for a user from Supabase.
     */
    suspend fun getSprintsByUserId(userId: String): Result<List<SprintDto>> = withContext(Dispatchers.IO) {
        return@withContext try {
            val client = supabaseManager.getClient()
            val sprints = client.postgrest[tableName]
                .select() {
                    filter {
                        eq("user_id", userId)
                    }
                    order("start_date", Order.DESCENDING)
                }
                .decodeList<SprintDto>()
            
            Result.Success(sprints)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting sprints for user: ${e.message}", e)
            Result.Error("Failed to get sprints: ${e.message}", e)
        }
    }
    
    /**
     * Get active sprint for a user from Supabase.
     */
    suspend fun getActiveSprintByUserId(userId: String): Result<SprintDto?> = withContext(Dispatchers.IO) {
        return@withContext try {
            val client = supabaseManager.getClient()
            val sprints = client.postgrest[tableName]
                .select() {
                    filter {
                        eq("user_id", userId)
                        eq("is_active", true)
                    }
                }
                .decodeList<SprintDto>()
            
            if (sprints.isNotEmpty()) {
                Result.Success(sprints.first())
            } else {
                Result.Success(null)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting active sprint: ${e.message}", e)
            Result.Error("Failed to get active sprint: ${e.message}", e)
        }
    }
    
    /**
     * Create or update a sprint in Supabase.
     */
    suspend fun upsertSprint(sprintDto: SprintDto): Result<SprintDto> = withContext(Dispatchers.IO) {
        return@withContext try {
            val client = supabaseManager.getClient()
            
            val exists = client.postgrest[tableName]
                .select() {
                    filter {
                        eq("id", sprintDto.id)
                    }
                }
                .decodeList<SprintDto>()
                .isNotEmpty()
            
            if (exists) {
                // Update existing sprint
                client.postgrest[tableName]
                    .update({
                        set("name", sprintDto.name)
                        set("summary", sprintDto.summary)
                        set("start_date", sprintDto.start_date)
                        set("end_date", sprintDto.end_date)
                        set("is_active", sprintDto.is_active)
                        set("is_completed", sprintDto.is_completed)
                        set("updated_at", System.currentTimeMillis())
                    }) {
                        filter {
                            eq("id", sprintDto.id)
                        }
                    }
            } else {
                // Insert new sprint
                client.postgrest[tableName]
                    .insert(sprintDto)
            }
            
            Result.Success(sprintDto)
        } catch (e: Exception) {
            Log.e(TAG, "Error upserting sprint: ${e.message}", e)
            Result.Error("Failed to save sprint: ${e.message}", e)
        }
    }
    
    /**
     * Mark a sprint as completed in Supabase.
     */
    suspend fun markSprintCompleted(sprintId: String): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            val client = supabaseManager.getClient()
            client.postgrest[tableName]
                .update({
                    set("is_completed", true)
                    set("is_active", false)
                    set("updated_at", System.currentTimeMillis())
                }) {
                    filter {
                        eq("id", sprintId)
                    }
                }
            
            Result.Success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error marking sprint as completed: ${e.message}", e)
            Result.Error("Failed to mark sprint as completed: ${e.message}", e)
        }
    }
    
    /**
     * Delete a sprint from Supabase.
     */
    suspend fun deleteSprint(sprintId: String): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            val client = supabaseManager.getClient()
            client.postgrest[tableName]
                .delete {
                    filter {
                        eq("id", sprintId)
                    }
                }
            
            Result.Success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting sprint: ${e.message}", e)
            Result.Error("Failed to delete sprint: ${e.message}", e)
        }
    }
    
    companion object {
        private const val TAG = "SprintApiService"
    }
}
