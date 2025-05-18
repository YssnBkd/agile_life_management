package com.example.agilelifemanagement.domain.usecase.day.activity

import com.example.agilelifemanagement.domain.model.DayActivity
import com.example.agilelifemanagement.domain.repository.DayRepository
import javax.inject.Inject

/**
 * Use case for updating an existing day activity.
 */
class UpdateDayActivityUseCase @Inject constructor(
    private val dayRepository: DayRepository
) {
    suspend operator fun invoke(activity: DayActivity): Result<DayActivity> {
        // Pass through to repository
        return dayRepository.updateActivity(activity)
    }
}
