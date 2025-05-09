package com.example.agilelifemanagement.data.repository

import com.example.agilelifemanagement.data.local.dao.SprintReviewDao
import com.example.agilelifemanagement.data.local.entity.SprintReviewEntity
import com.example.agilelifemanagement.data.mappers.toSprintReview
import com.example.agilelifemanagement.data.remote.api.SprintReviewApiService
import com.example.agilelifemanagement.data.remote.dto.SprintReviewDto
import com.example.agilelifemanagement.data.remote.SyncManager // Added for DI and sync
import com.example.agilelifemanagement.domain.model.Result
import com.example.agilelifemanagement.domain.model.SprintReview
import com.example.agilelifemanagement.domain.repository.SprintReviewRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject
import com.example.agilelifemanagement.util.NetworkMonitor

/**
 * Implementation of [SprintReviewRepository] that follows the offline-first strategy.
 */
class SprintReviewRepositoryImpl @Inject constructor(
    private val sprintReviewDao: SprintReviewDao,
    private val sprintReviewApiService: SprintReviewApiService,

    private val syncManager: SyncManager,
    private val networkMonitor: NetworkMonitor
) : SprintReviewRepository {

    override fun getReviews(): Flow<List<SprintReview>> {
        return sprintReviewDao.getAllSprintReviews().map { entities ->
            entities.map { it.toSprintReview() }
        }
    }

    override fun getReviewById(id: String): Flow<SprintReview?> {
        return sprintReviewDao.getSprintReviewById(id).map { entity ->
            entity?.toSprintReview()
        }
    }

    override fun getReviewBySprintId(sprintId: String): Flow<SprintReview?> {
        return sprintReviewDao.getSprintReviewBySprintId(sprintId).map { entity ->
            entity?.toSprintReview()
        }
    }

    override suspend fun insertReview(review: SprintReview): String {
        val id = review.id.ifEmpty { UUID.randomUUID().toString() }
        val currentTimeMillis = System.currentTimeMillis() / 1000
        val userId = syncManager.getCurrentUserId() ?: error("User ID must not be null")
        val sprintReviewEntity = SprintReviewEntity(
            id = id,
            sprintId = review.sprintId,
            date = review.date.toEpochDay() * 86400, // Convert to seconds
            rating = review.rating,
            userId = userId,
            createdAt = currentTimeMillis,
            updatedAt = currentTimeMillis
        )
        sprintReviewDao.insert(sprintReviewEntity)
        syncManager.scheduleSync(id, "sprint_review", com.example.agilelifemanagement.data.local.entity.PendingOperation.CREATE)
        return id
    }

    override suspend fun updateReview(review: SprintReview) {
        val existingEntity = sprintReviewDao.getSprintReviewByIdSync(review.id)
        if (existingEntity != null) {
            val updatedEntity = existingEntity.copy(
                sprintId = review.sprintId,
                date = review.date.toEpochDay() * 86400, // Convert to seconds
                rating = review.rating,
                updatedAt = System.currentTimeMillis() / 1000
            )
            sprintReviewDao.insert(updatedEntity)
            syncManager.scheduleSync(review.id, "sprint_review", com.example.agilelifemanagement.data.local.entity.PendingOperation.UPDATE)
        }
    }

    override suspend fun deleteReview(id: String) {
        sprintReviewDao.deleteById(id)
        try {
            sprintReviewApiService.deleteSprintReview(id)
        } catch (e: Exception) {
            // Log error but don't throw - we've already deleted locally
        }
    }











    // Private helper methods
    // Synchronize a SprintReview entity with Supabase, with error handling
    private suspend fun syncSprintReview(entity: SprintReviewEntity): Result<Unit> {
        return try {
            val dto = SprintReviewDto.fromEntity(entity)
            sprintReviewApiService.upsertSprintReview(dto)
            syncManager.markSynced(entity.id, "sprint_review")
            Result.Success(Unit)
        } catch (e: Exception) {
            // Log error for debugging and analytics
            Result.Error("Failed to sync sprint review: ${e.message}", e)
        }
    }


}
