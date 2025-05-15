package com.example.agilelifemanagement.data.remote.api

import android.util.Log
import com.example.agilelifemanagement.data.remote.SupabaseManager
import com.example.agilelifemanagement.data.remote.dto.TagDto
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
 * API service for Tag operations with Supabase.
 */
@Singleton
class TagApiService @Inject constructor(
    private val supabaseManager: SupabaseManager
) {
    private val tableName = "tags"
    
    /**
     * Get a tag by ID from Supabase.
     */
    suspend fun getTagById(tagId: String): Result<TagDto> = withContext(Dispatchers.IO) {
        return@withContext try {
            val client = supabaseManager.getClient()
            val tag = client.postgrest[tableName]
                .select() {
                    filter {
                        eq("id", tagId)
                    }
                }
                .decodeSingle<TagDto>()
            
            Result.Success(tag)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting tag by ID: ${e.message}", e)
            Result.Error("Failed to get tag: ${e.message}", e)
        }
    }
    
    /**
     * Get all tags for a user from Supabase.
     */
    suspend fun getTagsByUserId(userId: String): Result<List<TagDto>> = withContext(Dispatchers.IO) {
        return@withContext try {
            val client = supabaseManager.getClient()
            val tags = client.postgrest[tableName]
                .select() {
                    filter {
                        eq("user_id", userId)
                    }
                    order("name", Order.ASCENDING)
                }
                .decodeList<TagDto>()
            
            Result.Success(tags)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting tags for user: ${e.message}", e)
            Result.Error("Failed to get tags: ${e.message}", e)
        }
    }
    
    /**
     * Create a new tag in Supabase.
     */
    suspend fun createTag(tagDto: TagDto): Result<TagDto> = withContext(Dispatchers.IO) {
        return@withContext try {
            val client = supabaseManager.getClient()
            client.postgrest[tableName]
                .insert(tagDto)
            
            Result.Success(tagDto)
        } catch (e: Exception) {
            Log.e(TAG, "Error creating tag: ${e.message}", e)
            Result.Error("Failed to create tag: ${e.message}", e)
        }
    }
    
    /**
     * Update a tag in Supabase.
     */
    suspend fun updateTag(tagDto: TagDto): Result<TagDto> = withContext(Dispatchers.IO) {
        return@withContext try {
            val client = supabaseManager.getClient()
            client.postgrest[tableName]
                .update({
                    set("name", tagDto.name)
                    set("color", tagDto.color)
                }) {
                    filter {
                        eq("id", tagDto.id)
                    }
                }
            
            Result.Success(tagDto)
        } catch (e: Exception) {
            Log.e(TAG, "Error updating tag: ${e.message}", e)
            Result.Error("Failed to update tag: ${e.message}", e)
        }
    }
    
    /**
     * Delete a tag from Supabase.
     */
    suspend fun deleteTag(tagId: String): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            val client = supabaseManager.getClient()
            client.postgrest[tableName]
                .delete {
                    filter {
                        eq("id", tagId)
                    }
                }
            
            Result.Success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting tag: ${e.message}", e)
            Result.Error("Failed to delete tag: ${e.message}", e)
        }
    }
    
    companion object {
        private const val TAG = "TagApiService"
    }
}
