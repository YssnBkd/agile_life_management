package com.example.agilelifemanagement.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.agilelifemanagement.data.local.entity.SprintEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

/**
 * Data Access Object for SprintEntity.
 * Handles database operations for sprints.
 */
@Dao
interface SprintDao {
    
    /**
     * Get all sprints.
     */
    @Query("SELECT * FROM sprints ORDER BY startDate DESC")
    fun getAllSprints(): Flow<List<SprintEntity>>
    
    /**
     * Get all sprints for a specific user.
     */
    @Query("SELECT * FROM sprints WHERE userId = :userId ORDER BY startDate DESC")
    fun getSprintsByUserId(userId: String): Flow<List<SprintEntity>>
    
    /**
     * Get a sprint by ID.
     */
    @Query("SELECT * FROM sprints WHERE id = :id")
    fun getSprintById(id: String): Flow<SprintEntity?>
    
    /**
     * Get active sprint by date.
     */
    @Query("SELECT * FROM sprints WHERE :date BETWEEN startDate AND endDate AND isActive = 1 LIMIT 1")
    fun getActiveSprintByDate(date: Long): Flow<SprintEntity?>
    
    /**
     * Get active sprints.
     */
    @Query("SELECT * FROM sprints WHERE isActive = 1 ORDER BY startDate ASC")
    fun getActiveSprints(): Flow<List<SprintEntity>>
    
    /**
     * Get completed sprints.
     */
    @Query("SELECT * FROM sprints WHERE isCompleted = 1 ORDER BY endDate DESC")
    fun getCompletedSprints(): Flow<List<SprintEntity>>
    
    /**
     * Get sprints that overlap with a date range.
     */
    @Query("SELECT * FROM sprints WHERE NOT (endDate < :startDate OR startDate > :endDate) ORDER BY startDate ASC")
    fun getSprintsInDateRange(startDate: Long, endDate: Long): Flow<List<SprintEntity>>
    
    /**
     * Insert a new sprint.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSprint(sprint: SprintEntity)
    
    /**
     * Insert multiple sprints.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSprints(sprints: List<SprintEntity>)
    
    /**
     * Update an existing sprint.
     */
    @Update
    suspend fun updateSprint(sprint: SprintEntity)
    
    /**
     * Delete a sprint.
     */
    @Delete
    suspend fun deleteSprint(sprint: SprintEntity)
    
    /**
     * Delete a sprint by ID.
     */
    @Query("DELETE FROM sprints WHERE id = :id")
    suspend fun deleteSprintById(id: String)
    
    /**
     * Delete all sprints.
     */
    @Query("DELETE FROM sprints")
    suspend fun deleteAllSprints()
    
    /**
     * Mark a sprint as completed.
     */
    @Query("UPDATE sprints SET isCompleted = 1, isActive = 0, updatedAt = :timestamp WHERE id = :id")
    suspend fun markSprintAsCompleted(id: String, timestamp: Long = System.currentTimeMillis())
    
    /**
     * Mark a sprint as active.
     */
    @Query("UPDATE sprints SET isActive = 1, updatedAt = :timestamp WHERE id = :id")
    suspend fun markSprintAsActive(id: String, timestamp: Long = System.currentTimeMillis())
    
    /**
     * Mark a sprint as inactive.
     */
    @Query("UPDATE sprints SET isActive = 0, updatedAt = :timestamp WHERE id = :id")
    suspend fun markSprintAsInactive(id: String, timestamp: Long = System.currentTimeMillis())
}
