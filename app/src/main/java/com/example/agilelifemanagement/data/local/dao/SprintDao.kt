package com.example.agilelifemanagement.data.local.dao

import androidx.room.*
import com.example.agilelifemanagement.data.local.entity.SprintEntity
import com.example.agilelifemanagement.data.local.entity.SprintReviewEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

/**
 * Room DAO for accessing and manipulating sprint data in the database.
 */
@Dao
interface SprintDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSprint(sprint: SprintEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSprints(sprints: List<SprintEntity>)
    
    @Update
    suspend fun updateSprint(sprint: SprintEntity): Int
    
    @Query("DELETE FROM sprints WHERE id = :sprintId")
    suspend fun deleteSprint(sprintId: String): Int
    
    @Query("SELECT * FROM sprints WHERE id = :sprintId")
    suspend fun getSprintById(sprintId: String): SprintEntity?
    
    @Query("SELECT * FROM sprints")
    fun getAllSprints(): Flow<List<SprintEntity>>
    
    @Query("SELECT * FROM sprints WHERE startDate <= :date AND endDate >= :date")
    fun getActiveSprintAtDate(date: LocalDate): Flow<SprintEntity?>
    
    @Query("SELECT * FROM sprints WHERE startDate BETWEEN :startDate AND :endDate OR endDate BETWEEN :startDate AND :endDate")
    fun getSprintsInRange(startDate: LocalDate, endDate: LocalDate): Flow<List<SprintEntity>>
    
    // Sprint Review operations
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSprintReview(sprintReview: SprintReviewEntity)
    
    @Update
    suspend fun updateSprintReview(sprintReview: SprintReviewEntity): Int
    
    @Query("SELECT * FROM sprint_reviews WHERE sprintId = :sprintId")
    fun getSprintReviewBySprintId(sprintId: String): Flow<SprintReviewEntity?>
    
    @Query("SELECT * FROM sprint_reviews")
    fun getAllSprintReviews(): Flow<List<SprintReviewEntity>>
}
