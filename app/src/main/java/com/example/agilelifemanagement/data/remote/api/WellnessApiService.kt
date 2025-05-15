package com.example.agilelifemanagement.data.remote.api

import com.example.agilelifemanagement.data.remote.model.DailyCheckupDto
import java.time.LocalDate

/**
 * Service interface for wellness-related API operations.
 */
interface WellnessApiService {
    
    /**
     * Get the daily wellness checkup for a specific date from the API.
     * @param date The date to get the checkup for
     * @return The daily checkup DTO for the specified date, or null if not found
     */
    suspend fun getDailyCheckup(date: LocalDate): DailyCheckupDto?
    
    /**
     * Get daily wellness checkups in a date range from the API.
     * @param startDate The start date (inclusive)
     * @param endDate The end date (inclusive)
     * @return List of daily checkup DTOs in the specified date range
     */
    suspend fun getDailyCheckupsInRange(startDate: LocalDate, endDate: LocalDate): List<DailyCheckupDto>
    
    /**
     * Save or update a daily wellness checkup in the API.
     * @param dailyCheckup The daily checkup DTO to save/update
     * @return The saved/updated daily checkup DTO
     */
    suspend fun saveDailyCheckup(dailyCheckup: DailyCheckupDto): DailyCheckupDto
    
    /**
     * Delete a daily wellness checkup from the API.
     * @param date The date of the checkup to delete
     * @return True if the checkup was successfully deleted
     */
    suspend fun deleteDailyCheckup(date: LocalDate): Boolean
    
    /**
     * Get wellness analytics data for a date range from the API.
     * @param startDate The start date (inclusive)
     * @param endDate The end date (inclusive)
     * @return A map of wellness metrics to their values for the specified date range
     */
    suspend fun getWellnessAnalytics(startDate: LocalDate, endDate: LocalDate): Map<String, Double>
}
