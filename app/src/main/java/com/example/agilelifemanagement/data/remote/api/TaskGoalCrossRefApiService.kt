package com.example.agilelifemanagement.data.remote.api

import android.util.Log
import com.example.agilelifemanagement.data.remote.SupabaseManager
import com.example.agilelifemanagement.data.remote.dto.TaskGoalCrossRefDto
import com.example.agilelifemanagement.domain.model.Result
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * API service for TaskGoalCrossRef operations with Supabase.
 * Handles the many-to-many relationship between tasks and goals.
 */
@Singleton
class TaskGoalCrossRefApiService @Inject constructor(
    private val supabaseManager: SupabaseManager
) {
    private val tableName = "agile_life.task_goal_cross_refs"
    
    /**
     * Get all task-goal relationships for a specific task.
     */
    suspend fun getGoalsByTaskId(taskId: String): Result<List<TaskGoalCrossRefDto>> = withContext(Dispatchers.IO) {
        return@withContext try {
            val client = supabaseManager.getClient()
            val crossRefs = client.postgrest[tableName]
                .select() {
                    filter {
                        eq("task_id", taskId)
                    }
                }
                .decodeList<TaskGoalCrossRefDto>()
            
            Result.Success(crossRefs)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting goals for task: ${e.message}", e)
            Result.Error("Failed to get goals for task: ${e.message}", e)
        }
    }
    
    /**
     * Get all task-goal relationships for a specific goal.
     */
    suspend fun getTasksByGoalId(goalId: String): Result<List<TaskGoalCrossRefDto>> = withContext(Dispatchers.IO) {
        return@withContext try {
            val client = supabaseManager.getClient()
            val crossRefs = client.postgrest[tableName]
                .select() {
                    filter {
                        eq("goal_id", goalId)
                    }
                }
                .decodeList<TaskGoalCrossRefDto>()
            
            Result.Success(crossRefs)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting tasks for goal: ${e.message}", e)
            Result.Error("Failed to get tasks for goal: ${e.message}", e)
        }
    }
    
    /**
     * Create a new task-goal relationship.
     */
    suspend fun createTaskGoalRelation(crossRefDto: TaskGoalCrossRefDto): Result<TaskGoalCrossRefDto> = withContext(Dispatchers.IO) {
        return@withContext try {
            val client = supabaseManager.getClient()
            client.postgrest[tableName]
                .insert(crossRefDto)
            
            Result.Success(crossRefDto)
        } catch (e: Exception) {
            Log.e(TAG, "Error creating task-goal relation: ${e.message}", e)
            Result.Error("Failed to create task-goal relation: ${e.message}", e)
        }
    }
    
    /**
     * Delete a task-goal relationship by ID.
     */
    suspend fun deleteTaskGoalRelation(id: String): Result<Unit> = withContext(Dispatchers.IO) {
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
            Log.e(TAG, "Error deleting task-goal relation: ${e.message}", e)
            Result.Error("Failed to delete task-goal relation: ${e.message}", e)
        }
    }
    
    /**
     * Delete all relationships for a specific task.
     */
    suspend fun deleteAllRelationsForTask(taskId: String): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            val client = supabaseManager.getClient()
            client.postgrest[tableName]
                .delete {
                    filter {
                        eq("task_id", taskId)
                    }
                }
            
            Result.Success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting relations for task: ${e.message}", e)
            Result.Error("Failed to delete relations for task: ${e.message}", e)
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
    
    companion object {
        private const val TAG = "TaskGoalCrossRefApiService"
    }
}
