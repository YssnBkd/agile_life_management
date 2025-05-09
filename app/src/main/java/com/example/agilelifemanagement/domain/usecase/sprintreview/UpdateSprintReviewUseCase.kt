package com.example.agilelifemanagement.domain.usecase.sprintreview

import com.example.agilelifemanagement.domain.model.Result
import com.example.agilelifemanagement.domain.model.SprintReview
import com.example.agilelifemanagement.domain.repository.SprintReviewRepository
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import javax.inject.Inject

/**
 * Use case for updating a sprint review with validation.
 */
class UpdateSprintReviewUseCase @Inject constructor(
    private val sprintReviewRepository: SprintReviewRepository
) {
    /**
     * Update a sprint review with validation.
     *
     * @param id The ID of the sprint review to update.
     * @param rating The updated rating for the sprint (1-5).
     * @param date The updated date of the review.
     * @return Result indicating success or an error.
     */
    suspend operator fun invoke(
        id: String,
        rating: Int,
        date: LocalDate
    ): Result<Unit> {
        // Validation
        if (rating < 1 || rating > 5) {
            return Result.Error("Rating must be between 1 and 5")
        }
        
        // Check if review exists
        val existingReview = sprintReviewRepository.getReviewById(id).first()
            ?: return Result.Error("Sprint review not found")
        
        // Create updated review
        val updatedReview = SprintReview(
            id = id,
            sprintId = existingReview.sprintId,
            date = date,
            rating = rating
        )
        
        return try {
            sprintReviewRepository.updateReview(updatedReview)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error("Failed to update sprint review: ${e.message}", e)
        }
    }
}
