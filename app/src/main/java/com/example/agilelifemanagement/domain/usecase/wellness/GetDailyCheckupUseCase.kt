package com.example.agilelifemanagement.domain.usecase.wellness

import com.example.agilelifemanagement.domain.model.DailyCheckup
import com.example.agilelifemanagement.domain.repository.WellnessRepository
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import javax.inject.Inject

/**
 * Use case for retrieving a daily wellness checkup for a specific date.
 */
class GetDailyCheckupUseCase @Inject constructor(
    private val wellnessRepository: WellnessRepository
) {
    operator fun invoke(date: LocalDate): Flow<DailyCheckup?> {
        return wellnessRepository.getDailyCheckupByDate(date)
    }
}
