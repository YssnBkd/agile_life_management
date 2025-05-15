package com.example.agilelifemanagement.domain.usecase.day

import com.example.agilelifemanagement.domain.model.DayActivity
import com.example.agilelifemanagement.domain.repository.DayRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.time.LocalDate
import javax.inject.Inject

/**
 * Use case for retrieving all activities for a week.
 */
class GetWeekActivitiesUseCase @Inject constructor(
    private val dayRepository: DayRepository
) {
    /**
     * Get all activities for a week starting from the specified date
     * 
     * @param startDate The first day of the week
     * @return A Flow of mapped activities by date
     */
    operator fun invoke(startDate: LocalDate): Flow<Map<LocalDate, List<DayActivity>>> {
        // Create a list of 7 days starting from startDate
        val dates = (0..6).map { startDate.plusDays(it.toLong()) }
        
        // Create a flow for each date's activities
        val activityFlows = dates.map { date ->
            dayRepository.getActivitiesByDate(date)
        }
        
        // Combine all flows into a single flow of a map
        return combine(activityFlows) { activitiesArray ->
            // Zip the dates with their corresponding activities
            dates.zip(activitiesArray.toList()).toMap()
        }
    }
}
