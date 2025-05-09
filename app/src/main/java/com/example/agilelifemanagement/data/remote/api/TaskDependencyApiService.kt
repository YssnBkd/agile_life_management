package com.example.agilelifemanagement.data.remote.api

import android.util.Log
import com.example.agilelifemanagement.data.remote.SupabaseManager
import com.example.agilelifemanagement.data.remote.dto.TaskDependencyDto
import com.example.agilelifemanagement.domain.model.Result
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * API service for TaskDependency operations with Supabase.
 * Handles the dependency relationships between tasks.
 */
@Singleton
class TaskDependencyApiService @Inject constructor(
    private val supabaseManager: SupabaseManager
) {
    private val tableName = "agile_life.task_dependencies"
    
    /**
     * Get all dependencies for a specific task.
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
            Result.Error("Failed to get dependencies for task: ${e.message}", e)
        }
    }
    
    /**
     * Get all tasks that depend on a specific task.
     */
    suspend fun getDependentTasks(dependsOnTaskId: String): Result<List<TaskDependencyDto>> = withContext(Dispatchers.IO) {
        return@withContext try {
            val client = supabaseManager.getClient()
            val dependencies = client.postgrest[tableName]
                .select() {
                    filter {
                        eq("depends_on_task_id", dependsOnTaskId)
                    }
                }
                .decodeList<TaskDependencyDto>()
            
            Result.Success(dependencies)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting dependent tasks: ${e.message}", e)
            Result.Error("Failed to get dependent tasks: ${e.message}", e)
        }
    }
    
    /**
     * Create a new task dependency.
     */
    suspend fun createTaskDependency(dependencyDto: TaskDependencyDto): Result<TaskDependencyDto> = withContext(Dispatchers.IO) {
        return@withContext try {
            val client = supabaseManager.getClient()
            client.postgrest[tableName]
                .insert(dependencyDto)
            
            Result.Success(dependencyDto)
        } catch (e: Exception) {
            Log.e(TAG, "Error creating task dependency: ${e.message}", e)
            Result.Error("Failed to create task dependency: ${e.message}", e)
        }
    }
    
    /**
     * Delete a task dependency by ID.
     */
    suspend fun deleteTaskDependency(id: String): Result<Unit> = withContext(Dispatchers.IO) {
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
            Log.e(TAG, "Error deleting task dependency: ${e.message}", e)
            Result.Error("Failed to delete task dependency: ${e.message}", e)
        }
    }
    
    /**
     * Delete all dependencies for a specific task.
     */
    suspend fun deleteAllDependenciesForTask(taskId: String): Result<Unit> = withContext(Dispatchers.IO) {
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
            Log.e(TAG, "Error deleting dependencies for task: ${e.message}", e)
            Result.Error("Failed to delete dependencies for task: ${e.message}", e)
        }
    }
    
    /**
     * Delete all dependencies that depend on a specific task.
     */
    suspend fun deleteAllDependentTasks(dependsOnTaskId: String): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            val client = supabaseManager.getClient()
            client.postgrest[tableName]
                .delete {
                    filter {
                        eq("depends_on_task_id", dependsOnTaskId)
                    }
                }
            
            Result.Success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting dependent tasks: ${e.message}", e)
            Result.Error("Failed to delete dependent tasks: ${e.message}", e)
        }
    }
    
    companion object {
        private const val TAG = "TaskDependencyApiService"
    }
}
