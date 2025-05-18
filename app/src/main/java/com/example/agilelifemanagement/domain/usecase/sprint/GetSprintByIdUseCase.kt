package com.example.agilelifemanagement.domain.usecase.sprint

import com.example.agilelifemanagement.domain.model.Sprint
import com.example.agilelifemanagement.domain.repository.SprintRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import timber.log.Timber
import javax.inject.Inject

/**
 * Use case for retrieving a specific sprint by ID.
 */
class GetSprintByIdUseCase @Inject constructor(
    private val sprintRepository: SprintRepository
) {
    // Converting from the repository's suspend function returning Result<Sprint> 
    // to a Flow<Sprint?> for reactive UI consumption
    operator fun invoke(sprintId: String): Flow<Sprint?> = flow {
        // Execute the repository call
        val result = sprintRepository.getSprintById(sprintId)
        
        // Emit the sprint if successful, otherwise emit null
        emit(result.getOrNull())
    }.catch { exception ->
        // Handle any exceptions during the flow and emit null
        Timber.e(exception, "Error getting sprint: $sprintId")
        emit(null)
    }
}
