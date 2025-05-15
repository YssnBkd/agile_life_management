package com.example.agilelifemanagement.data.remote.source

import com.example.agilelifemanagement.domain.model.DailyCheckup
import java.time.LocalDate

/**
 * Remote data source interface for wellness data.
 * Defines the contract for accessing and manipulating wellness data from remote sources.
 */
interface WellnessRemoteDataSource {
    
    /**
     * Get the daily wellness checkup for a specific date from the remote source.
     * @param date The date to get the checkup for
     * @return The daily checkup for the specified date, or null if not found
     */
    suspend fun getDailyCheckup(date: LocalDate): DailyCheckup?
    
    /**
     * Get daily wellness checkups in a date range from the remote source.
     * @param startDate The start date (inclusive)
     * @param endDate The end date (inclusive)
     * @return List of daily checkups in the specified date range
     */
    suspend fun getDailyCheckupsInRange(startDate: LocalDate, endDate: LocalDate): List<DailyCheckup>
    
    /**
     * Save or update a daily wellness checkup in the remote source.
     * @param dailyCheckup The daily checkup to save/update
     * @return The saved/updated daily checkup
     */
    suspend fun saveDailyCheckup(dailyCheckup: DailyCheckup): DailyCheckup
    
    /**
     * Delete a daily wellness checkup from the remote source.
     * @param date The date of the checkup to delete
     * @return True if the checkup was successfully deleted
     */
    suspend fun deleteDailyCheckup(date: LocalDate): Boolean
    
    /**
     * Get wellness analytics data for a date range from the remote source.
     * @param startDate The start date (inclusive)
     * @param endDate The end date (inclusive)
     * @return A map of wellness metrics to their values for the specified date range
     */
    suspend fun getWellnessAnalytics(startDate: LocalDate, endDate: LocalDate): Map<String, Double>
}
