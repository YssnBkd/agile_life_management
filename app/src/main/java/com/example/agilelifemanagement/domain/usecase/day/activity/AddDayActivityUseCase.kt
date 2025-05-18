package com.example.agilelifemanagement.domain.usecase.day.activity

import com.example.agilelifemanagement.domain.model.DayActivity
import com.example.agilelifemanagement.domain.repository.DayRepository
import javax.inject.Inject
import kotlin.Result

/**
 * Use case for adding a new day activity.
 * This use case follows the offline-first approach by immediately saving to local storage
 * and syncing with remote sources in the background.
 */
class AddDayActivityUseCase @Inject constructor(
    private val dayRepository: DayRepository
) {
    /**
     * Creates a new activity in the system.
     * @param activity The activity to create
     * @return Result containing the created activity with its generated ID if successful, or an error
     */
    suspend operator fun invoke(activity: DayActivity): Result<DayActivity> {
        return dayRepository.addActivity(activity)
    }
}
