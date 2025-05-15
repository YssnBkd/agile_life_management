package com.example.agilelifemanagement.data.remote.api

import android.util.Log
import com.example.agilelifemanagement.data.remote.SupabaseManager
import com.example.agilelifemanagement.data.remote.dto.SprintReviewDto
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
 * API service for Sprint Review operations with Supabase.
 */
@Singleton
class SprintReviewApiService @Inject constructor(
    private val supabaseManager: SupabaseManager
) {
    private val tableName = "sprint_reviews"
    
    /**
     * Get a sprint review by ID from Supabase.
     */
    suspend fun getSprintReviewById(reviewId: String): Result<SprintReviewDto> = withContext(Dispatchers.IO) {
        return@withContext try {
            val client = supabaseManager.getClient()
            val review = client.postgrest[tableName]
                .select() {
                    filter {
                        eq("id", reviewId)
                    }
                }
                .decodeSingle<SprintReviewDto>()
            
            Result.Success(review)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting sprint review by ID: ${e.message}", e)
            Result.Error("Failed to get sprint review: ${e.message}", e)
        }
    }
    
    /**
     * Get a sprint review by sprint ID from Supabase.
     */
    suspend fun getSprintReviewBySprintId(sprintId: String): Result<SprintReviewDto?> = withContext(Dispatchers.IO) {
        return@withContext try {
            val client = supabaseManager.getClient()
            val reviews = client.postgrest[tableName]
                .select() {
                    filter {
                        eq("sprint_id", sprintId)
                    }
                }
                .decodeList<SprintReviewDto>()
            
            if (reviews.isNotEmpty()) {
                Result.Success(reviews.first())
            } else {
                Result.Success(null)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting sprint review by sprint ID: ${e.message}", e)
            Result.Error("Failed to get sprint review: ${e.message}", e)
        }
    }
    
    /**
     * Get all sprint reviews for a user from Supabase.
     */
    suspend fun getSprintReviewsByUserId(userId: String): Result<List<SprintReviewDto>> = withContext(Dispatchers.IO) {
        return@withContext try {
            val client = supabaseManager.getClient()
            val reviews = client.postgrest[tableName]
                .select() {
                    filter {
                        eq("user_id", userId)
                    }
                    order("date", Order.DESCENDING)
                }
                .decodeList<SprintReviewDto>()
            
            Result.Success(reviews)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting sprint reviews for user: ${e.message}", e)
            Result.Error("Failed to get sprint reviews: ${e.message}", e)
        }
    }
    
    /**
     * Create a sprint review in Supabase.
     */
    suspend fun createSprintReview(reviewDto: SprintReviewDto): Result<SprintReviewDto> = withContext(Dispatchers.IO) {
        return@withContext try {
            val client = supabaseManager.getClient()
            client.postgrest[tableName]
                .insert(reviewDto)
            
            Result.Success(reviewDto)
        } catch (e: Exception) {
            Log.e(TAG, "Error creating sprint review: ${e.message}", e)
            Result.Error("Failed to create sprint review: ${e.message}", e)
        }
    }
    
    /**
     * Update a sprint review in Supabase.
     */
    suspend fun updateSprintReview(reviewDto: SprintReviewDto): Result<SprintReviewDto> = withContext(Dispatchers.IO) {
        return@withContext try {
            val client = supabaseManager.getClient()
            client.postgrest[tableName]
                .update({
                    set("rating", reviewDto.rating)
                    set("updated_at", System.currentTimeMillis())
                }) {
                    filter {
                        eq("id", reviewDto.id)
                    }
                }
            
            Result.Success(reviewDto)
        } catch (e: Exception) {
            Log.e(TAG, "Error updating sprint review: ${e.message}", e)
            Result.Error("Failed to update sprint review: ${e.message}", e)
        }
    }
    
    /**
     * Delete a sprint review from Supabase.
     */
    suspend fun deleteSprintReview(reviewId: String): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            val client = supabaseManager.getClient()
            client.postgrest[tableName]
                .delete {
                    filter {
                        eq("id", reviewId)
                    }
                }
            
            Result.Success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting sprint review: ${e.message}", e)
            Result.Error("Failed to delete sprint review: ${e.message}", e)
        }
    }
    
    companion object {
        private const val TAG = "SprintReviewApiService"
    }
    
    /**
     * Upsert (insert or update) a sprint review in Supabase.
     * This method checks if the review exists and either updates it or creates a new one.
     */
    suspend fun upsertSprintReview(reviewDto: SprintReviewDto): Result<SprintReviewDto> = withContext(Dispatchers.IO) {
        return@withContext try {
            val client = supabaseManager.getClient()
            
            // Check if the review exists for this sprint
            val existingReview = getSprintReviewBySprintId(reviewDto.sprint_id)
            
            when {
                // If we have a successful result with a non-null review, update it
                existingReview is Result.Success && existingReview.data != null -> {
                    // Create a copy with the existing ID
                    val updatedDto = reviewDto.copy(id = existingReview.data.id)
                    updateSprintReview(updatedDto)
                }
                // Otherwise create a new review
                else -> {
                    createSprintReview(reviewDto)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error upserting sprint review: ${e.message}", e)
            Result.Error("Failed to upsert sprint review: ${e.message}", e)
        }
    }
}
