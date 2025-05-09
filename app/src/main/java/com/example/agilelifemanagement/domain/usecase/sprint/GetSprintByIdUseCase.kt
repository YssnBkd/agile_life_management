package com.example.agilelifemanagement.domain.usecase.sprint

import com.example.agilelifemanagement.domain.model.Sprint
import com.example.agilelifemanagement.domain.repository.SprintRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for retrieving a specific sprint by ID.
 */
class GetSprintByIdUseCase @Inject constructor(
    private val sprintRepository: SprintRepository
) {
    /**
     * Get a sprint by its ID.
     *
     * @param id The unique identifier of the sprint.
     * @return Flow emitting the sprint if found, or null if not found.
     */
    operator fun invoke(id: String): Flow<Sprint?> {
        return sprintRepository.getSprintById(id)
    }
}
