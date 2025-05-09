package com.example.agilelifemanagement.data.remote.api

import android.util.Log
import com.example.agilelifemanagement.data.remote.SupabaseManager
import com.example.agilelifemanagement.data.remote.dto.TagDto
import com.example.agilelifemanagement.domain.model.Result
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * API service for Tag operations with Supabase.
 * Implements security best practices according to the app's security implementation guidelines.
 */
@Singleton
class TagApiService @Inject constructor(
    private val supabaseManager: SupabaseManager
) {
    private val tableName = "agile_life.tags"
    
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
     * Create or update a tag in Supabase.
     */
    suspend fun upsertTag(tagDto: TagDto): Result<TagDto> = withContext(Dispatchers.IO) {
        return@withContext try {
            val client = supabaseManager.getClient()
            
            // Check if tag exists
            val exists = client.postgrest[tableName]
                .select() {
                    filter {
                        eq("id", tagDto.id)
                    }
                }
                .decodeList<TagDto>()
                .isNotEmpty()
            if (exists) {
                // Update existing tag
                client.postgrest[tableName]
                    .update({
                        set("name", tagDto.name)
                        set("color", tagDto.color)
                    }) {
                        filter {
                            eq("id", tagDto.id)
                        }
                    }
            } else {
                // Insert new tag
                client.postgrest[tableName]
                    .insert(tagDto)
            }
            
            Result.Success(tagDto)
        } catch (e: Exception) {
            Log.e(TAG, "Error upserting tag: ${e.message}", e)
            Result.Error("Failed to save tag: ${e.message}", e)
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
