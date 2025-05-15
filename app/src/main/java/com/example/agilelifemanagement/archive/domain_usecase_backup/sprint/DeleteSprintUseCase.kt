package com.example.agilelifemanagement.domain.usecase.sprint

import com.example.agilelifemanagement.domain.model.Result
import com.example.agilelifemanagement.domain.repository.SprintRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/**
 * Use case for deleting a sprint and handling associated entities.
 */
class DeleteSprintUseCase @Inject constructor(
    private val sprintRepository: SprintRepository
) {
    /**
     * Delete a sprint by its ID.
     *
     * @param id The ID of the sprint to delete.
     * @return Result indicating success or an error.
     */
    suspend operator fun invoke(id: String): Result<Unit> {
        // Check if sprint exists
        val sprint = sprintRepository.getSprintById(id).first()
            ?: return Result.Error("Sprint not found")
        
        // Check if sprint is active
        if (sprint.isActive) {
            return Result.Error("Cannot delete an active sprint")
        }
        
        // Delete the sprint and pass through the Result
        return sprintRepository.deleteSprint(id)
    }
}
