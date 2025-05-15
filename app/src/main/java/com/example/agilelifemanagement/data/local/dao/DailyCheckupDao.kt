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
    suspend fun insertCheckup(checkup: DailyCheckupEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCheckups(checkups: List<DailyCheckupEntity>)
    
    @Update
    suspend fun updateCheckup(checkup: DailyCheckupEntity): Int
    
    @Query("DELETE FROM daily_checkups WHERE id = :checkupId")
    suspend fun deleteCheckup(checkupId: String): Int
    
    @Query("SELECT * FROM daily_checkups WHERE id = :checkupId")
    suspend fun getCheckupById(checkupId: String): DailyCheckupEntity?
    
    @Query("SELECT * FROM daily_checkups WHERE date = :date")
    fun getCheckupByDate(date: LocalDate): Flow<DailyCheckupEntity?>
    
    @Query("SELECT * FROM daily_checkups WHERE date BETWEEN :startDate AND :endDate ORDER BY date")
    fun getCheckupsForDateRange(startDate: LocalDate, endDate: LocalDate): Flow<List<DailyCheckupEntity>>
    
    @Query("SELECT AVG(moodRating) FROM daily_checkups WHERE date BETWEEN :startDate AND :endDate")
    fun getAverageMoodForDateRange(startDate: LocalDate, endDate: LocalDate): Flow<Float>
    
    @Query("SELECT AVG(sleepQuality) FROM daily_checkups WHERE date BETWEEN :startDate AND :endDate")
    fun getAverageSleepQualityForDateRange(startDate: LocalDate, endDate: LocalDate): Flow<Float>
    
    @Query("SELECT AVG(stressLevel) FROM daily_checkups WHERE date BETWEEN :startDate AND :endDate")
    fun getAverageStressLevelForDateRange(startDate: LocalDate, endDate: LocalDate): Flow<Float>
    
    @Query("SELECT AVG(energyLevel) FROM daily_checkups WHERE date BETWEEN :startDate AND :endDate")
    fun getAverageEnergyLevelForDateRange(startDate: LocalDate, endDate: LocalDate): Flow<Float>
}
