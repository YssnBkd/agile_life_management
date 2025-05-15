package com.example.agilelifemanagement.domain.usecase.wellness

import com.example.agilelifemanagement.domain.model.DailyCheckup
import com.example.agilelifemanagement.domain.repository.WellnessRepository
import javax.inject.Inject

/**
 * Use case for saving or updating a daily wellness checkup.
 * 
 * This use case follows the offline-first approach, saving the data to local storage
 * immediately and then synchronizing with remote storage in the background.
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
