package com.example.agilelifemanagement.domain.usecase.sprint

import com.example.agilelifemanagement.domain.model.Result
import com.example.agilelifemanagement.domain.model.Sprint
import com.example.agilelifemanagement.domain.repository.SprintRepository
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import javax.inject.Inject

/**
 * Use case for creating a new sprint with validation.
 */
class CreateSprintUseCase @Inject constructor(
    private val sprintRepository: SprintRepository
) {
    /**
     * Create a new sprint with validation.
     *
     * @param name The name of the sprint (required).
     * @param description The description of the sprint (optional).
     * @param startDate The start date of the sprint (required).
     * @param endDate The end date of the sprint (required).
     * @param isActive Whether the sprint is active (default: false).
     * @return Result containing the ID of the created sprint or an error.
     */
    suspend operator fun invoke(
        name: String,
        description: List<String> = emptyList(),
        tags: List<String> = emptyList(),
        startDate: LocalDate,
        endDate: LocalDate,
        isActive: Boolean = false
    ): Result<String> {
        // Validation
        if (name.isBlank()) {
            return Result.Error("Sprint name cannot be empty")
        }
        
        if (endDate.isBefore(startDate)) {
            return Result.Error("End date cannot be before start date")
        }
        
        // Check for overlapping active sprints if this sprint is active
        if (isActive) {
            val existingActiveSprint = sprintRepository.getActiveSprintByDate(startDate).first()
            if (existingActiveSprint != null) {
                return Result.Error("There is already an active sprint during this period")
            }
        }
        
        // Create sprint
        val sprint = Sprint(
            name = name,
            description = description,
            startDate = startDate,
            endDate = endDate,
            isActive = isActive,
            isCompleted = false
        )
        
        return try {
            val sprintId = sprintRepository.insertSprint(sprint)
            Result.Success(sprintId)
        } catch (e: Exception) {
            Result.Error("Failed to create sprint: ${e.message}", e)
        }
    }
}
