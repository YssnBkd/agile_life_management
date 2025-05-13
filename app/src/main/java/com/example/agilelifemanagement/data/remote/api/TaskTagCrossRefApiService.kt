package com.example.agilelifemanagement.data.remote.api

import android.util.Log
import com.example.agilelifemanagement.data.remote.SupabaseManager
import com.example.agilelifemanagement.data.remote.dto.TaskTagCrossRefDto
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
 * API service for Task-Tag cross-references operations with Supabase.
 */
@Singleton
class TaskTagCrossRefApiService @Inject constructor(
    private val supabaseManager: SupabaseManager
) {
    private val tableName = "task_tag_cross_refs"
    
    /**
     * Get all task-tag cross-references for a task.
     */
    suspend fun getByTaskId(taskId: String): Result<List<TaskTagCrossRefDto>> = withContext(Dispatchers.IO) {
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
            Log.e(TAG, "Error getting task-tag cross-refs by task ID: ${e.message}", e)
            Result.Error("Failed to get task-tag cross-refs: ${e.message}", e)
        }
    }
    
    /**
     * Get all task-tag cross-references for a tag.
     */
    suspend fun getByTagId(tagId: String): Result<List<TaskTagCrossRefDto>> = withContext(Dispatchers.IO) {
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
            Log.e(TAG, "Error getting task-tag cross-refs by tag ID: ${e.message}", e)
            Result.Error("Failed to get task-tag cross-refs: ${e.message}", e)
        }
    }
    
    /**
     * Create a task-tag cross-reference.
     */
    suspend fun insert(crossRef: TaskTagCrossRefDto): Result<TaskTagCrossRefDto> = withContext(Dispatchers.IO) {
        return@withContext try {
            val client = supabaseManager.getClient()
            client.postgrest[tableName]
                .insert(crossRef)
            
            Result.Success(crossRef)
        } catch (e: Exception) {
            Log.e(TAG, "Error inserting task-tag cross-ref: ${e.message}", e)
            Result.Error("Failed to insert task-tag cross-ref: ${e.message}", e)
        }
    }
    
    /**
     * Delete a task-tag cross-reference by task ID and tag ID.
     */
    suspend fun delete(taskId: String, tagId: String): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            val client = supabaseManager.getClient()
            client.postgrest[tableName]
                .delete {
                    filter {
                        eq("task_id", taskId)
                        eq("tag_id", tagId)
                    }
                }
            
            Result.Success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting task-tag cross-ref: ${e.message}", e)
            Result.Error("Failed to delete task-tag cross-ref: ${e.message}", e)
        }
    }
    
    /**
     * Delete all task-tag cross-references for a task.
     */
    suspend fun deleteByTaskId(taskId: String): Result<Unit> = withContext(Dispatchers.IO) {
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
            Log.e(TAG, "Error deleting task-tag cross-refs by task ID: ${e.message}", e)
            Result.Error("Failed to delete task-tag cross-refs: ${e.message}", e)
        }
    }
    
    /**
     * Delete all task-tag cross-references for a tag.
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
            Log.e(TAG, "Error deleting task-tag cross-refs by tag ID: ${e.message}", e)
            Result.Error("Failed to delete task-tag cross-refs: ${e.message}", e)
        }
    }
    
    companion object {
        private const val TAG = "TaskTagCrossRefApiService"
    }
}
