package com.example.agilelifemanagement.domain.usecase.day.activity

import com.example.agilelifemanagement.domain.repository.DayRepository
import javax.inject.Inject

/**
 * Use case for deleting a day activity.
 */
class DeleteDayActivityUseCase @Inject constructor(
    private val dayRepository: DayRepository
) {
    suspend operator fun invoke(activityId: String): Result<Unit> {
        // Map the Boolean result to Unit for clients that don't need the boolean value
        return dayRepository.deleteActivity(activityId).map { Unit }
    }
}
