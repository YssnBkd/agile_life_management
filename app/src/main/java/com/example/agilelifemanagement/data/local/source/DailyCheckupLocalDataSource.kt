package com.example.agilelifemanagement.data.local.source

import com.example.agilelifemanagement.data.local.dao.DailyCheckupDao
import com.example.agilelifemanagement.data.local.entity.DailyCheckupEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import javax.inject.Inject

/**
 * Local data source for daily checkups.
 * Uses Room DAO to perform database operations.
 */
class DailyCheckupLocalDataSource @Inject constructor(
    private val checkupDao: DailyCheckupDao
) {
    /**
     * Get a checkup for a specific date.
     */
    fun observeCheckupByDate(date: LocalDate): Flow<DailyCheckupEntity?> = 
        checkupDao.getDailyCheckupByDate(date)
    
    /**
     * Get checkups for a date range.
     */
    fun observeCheckupsForDateRange(startDate: LocalDate, endDate: LocalDate): Flow<List<DailyCheckupEntity>> = 
        checkupDao.getDailyCheckupsByDateRange(startDate, endDate)
    
    /**
     * Get average mood rating for a date range.
     */
    fun getAverageMoodForDateRange(startDate: LocalDate, endDate: LocalDate): Flow<Float> = 
        checkupDao.getAverageMoodForRange(startDate, endDate)
    
    /**
     * Get average sleep quality for a date range.
     */
    fun getAverageSleepQualityForDateRange(startDate: LocalDate, endDate: LocalDate): Flow<Float> = 
        checkupDao.getAverageSleepQualityForRange(startDate, endDate)
    
    /**
     * Get average stress level for a date range.
     */
    fun getAverageStressLevelForDateRange(startDate: LocalDate, endDate: LocalDate): Flow<Float> = 
        checkupDao.getAverageStressLevelForRange(startDate, endDate)
    
    /**
     * Get average energy level for a date range.
     */
    fun getAverageEnergyLevelForDateRange(startDate: LocalDate, endDate: LocalDate): Flow<Float> = 
        checkupDao.getAverageEnergyLevelForRange(startDate, endDate)
    
    /**
     * Get a specific checkup by ID.
     */
    suspend fun getCheckupById(checkupId: String): DailyCheckupEntity? = 
        checkupDao.getCheckupById(checkupId)
    
    /**
     * Insert a checkup.
     */
    suspend fun insertCheckup(checkup: DailyCheckupEntity) {
        checkupDao.insertDailyCheckup(checkup)
    }
    
    /**
     * Insert multiple checkups.
     */
    suspend fun insertCheckups(checkups: List<DailyCheckupEntity>) {
        checkupDao.insertCheckups(checkups)
    }
    
    /**
     * Update a checkup.
     */
    suspend fun updateCheckup(checkup: DailyCheckupEntity): Int =
        checkupDao.updateCheckup(checkup)
    
    /**
     * Delete a checkup.
     */
    suspend fun deleteCheckup(checkupId: String): Int =
        checkupDao.deleteDailyCheckup(checkupId)
}
