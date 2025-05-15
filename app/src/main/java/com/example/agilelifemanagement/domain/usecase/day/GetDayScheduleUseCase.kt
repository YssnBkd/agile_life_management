package com.example.agilelifemanagement.domain.usecase.day

import com.example.agilelifemanagement.domain.model.DaySchedule
import com.example.agilelifemanagement.domain.repository.DayRepository
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import javax.inject.Inject

/**
 * Use case for retrieving a day's schedule by date.
 */
class GetDayScheduleUseCase @Inject constructor(
    private val dayRepository: DayRepository
) {
    operator fun invoke(date: LocalDate): Flow<DaySchedule?> {
        return dayRepository.getScheduleByDate(date)
    }
}
