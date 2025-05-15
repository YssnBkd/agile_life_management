package com.example.agilelifemanagement.domain.usecase.sprint

import com.example.agilelifemanagement.domain.model.Sprint
import com.example.agilelifemanagement.domain.repository.SprintRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for retrieving all sprints with optional filtering.
 */
class GetSprintsUseCase @Inject constructor(
    private val sprintRepository: SprintRepository
) {
    /**
     * Get all sprints.
     */
    operator fun invoke(): Flow<List<Sprint>> {
        return sprintRepository.getSprints()
    }
}
