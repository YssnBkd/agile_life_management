package com.example.agilelifemanagement.domain.usecase.sprint

import com.example.agilelifemanagement.domain.model.Sprint
import com.example.agilelifemanagement.domain.repository.SprintRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for retrieving all sprints.
 * This use case follows the offline-first approach by immediately providing local data
 * while syncing with remote sources in the background.
 */
class GetSprintsUseCase @Inject constructor(
    private val sprintRepository: SprintRepository
) {
    /**
     * Gets a Flow of all sprints, automatically updated when the repository data changes.
     * @return A Flow emitting lists of all sprints when changes occur
     */
    operator fun invoke(): Flow<List<Sprint>> {
        return sprintRepository.getAllSprints()
    }
}
