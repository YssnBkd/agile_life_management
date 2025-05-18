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
    suspend operator fun invoke(sprintReview: SprintReview): kotlin.Result<Unit> {
        // Use the createSprintReview method which is available in the repository
        return sprintRepository.createSprintReview(sprintReview).map { Unit }
    }
}
