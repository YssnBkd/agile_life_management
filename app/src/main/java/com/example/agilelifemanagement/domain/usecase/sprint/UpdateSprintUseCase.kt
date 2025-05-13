package com.example.agilelifemanagement.domain.usecase.sprint

import com.example.agilelifemanagement.domain.model.Result
import com.example.agilelifemanagement.domain.model.Sprint
import com.example.agilelifemanagement.domain.repository.SprintRepository
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import javax.inject.Inject

/**
 * Use case for updating an existing sprint with validation.
 */
class UpdateSprintUseCase @Inject constructor(
    private val sprintRepository: SprintRepository
) {
    /**
     * Update an existing sprint with validation.
     *
     * @param id The ID of the sprint to update (required).
     * @param name The updated name of the sprint (required).
     * @param description The updated description of the sprint (optional).
     * @param startDate The updated start date of the sprint (required).
     * @param endDate The updated end date of the sprint (required).
     * @param isActive The updated active status of the sprint.
     * @param isCompleted The updated completion status of the sprint.
     * @return Result indicating success or an error.
     */
    suspend operator fun invoke(
        id: String,
        name: String,
        description: List<String> = emptyList(),
        tags: List<String> = emptyList(),
        startDate: LocalDate,
        endDate: LocalDate,
        isActive: Boolean,
        isCompleted: Boolean
    ): Result<Unit> {
        // Validation
        if (name.isBlank()) {
            return Result.Error("Sprint name cannot be empty")
        }
        
        if (endDate.isBefore(startDate)) {
            return Result.Error("End date cannot be before start date")
        }
        
        // Check if sprint exists
        val existingSprint = sprintRepository.getSprintById(id).first()
            ?: return Result.Error("Sprint not found")
        
        // Check for overlapping active sprints if this sprint is active
        if (isActive && !existingSprint.isActive) {
            val existingActiveSprint = sprintRepository.getActiveSprintByDate(startDate).first()
            if (existingActiveSprint != null && existingActiveSprint.id != id) {
                return Result.Error("There is already an active sprint during this period")
            }
        }
        
        // Create updated sprint
        val updatedSprint = Sprint(
            id = id,
            name = name,
            description = description,
            startDate = startDate,
            endDate = endDate,
            isActive = isActive,
            isCompleted = isCompleted
        )
        
        // Since updateSprint now returns a Result type directly, we don't need a try-catch block
        return sprintRepository.updateSprint(updatedSprint)
    }
}
