package com.example.agilelifemanagement.data.remote.api

import android.util.Log
import com.example.agilelifemanagement.data.remote.SupabaseManager
import com.example.agilelifemanagement.data.remote.dto.TaskSprintCrossRefDto
import com.example.agilelifemanagement.domain.model.Result
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * API service for TaskSprintCrossRef operations with Supabase.
 * Handles the many-to-many relationship between tasks and sprints.
 */
@Singleton
class TaskSprintCrossRefApiService @Inject constructor(
    private val supabaseManager: SupabaseManager
) {
    private val tableName = "agile_life.task_sprint_cross_refs"
    
    /**
     * Get all task-sprint relationships for a specific task.
     */
    suspend fun getSprintsByTaskId(taskId: String): Result<List<TaskSprintCrossRefDto>> = withContext(Dispatchers.IO) {
        return@withContext try {
            val client = supabaseManager.getClient()
            val crossRefs = client.postgrest[tableName]
                .select() {
                    filter {
                        eq("task_id", taskId)
                    }
                }
                .decodeList<TaskSprintCrossRefDto>()
            
            Result.Success(crossRefs)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting sprints for task: ${e.message}", e)
            Result.Error("Failed to get sprints for task: ${e.message}", e)
        }
    }
    
    /**
     * Get all task-sprint relationships for a specific sprint.
     */
    suspend fun getTasksBySprintId(sprintId: String): Result<List<TaskSprintCrossRefDto>> = withContext(Dispatchers.IO) {
        return@withContext try {
            val client = supabaseManager.getClient()
            val crossRefs = client.postgrest[tableName]
                .select() {
                    filter {
                        eq("sprint_id", sprintId)
                    }
                }
                .decodeList<TaskSprintCrossRefDto>()
            
            Result.Success(crossRefs)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting tasks for sprint: ${e.message}", e)
            Result.Error("Failed to get tasks for sprint: ${e.message}", e)
        }
    }
    
    /**
     * Create a new task-sprint relationship.
     */
    suspend fun createTaskSprintRelation(crossRefDto: TaskSprintCrossRefDto): Result<TaskSprintCrossRefDto> = withContext(Dispatchers.IO) {
        return@withContext try {
            val client = supabaseManager.getClient()
            client.postgrest[tableName]
                .insert(crossRefDto)
            
            Result.Success(crossRefDto)
        } catch (e: Exception) {
            Log.e(TAG, "Error creating task-sprint relation: ${e.message}", e)
            Result.Error("Failed to create task-sprint relation: ${e.message}", e)
        }
    }
    
    /**
     * Delete a task-sprint relationship by ID.
     */
    suspend fun deleteTaskSprintRelation(id: String): Result<Unit> = withContext(Dispatchers.IO) {
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
            Log.e(TAG, "Error deleting task-sprint relation: ${e.message}", e)
            Result.Error("Failed to delete task-sprint relation: ${e.message}", e)
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
        private const val TAG = "TaskSprintCrossRefApiService"
    }
}
