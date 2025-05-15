package com.example.agilelifemanagement.domain.usecase.sprintreview

import com.example.agilelifemanagement.domain.model.SprintReview
import com.example.agilelifemanagement.domain.repository.SprintReviewRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for retrieving a sprint review by sprint ID.
 */
class GetSprintReviewUseCase @Inject constructor(
    private val sprintReviewRepository: SprintReviewRepository
) {
    /**
     * Get a sprint review by sprint ID.
     *
     * @param sprintId The ID of the sprint to get the review for.
     * @return Flow emitting the sprint review if found, or null if not found.
     */
    operator fun invoke(sprintId: String): Flow<SprintReview?> {
        return sprintReviewRepository.getReviewBySprintId(sprintId)
    }
}
