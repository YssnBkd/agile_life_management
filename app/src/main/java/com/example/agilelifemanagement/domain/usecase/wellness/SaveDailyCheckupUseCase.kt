package com.example.agilelifemanagement.domain.usecase.wellness

import com.example.agilelifemanagement.domain.model.DailyCheckup
import com.example.agilelifemanagement.domain.repository.WellnessRepository
import kotlin.Result
import javax.inject.Inject

/**
 * Use case for saving or updating a daily wellness checkup.
 * 
 * Note: This is a temporary implementation after the May 15, 2025 architectural change
 * where the data layer was archived for rebuilding.
 */
class SaveDailyCheckupUseCase @Inject constructor(
    private val wellnessRepository: WellnessRepository
) {
    /**
     * Saves or updates a daily wellness checkup.
     * 
     * @param checkup The daily checkup data to save
     * @return Result containing the saved checkup with updated information if successful, or an error
     */
    suspend operator fun invoke(checkup: DailyCheckup): Result<DailyCheckup> {
        return wellnessRepository.saveDailyCheckup(checkup)
    }
}
