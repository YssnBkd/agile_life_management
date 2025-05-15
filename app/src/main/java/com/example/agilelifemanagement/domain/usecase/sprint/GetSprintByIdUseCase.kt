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
    operator fun invoke(sprintId: String): Flow<Sprint?> {
        return sprintRepository.getSprintById(sprintId)
    }
}
