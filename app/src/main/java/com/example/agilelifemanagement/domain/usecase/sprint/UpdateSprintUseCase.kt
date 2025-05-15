package com.example.agilelifemanagement.domain.usecase.sprint

import com.example.agilelifemanagement.domain.model.Result
import com.example.agilelifemanagement.domain.model.Sprint
import com.example.agilelifemanagement.domain.repository.SprintRepository
import javax.inject.Inject

/**
 * Use case for updating an existing sprint.
 */
class UpdateSprintUseCase @Inject constructor(
    private val sprintRepository: SprintRepository
) {
    suspend operator fun invoke(sprint: Sprint): Result<Unit> {
        return try {
            sprintRepository.updateSprint(sprint)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to update sprint")
        }
    }
}
