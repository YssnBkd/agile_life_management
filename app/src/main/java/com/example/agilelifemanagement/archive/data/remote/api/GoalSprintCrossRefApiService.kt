package com.example.agilelifemanagement.data.remote.api

import android.util.Log
import com.example.agilelifemanagement.data.remote.SupabaseManager
import com.example.agilelifemanagement.data.remote.dto.GoalSprintCrossRefDto
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
 * API service for Goal-Sprint cross-references operations with Supabase.
 */
@Singleton
class GoalSprintCrossRefApiService @Inject constructor(
    private val supabaseManager: SupabaseManager
) {
    private val tableName = "goal_sprint_cross_refs"
    
    /**
     * Get all goal-sprint cross-references for a goal.
     */
    suspend fun getByGoalId(goalId: String): Result<List<GoalSprintCrossRefDto>> = withContext(Dispatchers.IO) {
        return@withContext try {
            val client = supabaseManager.getClient()
            val crossRefs = client.postgrest[tableName]
                .select() {
                    filter {
                        eq("goal_id", goalId)
                    }
                }
                .decodeList<GoalSprintCrossRefDto>()
            
            Result.Success(crossRefs)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting goal-sprint cross-refs by goal ID: ${e.message}", e)
            Result.Error("Failed to get goal-sprint cross-refs: ${e.message}", e)
        }
    }
    
    /**
     * Get all goal-sprint cross-references for a sprint.
     */
    suspend fun getBySprintId(sprintId: String): Result<List<GoalSprintCrossRefDto>> = withContext(Dispatchers.IO) {
        return@withContext try {
            val client = supabaseManager.getClient()
            val crossRefs = client.postgrest[tableName]
                .select() {
                    filter {
                        eq("sprint_id", sprintId)
                    }
                }
                .decodeList<GoalSprintCrossRefDto>()
            
            Result.Success(crossRefs)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting goal-sprint cross-refs by sprint ID: ${e.message}", e)
            Result.Error("Failed to get goal-sprint cross-refs: ${e.message}", e)
        }
    }
    
    /**
     * Create a goal-sprint cross-reference.
     */
    suspend fun insert(crossRef: GoalSprintCrossRefDto): Result<GoalSprintCrossRefDto> = withContext(Dispatchers.IO) {
        return@withContext try {
            val client = supabaseManager.getClient()
            client.postgrest[tableName]
                .insert(crossRef)
            
            Result.Success(crossRef)
        } catch (e: Exception) {
            Log.e(TAG, "Error inserting goal-sprint cross-ref: ${e.message}", e)
            Result.Error("Failed to insert goal-sprint cross-ref: ${e.message}", e)
        }
    }
    
    /**
     * Delete a goal-sprint cross-reference by goal ID and sprint ID.
     */
    suspend fun delete(goalId: String, sprintId: String): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            val client = supabaseManager.getClient()
            client.postgrest[tableName]
                .delete {
                    filter {
                        eq("goal_id", goalId)
                        eq("sprint_id", sprintId)
                    }
                }
            
            Result.Success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting goal-sprint cross-ref: ${e.message}", e)
            Result.Error("Failed to delete goal-sprint cross-ref: ${e.message}", e)
        }
    }
    
    /**
     * Delete all goal-sprint cross-references for a goal.
     */
    suspend fun deleteByGoalId(goalId: String): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            val client = supabaseManager.getClient()
            client.postgrest[tableName]
                .delete {
                    filter {
                        eq("goal_id", goalId)
                    }
                }
            
            Result.Success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting goal-sprint cross-refs by goal ID: ${e.message}", e)
            Result.Error("Failed to delete goal-sprint cross-refs: ${e.message}", e)
        }
    }
    
    /**
     * Delete all goal-sprint cross-references for a sprint.
     */
    suspend fun deleteBySprintId(sprintId: String): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            val client = supabaseManager.getClient()
            client.postgrest[tableName]
                .delete {
                    filter {
                        eq("sprint_id", sprintId)
                    }
                }
            
            Result.Success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting goal-sprint cross-refs by sprint ID: ${e.message}", e)
            Result.Error("Failed to delete goal-sprint cross-refs: ${e.message}", e)
        }
    }
    
    companion object {
        private const val TAG = "GoalSprintCrossRefApiService"
    }
    
    /**
     * Create a goal-sprint relation.
     * This method is an alias for insert() to match repository method names.
     */
    suspend fun createGoalSprintRelation(crossRefDto: GoalSprintCrossRefDto): Result<GoalSprintCrossRefDto> {
        return insert(crossRefDto)
    }
    
    /**
     * Delete a goal-sprint relation by ID.
     */
    suspend fun deleteGoalSprintRelation(id: String): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            val client = supabaseManager.getClient()
            client.postgrest[tableName]
                .delete {
                    filter {
                        eq("id", id)
                    }
                }
            
            Result.Success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting goal-sprint relation by ID: ${e.message}", e)
            Result.Error("Failed to delete goal-sprint relation: ${e.message}", e)
        }
    }
}
