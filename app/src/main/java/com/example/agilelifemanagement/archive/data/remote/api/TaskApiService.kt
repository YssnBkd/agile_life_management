package com.example.agilelifemanagement.data.remote.api

import android.util.Log
import com.example.agilelifemanagement.data.remote.SupabaseManager
import com.example.agilelifemanagement.data.remote.dto.TaskDto
import com.example.agilelifemanagement.domain.model.Result
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * API service for Task operations with Supabase.
 * Implements security best practices according to the app's security implementation guidelines.
 */
@Singleton
class TaskApiService @Inject constructor(
    private val supabaseManager: SupabaseManager
) {
    private val tableName = "tasks"
    
    /**
     * Get a task by ID from Supabase.
     */
    suspend fun getTaskById(taskId: String): Result<TaskDto> = withContext(Dispatchers.IO) {
        return@withContext try {
            val client = supabaseManager.getClient()
            val task = client.postgrest[tableName]
                .select() {
                    filter {
                        eq("id", taskId)
                    }
                }
                .decodeSingle<TaskDto>()
            
            Result.Success(task)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting task by ID: ${e.message}", e)
            Result.Error("Failed to get task: ${e.message}", e)
        }
    }
    
    /**
     * Get all tasks for a user from Supabase.
     */
    suspend fun getTasksByUserId(userId: String): Result<List<TaskDto>> = withContext(Dispatchers.IO) {
        return@withContext try {
            val client = supabaseManager.getClient()
            val tasks = client.postgrest[tableName]
                .select() {
                    filter {
                        eq("user_id", userId)
                    }
                    order("due_date", Order.ASCENDING)
                }
                .decodeList<TaskDto>()
            
            Result.Success(tasks)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting tasks for user: ${e.message}", e)
            Result.Error("Failed to get tasks: ${e.message}", e)
        }
    }
    
    /**
     * Get tasks by priority for a user from Supabase.
     */
    suspend fun getTasksByPriority(userId: String, priority: Int): Result<List<TaskDto>> = withContext(Dispatchers.IO) {
        return@withContext try {
            val client = supabaseManager.getClient()
            val tasks = client.postgrest[tableName]
                .select() {
                    filter {
                        eq("user_id", userId)
                        eq("priority", priority)
                    }
                    order("due_date", Order.ASCENDING)
                }
                .decodeList<TaskDto>()
            
            Result.Success(tasks)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting tasks by priority: ${e.message}", e)
            Result.Error("Failed to get tasks by priority: ${e.message}", e)
        }
    }
    
    /**
     * Get tasks by status for a user from Supabase.
     */
    suspend fun getTasksByStatus(userId: String, status: Int): Result<List<TaskDto>> = withContext(Dispatchers.IO) {
        return@withContext try {
            val client = supabaseManager.getClient()
            val tasks = client.postgrest[tableName]
                .select() {
                    filter {
                        eq("user_id", userId)
                        eq("status", status)
                    }
                    order("due_date", Order.ASCENDING)
                }
                .decodeList<TaskDto>()
            
            Result.Success(tasks)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting tasks by status: ${e.message}", e)
            Result.Error("Failed to get tasks by status: ${e.message}", e)
        }
    }
    
    /**
     * Get tasks due by a specific date for a user from Supabase.
     */
    suspend fun getTasksDueByDate(userId: String, dueDate: Long): Result<List<TaskDto>> = withContext(Dispatchers.IO) {
        return@withContext try {
            val client = supabaseManager.getClient()
            val tasks = client.postgrest[tableName]
                .select() {
                    filter {
                        eq("user_id", userId)
                        lte("due_date", dueDate)
                        eq("status", 0) // Not completed
                    }
                    order("priority", Order.DESCENDING)
                }
                .decodeList<TaskDto>()
            
            Result.Success(tasks)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting tasks due by date: ${e.message}", e)
            Result.Error("Failed to get tasks due by date: ${e.message}", e)
        }
    }
    
    /**
     * Create or update a task in Supabase.
     */
    suspend fun upsertTask(taskDto: TaskDto): Result<TaskDto> = withContext(Dispatchers.IO) {
        return@withContext try {
            val client = supabaseManager.getClient()
            
            val exists = client.postgrest[tableName]
                .select() {
                    filter {
                        eq("id", taskDto.id)
                    }
                }
                .decodeList<TaskDto>()
                .isNotEmpty()
            
            if (exists) {
                // Update existing task
                client.postgrest[tableName]
                    .update({
                        set("title", taskDto.title)
                        set("summary", taskDto.summary)
                        set("due_date", taskDto.due_date)
                        set("priority", taskDto.priority)
                        set("status", taskDto.status)
                        set("estimated_effort", taskDto.estimated_effort)
                        set("actual_effort", taskDto.actual_effort)
                        set("is_recurring", taskDto.is_recurring)
                        set("recurring_pattern", taskDto.recurring_pattern)
                        set("updated_at", System.currentTimeMillis())
                    }) {
                        filter {
                            eq("id", taskDto.id)
                        }
                    }
            } else {
                // Insert new task
                client.postgrest[tableName]
                    .insert(taskDto)
            }
            
            Result.Success(taskDto)
        } catch (e: Exception) {
            Log.e(TAG, "Error upserting task: ${e.message}", e)
            Result.Error("Failed to save task: ${e.message}", e)
        }
    }
    
    /**
     * Delete a task from Supabase.
     */
    suspend fun deleteTask(taskId: String): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            val client = supabaseManager.getClient()
            client.postgrest[tableName]
                .delete {
                    filter {
                        eq("id", taskId)
                    }
                }
            
            Result.Success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting task: ${e.message}", e)
            Result.Error("Failed to delete task: ${e.message}", e)
        }
    }
    
    companion object {
        private const val TAG = "TaskApiService"
    }
}
