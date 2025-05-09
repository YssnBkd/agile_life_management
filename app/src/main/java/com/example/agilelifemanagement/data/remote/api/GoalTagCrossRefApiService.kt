package com.example.agilelifemanagement.data.remote.api

import android.util.Log
import com.example.agilelifemanagement.data.remote.SupabaseManager
import com.example.agilelifemanagement.data.remote.dto.GoalTagCrossRefDto
import com.example.agilelifemanagement.domain.model.Result
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * API service for GoalTagCrossRef operations with Supabase.
 * Handles the many-to-many relationship between goals and tags.
 */
@Singleton
class GoalTagCrossRefApiService @Inject constructor(
    private val supabaseManager: SupabaseManager
) {
    private val tableName = "agile_life.goal_tag_cross_refs"
    
    /**
     * Get all goal-tag relationships for a specific goal.
     */
    suspend fun getTagsByGoalId(goalId: String): Result<List<GoalTagCrossRefDto>> = withContext(Dispatchers.IO) {
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
            Log.e(TAG, "Error getting tags for goal: ${e.message}", e)
            Result.Error("Failed to get tags for goal: ${e.message}", e)
        }
    }
    
    /**
     * Get all goal-tag relationships for a specific tag.
     */
    suspend fun getGoalsByTagId(tagId: String): Result<List<GoalTagCrossRefDto>> = withContext(Dispatchers.IO) {
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
            Log.e(TAG, "Error getting goals for tag: ${e.message}", e)
            Result.Error("Failed to get goals for tag: ${e.message}", e)
        }
    }
    
    /**
     * Create a new goal-tag relationship.
     */
    suspend fun createGoalTagRelation(crossRefDto: GoalTagCrossRefDto): Result<GoalTagCrossRefDto> = withContext(Dispatchers.IO) {
        return@withContext try {
            val client = supabaseManager.getClient()
            client.postgrest[tableName]
                .insert(crossRefDto)
            
            Result.Success(crossRefDto)
        } catch (e: Exception) {
            Log.e(TAG, "Error creating goal-tag relation: ${e.message}", e)
            Result.Error("Failed to create goal-tag relation: ${e.message}", e)
        }
    }
    
    /**
     * Delete a goal-tag relationship by ID.
     */
    suspend fun deleteGoalTagRelation(id: String): Result<Unit> = withContext(Dispatchers.IO) {
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
            Log.e(TAG, "Error deleting goal-tag relation: ${e.message}", e)
            Result.Error("Failed to delete goal-tag relation: ${e.message}", e)
        }
    }
    
    /**
     * Delete all relationships for a specific goal.
     */
    suspend fun deleteAllRelationsForGoal(goalId: String): Result<Unit> = withContext(Dispatchers.IO) {
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
            Log.e(TAG, "Error deleting relations for goal: ${e.message}", e)
            Result.Error("Failed to delete relations for goal: ${e.message}", e)
        }
    }
    
    /**
     * Delete all relationships for a specific tag.
     */
    suspend fun deleteAllRelationsForTag(tagId: String): Result<Unit> = withContext(Dispatchers.IO) {
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
            Log.e(TAG, "Error deleting relations for tag: ${e.message}", e)
            Result.Error("Failed to delete relations for tag: ${e.message}", e)
        }
    }
    
    companion object {
        private const val TAG = "GoalTagCrossRefApiService"
    }
}
