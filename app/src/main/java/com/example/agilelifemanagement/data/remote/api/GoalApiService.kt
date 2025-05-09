package com.example.agilelifemanagement.data.remote.api

import android.util.Log
import com.example.agilelifemanagement.data.remote.SupabaseManager
import com.example.agilelifemanagement.data.remote.dto.GoalDto
import com.example.agilelifemanagement.domain.model.Result
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * API service for Goal operations with Supabase.
 * Implements security best practices according to the app's security implementation guidelines.
 */
@Singleton
class GoalApiService @Inject constructor(
    private val supabaseManager: SupabaseManager
) {
    private val tableName = "agile_life.goals"
    
    /**
     * Get a goal by ID from Supabase.
     */
    suspend fun getGoalById(goalId: String): Result<GoalDto> = withContext(Dispatchers.IO) {
        return@withContext try {
            val client = supabaseManager.getClient()
            val goal = client.postgrest[tableName]
                .select() {
                    filter {
                        eq("id", goalId)
                    }
                }
                .decodeSingle<GoalDto>()
            
            Result.Success(goal)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting goal by ID: ${e.message}", e)
            Result.Error("Failed to get goal: ${e.message}", e)
        }
    }
    
    /**
     * Get all goals for a user from Supabase.
     */
    suspend fun getGoalsByUserId(userId: String): Result<List<GoalDto>> = withContext(Dispatchers.IO) {
        return@withContext try {
            val client = supabaseManager.getClient()
            val goals = client.postgrest[tableName]
                .select() {
                    filter {
                        eq("user_id", userId)
                    }
                    order("created_at", Order.DESCENDING)
                }
                .decodeList<GoalDto>()
            
            Result.Success(goals)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting goals for user: ${e.message}", e)
            Result.Error("Failed to get goals: ${e.message}", e)
        }
    }
    
    /**
     * Get active goals for a user from Supabase.
     */
    suspend fun getActiveGoalsByUserId(userId: String): Result<List<GoalDto>> = withContext(Dispatchers.IO) {
        return@withContext try {
            val client = supabaseManager.getClient()
            val goals = client.postgrest[tableName]
                .select() {
                    filter {
                        eq("user_id", userId)
                        eq("is_completed", false)
                    }
                    order("deadline", Order.ASCENDING)
                }
                .decodeList<GoalDto>()
            
            Result.Success(goals)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting active goals: ${e.message}", e)
            Result.Error("Failed to get active goals: ${e.message}", e)
        }
    }
    
    /**
     * Create or update a goal in Supabase.
     */
    suspend fun upsertGoal(goalDto: GoalDto): Result<GoalDto> = withContext(Dispatchers.IO) {
        return@withContext try {
            val client = supabaseManager.getClient()
            
            // Check if goal exists
            val existingGoal = client.postgrest[tableName]
                .select() {
                    filter {
                        eq("id", goalDto.id)
                    }
                }
                .decodeAsOrNull<GoalDto>()
            
            val result = if (existingGoal != null) {
                // Update existing goal
                val updatedGoal = goalDto.copy(created_at = existingGoal.created_at)
                client.postgrest[tableName]
                    .update(updatedGoal) {
                        filter {
                            eq("id", goalDto.id)
                        }
                    }
                    .decodeSingle<GoalDto>()
            } else {
                // Insert new goal
                client.postgrest[tableName]
                    .insert(goalDto)
                    .decodeSingle<GoalDto>()
            }
            
            Result.Success(result)
        } catch (e: Exception) {
            Log.e(TAG, "Error upserting goal: ${e.message}", e)
            Result.Error("Failed to upsert goal: ${e.message}", e)
        }
    }
    
    /**
     * Delete a goal from Supabase.
     */
    suspend fun deleteGoal(goalId: String): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            val client = supabaseManager.getClient()
            client.postgrest[tableName]
                .delete {
                    filter {
                        eq("id", goalId)
                    }
                }
            
            Result.Success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting goal: ${e.message}", e)
            Result.Error("Failed to delete goal: ${e.message}", e)
        }
    }
    
    companion object {
        private const val TAG = "GoalApiService"
    }
}
