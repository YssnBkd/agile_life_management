package com.example.agilelifemanagement.domain.usecase.sprint

import com.example.agilelifemanagement.domain.model.Sprint
import com.example.agilelifemanagement.domain.repository.SprintRepository
import javax.inject.Inject

/**
 * Use case for creating a new sprint.
 * 
 * This use case follows the offline-first approach by immediately saving to local storage
 * and synchronizing with remote sources in the background.
 */
class CreateSprintUseCase @Inject constructor(
    private val sprintRepository: SprintRepository
) {
    /**
     * Creates a new sprint in the system.
     * 
     * @param sprint The sprint to be created
     * @return Result containing the created sprint with its generated ID if successful, or an error
     */
    suspend operator fun invoke(sprint: Sprint): Result<Sprint> {
        return sprintRepository.createSprint(sprint)
    }
}
