package com.example.agilelifemanagement.domain.usecase.dailycheckup

import com.example.agilelifemanagement.domain.model.DailyCheckup
import com.example.agilelifemanagement.domain.model.Result
import com.example.agilelifemanagement.domain.repository.DailyCheckupRepository
import com.example.agilelifemanagement.domain.repository.SprintRepository
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import javax.inject.Inject

/**
 * Use case for updating a daily checkup with validation.
 */
class UpdateDailyCheckupUseCase @Inject constructor(
    private val dailyCheckupRepository: DailyCheckupRepository,
    private val sprintRepository: SprintRepository
) {
    /**
     * Update a daily checkup with validation.
     *
     * @param id The ID of the daily checkup to update.
     * @param date The updated date of the checkup.
     * @param sprintId The updated sprint ID for the checkup.
     * @param notes The updated notes for the checkup.
     * @return Result indicating success or an error.
     */
    suspend operator fun invoke(
        id: String,
        date: LocalDate,
        sprintId: String,
        notes: String
    ): Result<Unit> {
        try {
            // Check if checkup exists
            val existingCheckup = dailyCheckupRepository.getCheckupById(id).first()
                ?: return Result.Error("Daily checkup not found")
            
            // If date is changing, check for conflicts
            if (date != existingCheckup.date) {
                val checkupAtNewDate = dailyCheckupRepository.getCheckupByDate(date).first()
                if (checkupAtNewDate != null && checkupAtNewDate.id != id) {
                    return Result.Error("A daily checkup already exists for the new date")
                }
            }
            
            // Validate sprint
            val sprint = sprintRepository.getSprintById(sprintId).first()
                ?: return Result.Error("Sprint not found")
            
            // Validate that the date falls within the sprint's date range
            if (date.isBefore(sprint.startDate) || date.isAfter(sprint.endDate)) {
                return Result.Error("Checkup date must be within the sprint's date range")
            }
            
            // Create updated checkup
            val updatedCheckup = DailyCheckup(
                id = id,
                date = date,
                sprintId = sprintId,
                notes = notes
            )
            
            dailyCheckupRepository.updateCheckup(updatedCheckup)
            return Result.Success(Unit)
        } catch (e: Exception) {
            return Result.Error("Failed to update daily checkup: ${e.message}", e)
        }
    }
}
