package com.example.agilelifemanagement.domain.usecase.dailycheckup

import com.example.agilelifemanagement.domain.model.DailyCheckup
import com.example.agilelifemanagement.domain.repository.DailyCheckupRepository
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import javax.inject.Inject

/**
 * Use case for retrieving daily checkups.
 */
class GetDailyCheckupUseCase @Inject constructor(
    private val dailyCheckupRepository: DailyCheckupRepository
) {
    /**
     * Get a daily checkup for a specific date.
     *
     * @param date The date to get the checkup for.
     * @return Flow emitting the daily checkup if found, or null if not found.
     */
    operator fun invoke(date: LocalDate): Flow<DailyCheckup?> {
        return dailyCheckupRepository.getCheckupByDate(date)
    }
    
    /**
     * Get the daily checkup for today.
     *
     * @return Flow emitting today's daily checkup if found, or null if not found.
     */
    fun today(): Flow<DailyCheckup?> {
        val today = LocalDate.now()
        return dailyCheckupRepository.getCheckupByDate(today)
    }
    
    /**
     * Get all daily checkups for a specific sprint.
     *
     * @param sprintId The ID of the sprint to get checkups for.
     * @return Flow emitting a list of daily checkups for the sprint.
     */
    fun forSprint(sprintId: String): Flow<List<DailyCheckup>> {
        return dailyCheckupRepository.getCheckupsBySprintId(sprintId)
    }
}
