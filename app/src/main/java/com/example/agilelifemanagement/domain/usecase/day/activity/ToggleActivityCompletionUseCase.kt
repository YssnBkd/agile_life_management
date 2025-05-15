package com.example.agilelifemanagement.domain.usecase.day.activity

import com.example.agilelifemanagement.domain.model.Result
import com.example.agilelifemanagement.domain.repository.DayRepository
import javax.inject.Inject

/**
 * Use case for toggling the completion status of a day activity.
 */
class ToggleActivityCompletionUseCase @Inject constructor(
    private val dayRepository: DayRepository
) {
    suspend operator fun invoke(activityId: String, completed: Boolean): Result<Unit> {
        return try {
            dayRepository.toggleActivityCompletion(activityId, completed)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to toggle activity completion")
        }
    }
}
