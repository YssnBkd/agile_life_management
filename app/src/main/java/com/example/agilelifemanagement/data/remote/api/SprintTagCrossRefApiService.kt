package com.example.agilelifemanagement.data.remote.api

import android.util.Log
import com.example.agilelifemanagement.data.remote.SupabaseManager
import com.example.agilelifemanagement.data.remote.dto.SprintTagCrossRefDto
import com.example.agilelifemanagement.domain.model.Result
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * API service for SprintTagCrossRef operations with Supabase.
 * Handles the many-to-many relationship between sprints and tags.
 */
@Singleton
class SprintTagCrossRefApiService @Inject constructor(
    private val supabaseManager: SupabaseManager
) {
    private val tableName = "agile_life.sprint_tag_cross_refs"
    
    /**
     * Get all sprint-tag relationships for a specific sprint.
     */
    suspend fun getTagsBySprintId(sprintId: String): Result<List<SprintTagCrossRefDto>> = withContext(Dispatchers.IO) {
        return@withContext try {
            val client = supabaseManager.getClient()
            val crossRefs = client.postgrest[tableName]
                .select() {
                    filter {
                        eq("sprint_id", sprintId)
                    }
                }
                .decodeList<SprintTagCrossRefDto>()
            
            Result.Success(crossRefs)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting tags for sprint: ${e.message}", e)
            Result.Error("Failed to get tags for sprint: ${e.message}", e)
        }
    }
    
    /**
     * Get all sprint-tag relationships for a specific tag.
     */
    suspend fun getSprintsByTagId(tagId: String): Result<List<SprintTagCrossRefDto>> = withContext(Dispatchers.IO) {
        return@withContext try {
            val client = supabaseManager.getClient()
            val crossRefs = client.postgrest[tableName]
                .select() {
                    filter {
                        eq("tag_id", tagId)
                    }
                }
                .decodeList<SprintTagCrossRefDto>()
            
            Result.Success(crossRefs)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting sprints for tag: ${e.message}", e)
            Result.Error("Failed to get sprints for tag: ${e.message}", e)
        }
    }
    
    /**
     * Create a new sprint-tag relationship.
     */
    suspend fun createSprintTagRelation(crossRefDto: SprintTagCrossRefDto): Result<SprintTagCrossRefDto> = withContext(Dispatchers.IO) {
        return@withContext try {
            val client = supabaseManager.getClient()
            val result = client.postgrest[tableName]
                .insert(crossRefDto)
                .decodeSingle<SprintTagCrossRefDto>()
            
            Result.Success(result)
        } catch (e: Exception) {
            Log.e(TAG, "Error creating sprint-tag relation: ${e.message}", e)
            Result.Error("Failed to create sprint-tag relation: ${e.message}", e)
        }
    }
    
    /**
     * Delete a sprint-tag relationship by ID.
     */
    suspend fun deleteSprintTagRelation(id: String): Result<Unit> = withContext(Dispatchers.IO) {
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
            Log.e(TAG, "Error deleting sprint-tag relation: ${e.message}", e)
            Result.Error("Failed to delete sprint-tag relation: ${e.message}", e)
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
        private const val TAG = "SprintTagCrossRefApiService"
    }
}
