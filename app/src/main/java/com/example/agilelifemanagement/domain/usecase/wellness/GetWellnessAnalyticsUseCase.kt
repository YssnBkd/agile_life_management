package com.example.agilelifemanagement.domain.usecase.wellness

import com.example.agilelifemanagement.domain.model.WellnessAnalytics
import com.example.agilelifemanagement.domain.repository.WellnessRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject

/**
 * Use case for retrieving wellness analytics over a time period.
 * 
 * Note: This is a temporary implementation after the May 15, 2025 architectural change
 * where the data layer was archived for rebuilding.
 */
class GetWellnessAnalyticsUseCase @Inject constructor(
    private val wellnessRepository: WellnessRepository
) {
    /**
     * Gets wellness analytics for a specific time frame (in days).
     * 
     * @param timeFrame Number of days to analyze (e.g., 7 for week, 30 for month)
     * @return Flow emitting wellness analytics or null
     */
    operator fun invoke(timeFrame: Int = 30): Flow<WellnessAnalytics?> {
        // Calculate date range based on timeFrame
        val endDate = LocalDate.now()
        val startDate = endDate.minusDays(timeFrame.toLong())
        
        // Get average metrics for the date range
        val moodFlow = wellnessRepository.getAverageMoodForRange(startDate, endDate)
        val sleepFlow = wellnessRepository.getAverageSleepQualityForRange(startDate, endDate)
        
        // Combine flows to create WellnessAnalytics
        // For the temporary implementation, we create a basic wellness analytics object
        return moodFlow.map { moodAvg ->
            WellnessAnalytics(
                averageMood = moodAvg,
                averageSleepQuality = sleepFlow.first(), // This would be better with Flow.zip in a real implementation
                startDate = startDate,
                endDate = endDate,
                // Other metrics could be added here in a full implementation
                metrics = mapOf(
                    "mood" to moodAvg,
                    "sleep" to sleepFlow.first()
                )
            )
        }
    }
}
