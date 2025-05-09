package com.example.agilelifemanagement.domain.repository


import com.example.agilelifemanagement.domain.model.SprintReview
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for SprintReview operations.
 */
interface SprintReviewRepository {
    fun getReviews(): Flow<List<SprintReview>>
    fun getReviewById(id: String): Flow<SprintReview?>
    fun getReviewBySprintId(sprintId: String): Flow<SprintReview?>
    suspend fun insertReview(review: SprintReview): String
    suspend fun updateReview(review: SprintReview)
    suspend fun deleteReview(id: String)

}
