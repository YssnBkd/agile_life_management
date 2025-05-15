package com.example.agilelifemanagement.domain.usecase.wellness

import com.example.agilelifemanagement.domain.model.DailyCheckup
import com.example.agilelifemanagement.domain.repository.WellnessRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject

/**
 * Use case for retrieving wellness analytics over a time period.
 */
class GetWellnessAnalyticsUseCase @Inject constructor(
    private val wellnessRepository: WellnessRepository
) {
    data class WellnessAnalytics(
        val averageMoodScore: Float,
        val averageEnergyLevel: Float,
        val averageStressLevel: Float,
        val sleepQualityTrend: List<Pair<LocalDate, Int>>,
        val moodTrend: List<Pair<LocalDate, Int>>,
        val completionRate: Float
    )
    
    operator fun invoke(startDate: LocalDate, endDate: LocalDate): Flow<WellnessAnalytics> {
        return wellnessRepository.getCheckupsBetweenDates(startDate, endDate)
            .map { checkups ->
                val validCheckups = checkups.filterNotNull()
                
                // Default values if no valid checkups
                if (validCheckups.isEmpty()) {
                    return@map WellnessAnalytics(
                        averageMoodScore = 0f,
                        averageEnergyLevel = 0f,
                        averageStressLevel = 0f,
                        sleepQualityTrend = emptyList(),
                        moodTrend = emptyList(),
                        completionRate = 0f
                    )
                }
                
                // Calculate average metrics
                val avgMood = validCheckups.map { it.moodRating }.average().toFloat()
                val avgEnergy = validCheckups.map { it.energyLevel }.average().toFloat()
                val avgStress = validCheckups.map { it.stressLevel }.average().toFloat()
                
                // Create trend data
                val sleepQualityTrend = validCheckups.map { 
                    Pair(it.date, it.sleepQuality) 
                }.sortedBy { it.first }
                
                val moodTrend = validCheckups.map { 
                    Pair(it.date, it.moodRating) 
                }.sortedBy { it.first }
                
                // Calculate completion rate (days with checkups / total days in range)
                val totalDaysInRange = java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate.plusDays(1))
                val completionRate = if (totalDaysInRange > 0) {
                    validCheckups.size.toFloat() / totalDaysInRange.toFloat()
                } else {
                    0f
                }
                
                WellnessAnalytics(
                    averageMoodScore = avgMood,
                    averageEnergyLevel = avgEnergy,
                    averageStressLevel = avgStress,
                    sleepQualityTrend = sleepQualityTrend,
                    moodTrend = moodTrend,
                    completionRate = completionRate
                )
            }
    }
}
