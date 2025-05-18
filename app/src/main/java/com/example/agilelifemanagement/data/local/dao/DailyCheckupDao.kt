package com.example.agilelifemanagement.data.local.dao

import androidx.room.*
import com.example.agilelifemanagement.data.local.entity.DailyCheckupEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

/**
 * Room DAO for accessing and manipulating daily checkup data in the database.
 */
@Dao
interface DailyCheckupDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDailyCheckup(checkup: DailyCheckupEntity): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCheckups(checkups: List<DailyCheckupEntity>)
    
    @Update
    suspend fun updateCheckup(checkup: DailyCheckupEntity): Int
    
    @Query("DELETE FROM daily_checkups WHERE id = :checkupId")
    suspend fun deleteDailyCheckup(checkupId: String): Int
    
    @Query("SELECT * FROM daily_checkups WHERE id = :checkupId")
    suspend fun getCheckupById(checkupId: String): DailyCheckupEntity?
    
    @Query("SELECT * FROM daily_checkups WHERE date = :date")
    fun getDailyCheckupByDate(date: LocalDate): Flow<DailyCheckupEntity?>
    
    @Query("SELECT * FROM daily_checkups WHERE date = :date")
    suspend fun getDailyCheckupByDateSync(date: LocalDate): DailyCheckupEntity?
    
    @Query("SELECT * FROM daily_checkups WHERE date BETWEEN :startDate AND :endDate ORDER BY date")
    fun getDailyCheckupsByDateRange(startDate: LocalDate, endDate: LocalDate): Flow<List<DailyCheckupEntity>>
    
    @Query("SELECT * FROM daily_checkups WHERE date BETWEEN :startDate AND :endDate ORDER BY date")
    suspend fun getDailyCheckupsByDateRangeSync(startDate: LocalDate, endDate: LocalDate): List<DailyCheckupEntity>
    
    @Query("SELECT AVG(moodRating) FROM daily_checkups WHERE date BETWEEN :startDate AND :endDate")
    fun getAverageMoodForRange(startDate: LocalDate, endDate: LocalDate): Flow<Float>
    
    @Query("SELECT AVG(sleepQuality) FROM daily_checkups WHERE date BETWEEN :startDate AND :endDate")
    fun getAverageSleepQualityForRange(startDate: LocalDate, endDate: LocalDate): Flow<Float>
    
    @Query("SELECT AVG(stressLevel) FROM daily_checkups WHERE date BETWEEN :startDate AND :endDate")
    fun getAverageStressLevelForRange(startDate: LocalDate, endDate: LocalDate): Flow<Float>
    
    @Query("SELECT AVG(energyLevel) FROM daily_checkups WHERE date BETWEEN :startDate AND :endDate")
    fun getAverageEnergyLevelForRange(startDate: LocalDate, endDate: LocalDate): Flow<Float>
}
