package com.example.agilelifemanagement.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.agilelifemanagement.data.local.entity.SprintReviewEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SprintReviewDao {
    @Query("SELECT * FROM sprint_reviews ORDER BY date DESC")
    fun getAllSprintReviews(): Flow<List<SprintReviewEntity>>

    @Query("SELECT * FROM sprint_reviews WHERE id = :id LIMIT 1")
    fun getSprintReviewById(id: String): Flow<SprintReviewEntity?>

    @Query("SELECT * FROM sprint_reviews WHERE sprintId = :sprintId LIMIT 1")
    fun getSprintReviewBySprintId(sprintId: String): Flow<SprintReviewEntity?>

    @Query("SELECT * FROM sprint_reviews WHERE id = :id LIMIT 1")
    suspend fun getSprintReviewByIdSync(id: String): SprintReviewEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(review: SprintReviewEntity)

    @Delete
    suspend fun delete(review: SprintReviewEntity)

    @Query("DELETE FROM sprint_reviews WHERE id = :id")
    suspend fun deleteById(id: String)
}
