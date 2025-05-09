package com.example.agilelifemanagement.domain.usecase.dailycheckup

import com.example.agilelifemanagement.domain.model.DailyCheckup
import com.example.agilelifemanagement.domain.model.Result
import com.example.agilelifemanagement.domain.repository.DailyCheckupRepository
import com.example.agilelifemanagement.domain.repository.SprintRepository
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import javax.inject.Inject

/**
 * Use case for creating a daily checkup with validation.
 */
class CreateDailyCheckupUseCase @Inject constructor(
    private val dailyCheckupRepository: DailyCheckupRepository,
    private val sprintRepository: SprintRepository
) {
    /**
     * Create a daily checkup with validation.
     *
     * @param date The date of the checkup (defaults to current date).
     * @param sprintId The ID of the sprint this checkup is associated with.
     * @param notes Additional notes for the checkup (optional).
     * @return Result containing the ID of the created checkup or an error.
     */
    suspend operator fun invoke(
        date: LocalDate = LocalDate.now(),
        sprintId: String,
        notes: String = ""
    ): Result<String> {
        try {
            // Check if a checkup already exists for this date
            val existingCheckup = dailyCheckupRepository.getCheckupByDate(date).first()
            if (existingCheckup != null) {
                return Result.Error("A daily checkup already exists for this date")
            }
            
            // Validate sprint
            val sprint = sprintRepository.getSprintById(sprintId).first()
                ?: return Result.Error("Sprint not found")
            
            // Validate that the date falls within the sprint's date range
            if (date.isBefore(sprint.startDate) || date.isAfter(sprint.endDate)) {
                return Result.Error("Checkup date must be within the sprint's date range")
            }
            
            // Create the daily checkup
            val dailyCheckup = DailyCheckup(
                date = date,
                sprintId = sprintId,
                notes = notes
            )
            
            val checkupId = dailyCheckupRepository.insertCheckup(dailyCheckup)
            return Result.Success(checkupId)
        } catch (e: Exception) {
            return Result.Error("Failed to create daily checkup: ${e.message}", e)
        }
    }
}
