package com.example.agilelifemanagement.domain.usecase.day.activity

import com.example.agilelifemanagement.domain.model.DayActivity
import com.example.agilelifemanagement.domain.repository.DayRepository
import javax.inject.Inject
import kotlin.Result

/**
 * Use case for toggling the completion status of a day activity.
 */
class ToggleActivityCompletionUseCase @Inject constructor(
    private val dayRepository: DayRepository
) {
    suspend operator fun invoke(activityId: String): Result<DayActivity> {
        return dayRepository.toggleActivityCompletion(activityId)
    }
}
