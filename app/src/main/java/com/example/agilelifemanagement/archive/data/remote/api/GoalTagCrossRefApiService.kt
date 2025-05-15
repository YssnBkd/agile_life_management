package com.example.agilelifemanagement.data.remote.api

import android.util.Log
import com.example.agilelifemanagement.data.remote.SupabaseManager
import com.example.agilelifemanagement.data.remote.dto.GoalTagCrossRefDto
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
 * API service for Goal-Tag cross-references operations with Supabase.
 */
@Singleton
class GoalTagCrossRefApiService @Inject constructor(
    private val supabaseManager: SupabaseManager
) {
    private val tableName = "goal_tag_cross_refs"
    
    /**
     * Get all goal-tag cross-references for a goal.
     */
    suspend fun getByGoalId(goalId: String): Result<List<GoalTagCrossRefDto>> = withContext(Dispatchers.IO) {
        return@withContext try {
            val client = supabaseManager.getClient()
            val crossRefs = client.postgrest[tableName]
                .select() {
                    filter {
                        eq("goal_id", goalId)
                    }
                }
                .decodeList<GoalTagCrossRefDto>()
            
            Result.Success(crossRefs)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting goal-tag cross-refs by goal ID: ${e.message}", e)
            Result.Error("Failed to get goal-tag cross-refs: ${e.message}", e)
        }
    }
    
    /**
     * Get all goal-tag cross-references for a tag.
     */
    suspend fun getByTagId(tagId: String): Result<List<GoalTagCrossRefDto>> = withContext(Dispatchers.IO) {
        return@withContext try {
            val client = supabaseManager.getClient()
            val crossRefs = client.postgrest[tableName]
                .select() {
                    filter {
                        eq("tag_id", tagId)
                    }
                }
                .decodeList<GoalTagCrossRefDto>()
            
            Result.Success(crossRefs)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting goal-tag cross-refs by tag ID: ${e.message}", e)
            Result.Error("Failed to get goal-tag cross-refs: ${e.message}", e)
        }
    }
    
    /**
     * Create a goal-tag cross-reference.
     */
    suspend fun insert(crossRef: GoalTagCrossRefDto): Result<GoalTagCrossRefDto> = withContext(Dispatchers.IO) {
        return@withContext try {
            val client = supabaseManager.getClient()
            client.postgrest[tableName]
                .insert(crossRef)
            
            Result.Success(crossRef)
        } catch (e: Exception) {
            Log.e(TAG, "Error inserting goal-tag cross-ref: ${e.message}", e)
            Result.Error("Failed to insert goal-tag cross-ref: ${e.message}", e)
        }
    }
    
    /**
     * Delete a goal-tag cross-reference by goal ID and tag ID.
     */
    suspend fun delete(goalId: String, tagId: String): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            val client = supabaseManager.getClient()
            client.postgrest[tableName]
                .delete {
                    filter {
                        eq("goal_id", goalId)
                        eq("tag_id", tagId)
                    }
                }
            
            Result.Success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting goal-tag cross-ref: ${e.message}", e)
            Result.Error("Failed to delete goal-tag cross-ref: ${e.message}", e)
        }
    }
    
    /**
     * Delete all goal-tag cross-references for a goal.
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
            Log.e(TAG, "Error deleting goal-tag cross-refs by goal ID: ${e.message}", e)
            Result.Error("Failed to delete goal-tag cross-refs: ${e.message}", e)
        }
    }
    
    /**
     * Delete all goal-tag cross-references for a tag.
     */
    suspend fun deleteByTagId(tagId: String): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            val client = supabaseManager.getClient()
            client.postgrest[tableName]
                .delete {
                    filter {
                        eq("tag_id", tagId)
                    }
                }
            
            Result.Success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting goal-tag cross-refs by tag ID: ${e.message}", e)
            Result.Error("Failed to delete goal-tag cross-refs: ${e.message}", e)
        }
    }
    
    companion object {
        private const val TAG = "GoalTagCrossRefApiService"
    }
}
