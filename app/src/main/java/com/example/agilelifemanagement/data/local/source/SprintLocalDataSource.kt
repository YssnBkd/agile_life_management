package com.example.agilelifemanagement.data.local.source

import com.example.agilelifemanagement.data.local.dao.SprintDao
import com.example.agilelifemanagement.data.local.entity.SprintEntity
import com.example.agilelifemanagement.data.local.entity.SprintReviewEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import javax.inject.Inject

/**
 * Local data source for sprints.
 * Uses Room DAO to perform database operations.
 */
class SprintLocalDataSource @Inject constructor(
    private val sprintDao: SprintDao
) {
    /**
     * Get all sprints as an observable flow.
     */
    fun observeSprints(): Flow<List<SprintEntity>> = sprintDao.getAllSprints()
    
    /**
     * Get the active sprint at a specific date.
     */
    fun observeActiveSprintAtDate(date: LocalDate): Flow<SprintEntity?> = 
        sprintDao.getActiveSprintAtDate(date)
    
    /**
     * Get sprints within a date range.
     */
    fun observeSprintsInRange(startDate: LocalDate, endDate: LocalDate): Flow<List<SprintEntity>> = 
        sprintDao.getSprintsInRange(startDate, endDate)
    
    /**
     * Get a specific sprint by ID.
     */
    suspend fun getSprintById(sprintId: String): SprintEntity? = 
        sprintDao.getSprintById(sprintId)
    
    /**
     * Insert a sprint.
     */
    suspend fun insertSprint(sprint: SprintEntity) {
        sprintDao.insertSprint(sprint)
    }
    
    /**
     * Insert multiple sprints.
     */
    suspend fun insertSprints(sprints: List<SprintEntity>) {
        sprintDao.insertSprints(sprints)
    }
    
    /**
     * Update a sprint.
     */
    suspend fun updateSprint(sprint: SprintEntity): Int =
        sprintDao.updateSprint(sprint)
    
    /**
     * Delete a sprint.
     */
    suspend fun deleteSprint(sprintId: String): Int =
        sprintDao.deleteSprint(sprintId)
    
    /**
     * Get a sprint review by sprint ID.
     */
    fun observeSprintReviewBySprintId(sprintId: String): Flow<SprintReviewEntity?> =
        sprintDao.getSprintReviewBySprintId(sprintId)
    
    /**
     * Get all sprint reviews.
     */
    fun observeSprintReviews(): Flow<List<SprintReviewEntity>> =
        sprintDao.getAllSprintReviews()
    
    /**
     * Insert a sprint review.
     */
    suspend fun insertSprintReview(sprintReview: SprintReviewEntity) {
        sprintDao.insertSprintReview(sprintReview)
    }
    
    /**
     * Update a sprint review.
     */
    suspend fun updateSprintReview(sprintReview: SprintReviewEntity): Int =
        sprintDao.updateSprintReview(sprintReview)
}
