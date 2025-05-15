package com.example.agilelifemanagement.domain.usecase.sprint

import com.example.agilelifemanagement.domain.model.Result
import com.example.agilelifemanagement.domain.model.SprintReview
import com.example.agilelifemanagement.domain.repository.SprintRepository
import javax.inject.Inject

/**
 * Use case for creating a new sprint review.
 */
class CreateSprintReviewUseCase @Inject constructor(
    private val sprintRepository: SprintRepository
) {
    suspend operator fun invoke(sprintReview: SprintReview): Result<Unit> {
        return try {
            sprintRepository.insertSprintReview(sprintReview)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to create sprint review")
        }
    }
}
