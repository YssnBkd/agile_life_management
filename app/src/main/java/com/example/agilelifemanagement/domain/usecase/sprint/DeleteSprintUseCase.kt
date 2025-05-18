package com.example.agilelifemanagement.domain.usecase.sprint

import com.example.agilelifemanagement.domain.repository.SprintRepository
import javax.inject.Inject

/**
 * Use case for deleting a sprint.
 * 
 * This use case follows the offline-first approach, ensuring the sprint is deleted
 * from local storage immediately and then synchronizing with remote storage in the background.
 */
class DeleteSprintUseCase @Inject constructor(
    private val sprintRepository: SprintRepository
) {
    /**
     * Deletes a sprint with the given ID.
     * 
     * @param sprintId The unique identifier of the sprint to delete
     * @return Result indicating success or error with details
     */
    suspend operator fun invoke(sprintId: String): Result<Unit> {
        // Map the Boolean result to Unit for clients that don't need the boolean value
        return sprintRepository.deleteSprint(sprintId).map { Unit }
    }
}
