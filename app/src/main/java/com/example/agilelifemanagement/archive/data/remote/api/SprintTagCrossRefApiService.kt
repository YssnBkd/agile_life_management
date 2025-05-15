package com.example.agilelifemanagement.data.remote.api

import android.util.Log
import com.example.agilelifemanagement.data.remote.SupabaseManager
import com.example.agilelifemanagement.data.remote.dto.SprintTagCrossRefDto
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
 * API service for Sprint-Tag cross-references operations with Supabase.
 */
@Singleton
class SprintTagCrossRefApiService @Inject constructor(
    private val supabaseManager: SupabaseManager
) {
    private val tableName = "sprint_tag_cross_refs"
    
    /**
     * Get all sprint-tag cross-references for a sprint.
     */
    suspend fun getBySprintId(sprintId: String): Result<List<SprintTagCrossRefDto>> = withContext(Dispatchers.IO) {
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
            Log.e(TAG, "Error getting sprint-tag cross-refs by sprint ID: ${e.message}", e)
            Result.Error("Failed to get sprint-tag cross-refs: ${e.message}", e)
        }
    }
    
    /**
     * Get all sprint-tag cross-references for a tag.
     */
    suspend fun getByTagId(tagId: String): Result<List<SprintTagCrossRefDto>> = withContext(Dispatchers.IO) {
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
            Log.e(TAG, "Error getting sprint-tag cross-refs by tag ID: ${e.message}", e)
            Result.Error("Failed to get sprint-tag cross-refs: ${e.message}", e)
        }
    }
    
    /**
     * Create a sprint-tag cross-reference.
     */
    suspend fun insert(crossRef: SprintTagCrossRefDto): Result<SprintTagCrossRefDto> = withContext(Dispatchers.IO) {
        return@withContext try {
            val client = supabaseManager.getClient()
            client.postgrest[tableName]
                .insert(crossRef)
            
            Result.Success(crossRef)
        } catch (e: Exception) {
            Log.e(TAG, "Error inserting sprint-tag cross-ref: ${e.message}", e)
            Result.Error("Failed to insert sprint-tag cross-ref: ${e.message}", e)
        }
    }
    
    /**
     * Delete a sprint-tag cross-reference by sprint ID and tag ID.
     */
    suspend fun delete(sprintId: String, tagId: String): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            val client = supabaseManager.getClient()
            client.postgrest[tableName]
                .delete {
                    filter {
                        eq("sprint_id", sprintId)
                        eq("tag_id", tagId)
                    }
                }
            
            Result.Success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting sprint-tag cross-ref: ${e.message}", e)
            Result.Error("Failed to delete sprint-tag cross-ref: ${e.message}", e)
        }
    }
    
    /**
     * Delete all sprint-tag cross-references for a sprint.
     */
    suspend fun deleteBySprintId(sprintId: String): Result<Unit> = withContext(Dispatchers.IO) {
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
            Log.e(TAG, "Error deleting sprint-tag cross-refs by sprint ID: ${e.message}", e)
            Result.Error("Failed to delete sprint-tag cross-refs: ${e.message}", e)
        }
    }
    
    /**
     * Delete all sprint-tag cross-references for a tag.
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
            Log.e(TAG, "Error deleting sprint-tag cross-refs by tag ID: ${e.message}", e)
            Result.Error("Failed to delete sprint-tag cross-refs: ${e.message}", e)
        }
    }
    
    companion object {
        private const val TAG = "SprintTagCrossRefApiService"
    }
}
