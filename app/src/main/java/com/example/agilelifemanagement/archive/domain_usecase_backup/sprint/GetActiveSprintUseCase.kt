package com.example.agilelifemanagement.domain.usecase.sprint

import com.example.agilelifemanagement.domain.model.Sprint
import com.example.agilelifemanagement.domain.repository.SprintRepository
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import javax.inject.Inject

/**
 * Use case for retrieving the currently active sprint.
 */
class GetActiveSprintUseCase @Inject constructor(
    private val sprintRepository: SprintRepository
) {
    /**
     * Get the currently active sprint for the current date.
     *
     * @return Flow emitting the active sprint if found, or null if not found.
     */
    operator fun invoke(): Flow<Sprint?> {
        val today = LocalDate.now()
        return sprintRepository.getActiveSprintByDate(today)
    }
    
    /**
     * Get the active sprint for a specific date.
     *
     * @param date The date to check for an active sprint.
     * @return Flow emitting the active sprint if found, or null if not found.
     */
    fun forDate(date: LocalDate): Flow<Sprint?> {
        return sprintRepository.getActiveSprintByDate(date)
    }
}
