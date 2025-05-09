package com.example.agilelifemanagement.data.remote.api

import android.util.Log
import com.example.agilelifemanagement.data.remote.SupabaseManager
import com.example.agilelifemanagement.data.remote.dto.TaskTagCrossRefDto
import com.example.agilelifemanagement.domain.model.Result
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * API service for TaskTagCrossRef operations with Supabase.
 * Handles the many-to-many relationship between tasks and tags.
 */
@Singleton
class TaskTagCrossRefApiService @Inject constructor(
    private val supabaseManager: SupabaseManager
) {
    private val tableName = "agile_life.task_tag_cross_refs"
    
    /**
     * Get all task-tag relationships for a specific task.
     */
    suspend fun getTagsByTaskId(taskId: String): Result<List<TaskTagCrossRefDto>> = withContext(Dispatchers.IO) {
        return@withContext try {
            val client = supabaseManager.getClient()
            val crossRefs = client.postgrest[tableName]
                .select() {
                    filter {
                        eq("task_id", taskId)
                    }
                }
                .decodeList<TaskTagCrossRefDto>()
            
            Result.Success(crossRefs)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting tags for task: ${e.message}", e)
            Result.Error("Failed to get tags for task: ${e.message}", e)
        }
    }
    
    /**
     * Get all task-tag relationships for a specific tag.
     */
    suspend fun getTasksByTagId(tagId: String): Result<List<TaskTagCrossRefDto>> = withContext(Dispatchers.IO) {
        return@withContext try {
            val client = supabaseManager.getClient()
            val crossRefs = client.postgrest[tableName]
                .select() {
                    filter {
                        eq("tag_id", tagId)
                    }
                }
                .decodeList<TaskTagCrossRefDto>()
            
            Result.Success(crossRefs)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting tasks for tag: ${e.message}", e)
            Result.Error("Failed to get tasks for tag: ${e.message}", e)
        }
    }
    
    /**
     * Create a new task-tag relationship.
     */
    suspend fun createTaskTagRelation(crossRefDto: TaskTagCrossRefDto): Result<TaskTagCrossRefDto> = withContext(Dispatchers.IO) {
        return@withContext try {
            val client = supabaseManager.getClient()
            client.postgrest[tableName]
                .insert(crossRefDto)
            
            Result.Success(crossRefDto)
        } catch (e: Exception) {
            Log.e(TAG, "Error creating task-tag relation: ${e.message}", e)
            Result.Error("Failed to create task-tag relation: ${e.message}", e)
        }
    }
    
    /**
     * Delete a task-tag relationship by ID.
     */
    suspend fun deleteTaskTagRelation(id: String): Result<Unit> = withContext(Dispatchers.IO) {
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
            Log.e(TAG, "Error deleting task-tag relation: ${e.message}", e)
            Result.Error("Failed to delete task-tag relation: ${e.message}", e)
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
        private const val TAG = "TaskTagCrossRefApiService"
    }
}
