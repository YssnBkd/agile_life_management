package com.example.agilelifemanagement.data.remote.api

import android.util.Log
import com.example.agilelifemanagement.data.remote.SupabaseManager
import com.example.agilelifemanagement.data.remote.dto.TaskDependencyDto
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
 * API service for Task Dependency operations with Supabase.
 */
@Singleton
class TaskDependencyApiService @Inject constructor(
    private val supabaseManager: SupabaseManager
) {
    private val tableName = "task_dependencies"
    
    /**
     * Get all dependencies for a task (tasks that this task depends on).
     */
    suspend fun getDependenciesForTask(taskId: String): Result<List<TaskDependencyDto>> = withContext(Dispatchers.IO) {
        return@withContext try {
            val client = supabaseManager.getClient()
            val dependencies = client.postgrest[tableName]
                .select() {
                    filter {
                        eq("task_id", taskId)
                    }
                }
                .decodeList<TaskDependencyDto>()
            
            Result.Success(dependencies)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting dependencies for task: ${e.message}", e)
            Result.Error("Failed to get dependencies: ${e.message}", e)
        }
    }
    
    /**
     * Get all dependent tasks (tasks that depend on this task).
     */
    suspend fun getDependentTasks(taskId: String): Result<List<TaskDependencyDto>> = withContext(Dispatchers.IO) {
        return@withContext try {
            val client = supabaseManager.getClient()
            val dependents = client.postgrest[tableName]
                .select() {
                    filter {
                        eq("depends_on_task_id", taskId)
                    }
                }
                .decodeList<TaskDependencyDto>()
            
            Result.Success(dependents)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting dependent tasks: ${e.message}", e)
            Result.Error("Failed to get dependent tasks: ${e.message}", e)
        }
    }
    
    /**
     * Create a task dependency relationship.
     */
    suspend fun createDependency(dependency: TaskDependencyDto): Result<TaskDependencyDto> = withContext(Dispatchers.IO) {
        return@withContext try {
            val client = supabaseManager.getClient()
            client.postgrest[tableName]
                .insert(dependency)
            
            Result.Success(dependency)
        } catch (e: Exception) {
            Log.e(TAG, "Error creating task dependency: ${e.message}", e)
            Result.Error("Failed to create task dependency: ${e.message}", e)
        }
    }
    
    /**
     * Delete a task dependency relationship.
     */
    suspend fun deleteDependency(taskId: String, dependsOnTaskId: String): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            val client = supabaseManager.getClient()
            client.postgrest[tableName]
                .delete {
                    filter {
                        eq("task_id", taskId)
                        eq("depends_on_task_id", dependsOnTaskId)
                    }
                }
            
            Result.Success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting task dependency: ${e.message}", e)
            Result.Error("Failed to delete task dependency: ${e.message}", e)
        }
    }
    
    /**
     * Delete all dependencies for a task.
     */
    suspend fun deleteAllDependenciesForTask(taskId: String): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            val client = supabaseManager.getClient()
            
            // Delete dependencies where this task depends on others
            client.postgrest[tableName]
                .delete {
                    filter {
                        eq("task_id", taskId)
                    }
                }
            
            // Delete dependencies where other tasks depend on this one
            client.postgrest[tableName]
                .delete {
                    filter {
                        eq("depends_on_task_id", taskId)
                    }
                }
            
            Result.Success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting all task dependencies: ${e.message}", e)
            Result.Error("Failed to delete all task dependencies: ${e.message}", e)
        }
    }
    
    companion object {
        private const val TAG = "TaskDependencyApiService"
    }
}
