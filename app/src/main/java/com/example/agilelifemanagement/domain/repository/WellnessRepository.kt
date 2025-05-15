package com.example.agilelifemanagement.domain.repository

import com.example.agilelifemanagement.domain.model.DailyCheckup
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

/**
 * Repository interface for wellness-related operations.
 * Manages daily checkups and wellness data.
 */
interface WellnessRepository {
    /**
     * Get daily checkup for a specific date.
     * @param date The date to get the checkup for
     * @return A Flow emitting the daily checkup for the specified date, or null if none exists
     */
    fun getDailyCheckup(date: LocalDate): Flow<DailyCheckup?>
    
    /**
     * Get daily checkups for a date range.
     * @param startDate The start date (inclusive)
     * @param endDate The end date (inclusive)
     * @return A Flow emitting lists of daily checkups within the date range
     */
    fun getDailyCheckupsForRange(startDate: LocalDate, endDate: LocalDate): Flow<List<DailyCheckup>>
    
    /**
     * Save or update a daily checkup.
     * @param checkup The checkup to save
     * @return A Result containing the saved checkup with its assigned ID
     */
    suspend fun saveDailyCheckup(checkup: DailyCheckup): Result<DailyCheckup>
    
    /**
     * Delete a daily checkup.
     * @param checkupId The ID of the checkup to delete
     * @return A Result containing a boolean indicating success
     */
    suspend fun deleteDailyCheckup(checkupId: String): Result<Boolean>
    
    /**
     * Get average mood rating for a date range.
     * @param startDate The start date (inclusive)
     * @param endDate The end date (inclusive)
     * @return A Flow emitting the average mood rating as a float
     */
    fun getAverageMoodForRange(startDate: LocalDate, endDate: LocalDate): Flow<Float>
    
    /**
     * Get average sleep quality for a date range.
     * @param startDate The start date (inclusive)
     * @param endDate The end date (inclusive)
     * @return A Flow emitting the average sleep quality as a float
     */
    fun getAverageSleepQualityForRange(startDate: LocalDate, endDate: LocalDate): Flow<Float>
}
