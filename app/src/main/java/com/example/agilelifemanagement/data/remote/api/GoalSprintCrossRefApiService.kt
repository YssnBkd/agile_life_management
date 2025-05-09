package com.example.agilelifemanagement.data.remote.api

import android.util.Log
import com.example.agilelifemanagement.data.remote.SupabaseManager
import com.example.agilelifemanagement.data.remote.dto.GoalSprintCrossRefDto
import com.example.agilelifemanagement.domain.model.Result
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * API service for GoalSprintCrossRef operations with Supabase.
 * Handles the many-to-many relationship between goals and sprints.
 */
@Singleton
class GoalSprintCrossRefApiService @Inject constructor(
    private val supabaseManager: SupabaseManager
) {
    private val tableName = "agile_life.goal_sprint_cross_refs"
    
    /**
     * Get all goal-sprint relationships for a specific goal.
     */
    suspend fun getSprintsByGoalId(goalId: String): Result<List<GoalSprintCrossRefDto>> = withContext(Dispatchers.IO) {
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
            Log.e(TAG, "Error getting sprints for goal: ${e.message}", e)
            Result.Error("Failed to get sprints for goal: ${e.message}", e)
        }
    }
    
    /**
     * Get all goal-sprint relationships for a specific sprint.
     */
    suspend fun getGoalsBySprintId(sprintId: String): Result<List<GoalSprintCrossRefDto>> = withContext(Dispatchers.IO) {
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
            Log.e(TAG, "Error getting goals for sprint: ${e.message}", e)
            Result.Error("Failed to get goals for sprint: ${e.message}", e)
        }
    }
    
    /**
     * Create a new goal-sprint relationship.
     */
    suspend fun createGoalSprintRelation(crossRefDto: GoalSprintCrossRefDto): Result<GoalSprintCrossRefDto> = withContext(Dispatchers.IO) {
        return@withContext try {
            val client = supabaseManager.getClient()
            client.postgrest[tableName]
                .insert(crossRefDto)
            
            Result.Success(crossRefDto)
        } catch (e: Exception) {
            Log.e(TAG, "Error creating goal-sprint relation: ${e.message}", e)
            Result.Error("Failed to create goal-sprint relation: ${e.message}", e)
        }
    }
    
    /**
     * Delete a goal-sprint relationship by ID.
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
            Log.e(TAG, "Error deleting goal-sprint relation: ${e.message}", e)
            Result.Error("Failed to delete goal-sprint relation: ${e.message}", e)
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
     * Delete all relationships for a specific sprint.
     */
    suspend fun deleteAllRelationsForSprint(sprintId: String): Result<Unit> = withContext(Dispatchers.IO) {
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
            Log.e(TAG, "Error deleting relations for sprint: ${e.message}", e)
            Result.Error("Failed to delete relations for sprint: ${e.message}", e)
        }
    }
    
    companion object {
        private const val TAG = "GoalSprintCrossRefApiService"
    }
}
