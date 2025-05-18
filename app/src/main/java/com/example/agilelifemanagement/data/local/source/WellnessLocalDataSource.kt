package com.example.agilelifemanagement.data.local.source

import com.example.agilelifemanagement.data.local.dao.DailyCheckupDao
import com.example.agilelifemanagement.data.local.entity.DailyCheckupEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Local data source for wellness-related data.
 * This class provides access to the local database for wellness operations.
 */
@Singleton
class WellnessLocalDataSource @Inject constructor(
    private val dailyCheckupDao: DailyCheckupDao
) {
    /**
     * Get daily checkup for a specific date.
     * @param date The date to get the checkup for
     * @return A Flow emitting the daily checkup for the specified date, or null if none exists
     */
    fun getDailyCheckup(date: LocalDate): Flow<DailyCheckupEntity?> {
        return dailyCheckupDao.getDailyCheckupByDate(date)
    }
    
    /**
     * Get daily checkup for a specific date synchronously.
     * @param date The date to get the checkup for
     * @return The daily checkup for the specified date, or null if none exists
     */
    suspend fun getDailyCheckupSync(date: LocalDate): DailyCheckupEntity? {
        return dailyCheckupDao.getDailyCheckupByDateSync(date)
    }
    
    /**
     * Get daily checkups for a date range.
     * @param startDate The start date (inclusive)
     * @param endDate The end date (inclusive)
     * @return A Flow emitting lists of daily checkups within the date range
     */
    fun getDailyCheckupsForRange(startDate: LocalDate, endDate: LocalDate): Flow<List<DailyCheckupEntity>> {
        return dailyCheckupDao.getDailyCheckupsByDateRange(startDate, endDate)
    }
    
    /**
     * Get daily checkups for a date range synchronously.
     * @param startDate The start date (inclusive)
     * @param endDate The end date (inclusive)
     * @return A list of daily checkups within the date range
     */
    suspend fun getDailyCheckupsForRangeSync(startDate: LocalDate, endDate: LocalDate): List<DailyCheckupEntity> {
        return dailyCheckupDao.getDailyCheckupsByDateRangeSync(startDate, endDate)
    }
    
    /**
     * Insert or update a daily checkup.
     * @param checkup The daily checkup entity
     * @return The row ID of the inserted/updated checkup
     */
    suspend fun insertDailyCheckup(checkup: DailyCheckupEntity): Long {
        return dailyCheckupDao.insertDailyCheckup(checkup)
    }
    
    /**
     * Delete a daily checkup.
     * @param checkupId The ID of the checkup to delete
     * @return The number of rows affected (should be 0 or 1)
     */
    suspend fun deleteDailyCheckup(checkupId: String): Int {
        return dailyCheckupDao.deleteDailyCheckup(checkupId)
    }
    
    /**
     * Get average mood rating for a date range.
     * @param startDate The start date (inclusive)
     * @param endDate The end date (inclusive)
     * @return A Flow emitting the average mood rating as a float
     */
    fun getAverageMoodForRange(startDate: LocalDate, endDate: LocalDate): Flow<Float> {
        return dailyCheckupDao.getAverageMoodForRange(startDate, endDate)
    }
    
    /**
     * Get average sleep quality for a date range.
     * @param startDate The start date (inclusive)
     * @param endDate The end date (inclusive)
     * @return A Flow emitting the average sleep quality as a float
     */
    fun getAverageSleepQualityForRange(startDate: LocalDate, endDate: LocalDate): Flow<Float> {
        return dailyCheckupDao.getAverageSleepQualityForRange(startDate, endDate)
    }
}
