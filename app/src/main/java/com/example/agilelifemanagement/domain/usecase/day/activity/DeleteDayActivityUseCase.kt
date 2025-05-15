package com.example.agilelifemanagement.domain.usecase.day.activity

import com.example.agilelifemanagement.domain.model.Result
import com.example.agilelifemanagement.domain.repository.DayRepository
import javax.inject.Inject

/**
 * Use case for deleting a day activity.
 */
class DeleteDayActivityUseCase @Inject constructor(
    private val dayRepository: DayRepository
) {
    suspend operator fun invoke(activityId: String): Result<Unit> {
        return try {
            dayRepository.deleteActivity(activityId)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to delete activity")
        }
    }
}
