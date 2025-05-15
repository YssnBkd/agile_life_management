package com.example.agilelifemanagement.domain.usecase.day

import com.example.agilelifemanagement.domain.model.DaySchedule
import com.example.agilelifemanagement.domain.model.Result
import com.example.agilelifemanagement.domain.repository.DayRepository
import javax.inject.Inject

/**
 * Use case for updating a day's schedule.
 */
class UpdateDayScheduleUseCase @Inject constructor(
    private val dayRepository: DayRepository
) {
    suspend operator fun invoke(schedule: DaySchedule): Result<Unit> {
        return try {
            dayRepository.updateSchedule(schedule)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to update day schedule")
        }
    }
}
