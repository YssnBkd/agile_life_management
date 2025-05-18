package com.example.agilelifemanagement.data.repository

import com.example.agilelifemanagement.di.IoDispatcher
import com.example.agilelifemanagement.domain.model.Sprint
import com.example.agilelifemanagement.domain.model.SprintReview
import com.example.agilelifemanagement.domain.repository.SprintRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.coroutineScope
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SprintRepositoryImpl @Inject constructor(
    private val localDataSource: com.example.agilelifemanagement.data.local.source.SprintLocalDataSource,
    private val remoteDataSource: com.example.agilelifemanagement.data.remote.source.SprintRemoteDataSource,
    private val sprintMapper: com.example.agilelifemanagement.data.mapper.SprintMapper,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : SprintRepository {

    override fun getAllSprints(): Flow<List<Sprint>> =
        localDataSource.observeSprints().map { entities ->
            entities.map { sprintMapper.mapToDomain(it) }
        }

    // Convenience methods not required by the interface
    fun getActiveSprintAtDate(date: java.time.LocalDate): Flow<Sprint?> =
        localDataSource.observeActiveSprintAtDate(date).map { it?.let { sprintMapper.mapToDomain(it) } }

    fun getSprintsInRange(startDate: java.time.LocalDate, endDate: java.time.LocalDate): Flow<List<Sprint>> =
        localDataSource.observeSprintsInRange(startDate, endDate).map { entities ->
            entities.map { sprintMapper.mapToDomain(it) }
        }

    override suspend fun getSprintById(sprintId: String): Result<Sprint> = withContext(ioDispatcher) {
        try {
            val entity = localDataSource.getSprintById(sprintId)
            if (entity != null) {
                Result.success(sprintMapper.mapToDomain(entity))
            } else {
                Result.failure(NoSuchElementException("Sprint not found with ID: $sprintId"))
            }
        } catch (e: Exception) {
            Timber.e(e, "Error getting sprint by ID")
            Result.failure(e)
        }
    }

    override suspend fun createSprint(sprint: Sprint): Result<Sprint> = withContext(ioDispatcher) {
        try {
            val entity = sprintMapper.mapToEntity(sprint)
            localDataSource.insertSprint(entity)
            // Fire-and-forget remote sync
            coroutineScope {
                launch {
                    try {
                        remoteDataSource.createSprint(sprint)
                    } catch (e: Exception) {
                        Timber.e(e, "Remote sync failed for createSprint")
                    }
                }
            }
            Result.success(sprint.copy(id = entity.id))
        } catch (e: Exception) {
            Timber.e(e, "Error creating sprint")
            Result.failure(e)
        }
    }

    override suspend fun updateSprint(sprint: Sprint): Result<Sprint> = withContext(ioDispatcher) {
        try {
            val entity = sprintMapper.mapToEntity(sprint)
            localDataSource.updateSprint(entity)
            // Fire-and-forget remote sync
            coroutineScope {
                launch {
                    try {
                        remoteDataSource.updateSprint(sprint)
                    } catch (e: Exception) {
                        Timber.e(e, "Remote sync failed for updateSprint")
                    }
                }
            }
            Result.success(sprint)
        } catch (e: Exception) {
            Timber.e(e, "Error updating sprint")
            Result.failure(e)
        }
    }

    override suspend fun deleteSprint(sprintId: String): Result<Boolean> = withContext(ioDispatcher) {
        try {
            localDataSource.deleteSprint(sprintId)
            // Fire-and-forget remote sync
            coroutineScope {
                launch {
                    try {
                        remoteDataSource.deleteSprint(sprintId)
                    } catch (e: Exception) {
                        Timber.e(e, "Remote sync failed for deleteSprint")
                    }
                }
            }
            Result.success(true)
        } catch (e: Exception) {
            Timber.e(e, "Error deleting sprint")
            Result.failure(e)
        }
    }

    // Optionally: Add a method to refresh local from remote
    suspend fun refreshSprintsFromRemote() = withContext(ioDispatcher) {
        try {
            val remoteSprints = remoteDataSource.getAllSprints()
            val entities = remoteSprints.map { sprintMapper.mapToEntity(it) }
            localDataSource.insertSprints(entities)
        } catch (e: Exception) {
            Timber.e(e, "Error refreshing sprints from remote")
        }
    }

    override suspend fun createSprintReview(sprintReview: SprintReview): Result<SprintReview> = withContext(ioDispatcher) {
        try {
            // Create entity from domain model
            val entity = com.example.agilelifemanagement.data.local.entity.SprintReviewEntity(
                id = sprintReview.id.ifBlank { java.util.UUID.randomUUID().toString() },
                sprintId = sprintReview.sprintId,
                completionRate = sprintReview.completionRate,
                lessonsLearned = sprintReview.lessonsLearned,
                date = sprintReview.date
            )
            
            // Insert to local database
            localDataSource.insertSprintReview(entity)
            
            // Fire-and-forget remote sync
            coroutineScope {
                launch {
                    try {
                        // Note: remoteDataSource expects sprintId as a separate parameter
                        remoteDataSource.createSprintReview(sprintReview.sprintId, sprintReview)
                    } catch (e: Exception) {
                        Timber.e(e, "Remote sync failed for createSprintReview")
                    }
                }
            }
            Result.success(sprintReview.copy(id = entity.id))
        } catch (e: Exception) {
            Timber.e(e, "Error creating sprint review")
            Result.failure(e)
        }
    }
    
    override fun getSprintReviewBySprintId(sprintId: String): Flow<SprintReview?> =
        localDataSource.observeSprintReviewBySprintId(sprintId).map { entity ->
            entity?.let { 
                SprintReview(
                    id = it.id,
                    sprintId = it.sprintId,
                    completionRate = it.completionRate,
                    lessonsLearned = it.lessonsLearned,
                    date = it.date
                )
            }
        }
}
