package com.example.agilelifemanagement.domain.usecase.day.activity

import com.example.agilelifemanagement.domain.model.DayActivity
import com.example.agilelifemanagement.domain.model.Result
import com.example.agilelifemanagement.domain.repository.DayRepository
import javax.inject.Inject

/**
 * Use case for updating an existing day activity.
 */
class UpdateDayActivityUseCase @Inject constructor(
    private val dayRepository: DayRepository
) {
    suspend operator fun invoke(activity: DayActivity): Result<Unit> {
        return try {
            dayRepository.updateActivity(activity)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to update activity")
        }
    }
}
