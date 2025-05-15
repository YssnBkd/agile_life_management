package com.example.agilelifemanagement.domain.usecase.sprint

import com.example.agilelifemanagement.domain.model.SprintReview
import com.example.agilelifemanagement.domain.repository.SprintRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for retrieving a sprint review by sprint ID.
 */
class GetSprintReviewUseCase @Inject constructor(
    private val sprintRepository: SprintRepository
) {
    operator fun invoke(sprintId: String): Flow<SprintReview?> {
        return sprintRepository.getSprintReviewBySprintId(sprintId)
    }
}
