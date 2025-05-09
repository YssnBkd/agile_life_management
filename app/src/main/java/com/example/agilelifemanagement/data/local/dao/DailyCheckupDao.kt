package com.example.agilelifemanagement.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.agilelifemanagement.data.local.entity.DailyCheckupEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyCheckupDao {
    @Query("SELECT * FROM daily_checkups ORDER BY date DESC")
    fun getAllDailyCheckups(): Flow<List<DailyCheckupEntity>>

    @Query("SELECT * FROM daily_checkups WHERE id = :id")
    fun getDailyCheckupById(id: String): Flow<DailyCheckupEntity?>

    @Query("SELECT * FROM daily_checkups WHERE date = :dateEpochSeconds")
    fun getDailyCheckupByDate(dateEpochSeconds: Long): Flow<DailyCheckupEntity?>

    @Query("SELECT * FROM daily_checkups WHERE sprintId = :sprintId")
    fun getDailyCheckupsBySprintId(sprintId: String): Flow<List<DailyCheckupEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(checkup: DailyCheckupEntity)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(checkup: DailyCheckupEntity)

    @Query("SELECT * FROM daily_checkups WHERE id = :id")
    suspend fun getDailyCheckupByIdSync(id: String): DailyCheckupEntity?

    @Query("SELECT * FROM daily_checkups WHERE date = :dateEpochSeconds")
    suspend fun getDailyCheckupByDateSync(dateEpochSeconds: Long): DailyCheckupEntity?

    @Query("SELECT * FROM daily_checkups WHERE sprintId = :sprintId")
    suspend fun getDailyCheckupsBySprintIdSync(sprintId: String): List<DailyCheckupEntity>

    @Query("DELETE FROM daily_checkups WHERE id = :id")
    suspend fun deleteById(id: String)
}
