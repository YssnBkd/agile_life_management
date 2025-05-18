package com.example.agilelifemanagement.data.remote.source

import com.example.agilelifemanagement.domain.model.DailyCheckup
import timber.log.Timber
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of [WellnessRemoteDataSource] that serves as a stub for future remote integration.
 * This implementation provides placeholder functionality that will be replaced with
 * actual remote API calls in future iterations.
 */
@Singleton
class WellnessRemoteDataSourceImpl @Inject constructor() : WellnessRemoteDataSource {
    
    override suspend fun getDailyCheckup(date: LocalDate): DailyCheckup? {
        Timber.d("Remote: getDailyCheckup called (stub) for date: $date")
        return null // Stub implementation
    }
    
    override suspend fun getDailyCheckupsForRange(startDate: LocalDate, endDate: LocalDate): List<DailyCheckup> {
        Timber.d("Remote: getDailyCheckupsForRange called (stub) for range: $startDate to $endDate")
        return emptyList() // Stub implementation
    }
    
    override suspend fun saveDailyCheckup(dailyCheckup: DailyCheckup): DailyCheckup {
        Timber.d("Remote: saveDailyCheckup called (stub) for date: ${dailyCheckup.date}")
        return dailyCheckup // Stub implementation, just returns the input checkup
    }
    
    override suspend fun deleteDailyCheckup(checkupId: String): Boolean {
        Timber.d("Remote: deleteDailyCheckup called (stub) for checkup ID: $checkupId")
        return true // Stub implementation, pretend it always succeeds
    }
    
    override suspend fun getWellnessAnalytics(startDate: LocalDate, endDate: LocalDate): Map<String, Double> {
        Timber.d("Remote: getWellnessAnalytics called (stub) for range: $startDate to $endDate")
        return emptyMap() // Stub implementation
    }
}
