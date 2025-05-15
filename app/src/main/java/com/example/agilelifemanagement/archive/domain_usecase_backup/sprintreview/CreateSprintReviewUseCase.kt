package com.example.agilelifemanagement.domain.usecase.sprintreview

import com.example.agilelifemanagement.domain.model.Result
import com.example.agilelifemanagement.domain.model.SprintReview
import com.example.agilelifemanagement.domain.repository.SprintRepository
import com.example.agilelifemanagement.domain.repository.SprintReviewRepository
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import javax.inject.Inject

/**
 * Use case for creating a sprint review with validation.
 */
class CreateSprintReviewUseCase @Inject constructor(
    private val sprintReviewRepository: SprintReviewRepository,
    private val sprintRepository: SprintRepository
) {
    /**
     * Create a sprint review with validation.
     *
     * @param sprintId The ID of the sprint being reviewed.
     * @param rating The overall rating for the sprint (1-5).
     * @param date The date of the review (defaults to current date).
     * @return Result containing the ID of the created review or an error.
     */
    suspend operator fun invoke(
        sprintId: String,
        rating: Int,
        date: LocalDate = LocalDate.now()
    ): Result<String> {
        // Validation
        if (rating < 1 || rating > 5) {
            return Result.Error("Rating must be between 1 and 5")
        }
        
        // Check if sprint exists and is completed
        val sprint = sprintRepository.getSprintById(sprintId).first()
            ?: return Result.Error("Sprint not found")
        
        if (!sprint.isCompleted) {
            return Result.Error("Cannot create a review for a sprint that is not completed")
        }
        
        // Check if a review already exists for this sprint
        val existingReview = sprintReviewRepository.getReviewBySprintId(sprintId).first()
        if (existingReview != null) {
            return Result.Error("A review already exists for this sprint")
        }
        
        // Create the sprint review
        val sprintReview = SprintReview(
            sprintId = sprintId,
            date = date,
            rating = rating
        )
        
        return try {
            val reviewId = sprintReviewRepository.insertReview(sprintReview)
            Result.Success(reviewId)
        } catch (e: Exception) {
            Result.Error("Failed to create sprint review: ${e.message}", e)
        }
    }
}
