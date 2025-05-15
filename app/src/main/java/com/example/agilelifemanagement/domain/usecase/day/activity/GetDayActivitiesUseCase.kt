package com.example.agilelifemanagement.domain.usecase.day.activity

import com.example.agilelifemanagement.domain.model.DayActivity
import com.example.agilelifemanagement.domain.repository.DayActivityRepository
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import javax.inject.Inject

/**
 * Use case for retrieving day activities for a specific date.
 * This use case follows the offline-first approach by immediately providing local data
 * while syncing with remote sources in the background.
 */
class GetDayActivitiesUseCase @Inject constructor(
    private val dayActivityRepository: DayActivityRepository
) {
    /**
     * Gets a Flow of activities for a specific date, automatically updated when data changes.
     * @param date The date to get activities for
     * @return A Flow emitting lists of activities for the specified date when changes occur
     */
    operator fun invoke(date: LocalDate): Flow<List<DayActivity>> {
        return dayActivityRepository.getActivitiesForDate(date)
    }
}
