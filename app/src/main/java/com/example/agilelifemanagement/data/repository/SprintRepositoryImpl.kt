package com.example.agilelifemanagement.data.repository

import com.example.agilelifemanagement.data.local.source.SprintLocalDataSource
import com.example.agilelifemanagement.data.mapper.SprintMapper
import com.example.agilelifemanagement.data.remote.source.SprintRemoteDataSource
import com.example.agilelifemanagement.di.IoDispatcher
import com.example.agilelifemanagement.domain.model.Sprint
import com.example.agilelifemanagement.domain.model.SprintReview
import com.example.agilelifemanagement.domain.repository.SprintRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of [SprintRepository] that coordinates between local and remote data sources.
 * This implementation follows the offline-first approach, providing immediate access to local data
 * while syncing with remote sources in the background.
 */
@Singleton
class SprintRepositoryImpl @Inject constructor(
    private val sprintLocalDataSource: SprintLocalDataSource,
    private val sprintRemoteDataSource: SprintRemoteDataSource,
    private val sprintMapper: SprintMapper,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : SprintRepository {

    override fun getAllSprints(): Flow<List<Sprint>> {
        // Trigger background sync of all sprints
        syncSprintsWithRemote()
        
        // Return local data immediately for responsive UI
        return sprintLocalDataSource.observeSprints()
            .map { sprintEntities -> sprintEntities.map { sprintMapper.mapToDomain(it) } }
            .flowOn(ioDispatcher)
    }
    
    /**
     * Synchronizes local sprint data with remote data source in the background.
     */
    private fun syncSprintsWithRemote() {
        withContext(ioDispatcher) {
            launch {
                try {
                    val remoteSprints = sprintRemoteDataSource.getAllSprints()
                    val entitySprints = remoteSprints.map { sprintMapper.mapToEntity(it) }
                    sprintLocalDataSource.insertSprints(entitySprints)
                    Timber.d("Successfully synced ${remoteSprints.size} sprints with remote")
                } catch (e: Exception) {
                    Timber.e(e, "Error syncing sprints with remote")
                    // Continue with local data - offline-first approach
                }
            }
        }
    }

    override fun getActiveSprintAtDate(date: LocalDate): Flow<Sprint?> {
        // Try to fetch the active sprint for this date from remote
        syncActiveSprintAtDate(date)
        
        // Return local data immediately for responsive UI
        return sprintLocalDataSource.observeActiveSprintAtDate(date)
            .map { sprintEntity -> sprintEntity?.let { sprintMapper.mapToDomain(it) } }
            .flowOn(ioDispatcher)
    }
    
    private fun syncActiveSprintAtDate(date: LocalDate) {
        withContext(ioDispatcher) {
            launch {
                try {
                    val remoteSprint = sprintRemoteDataSource.getActiveSprintAtDate(date)
                    if (remoteSprint != null) {
                        val entitySprint = sprintMapper.mapToEntity(remoteSprint)
                        sprintLocalDataSource.insertSprint(entitySprint)
                        Timber.d("Successfully synced active sprint for date $date from remote")
                    }
                } catch (e: Exception) {
                    Timber.e(e, "Error syncing active sprint for date $date from remote")
                    // Continue with local data - offline-first approach
                }
            }
        }
    }

    override fun getSprintsInRange(startDate: LocalDate, endDate: LocalDate): Flow<List<Sprint>> {
        // Try to sync sprints in this date range from remote
        syncSprintsInRange(startDate, endDate)
        
        // Return local data immediately for responsive UI
        return sprintLocalDataSource.observeSprintsInRange(startDate, endDate)
            .map { sprintEntities -> sprintEntities.map { sprintMapper.mapToDomain(it) } }
            .flowOn(ioDispatcher)
    }
    
    private fun syncSprintsInRange(startDate: LocalDate, endDate: LocalDate) {
        withContext(ioDispatcher) {
            launch {
                try {
                    val remoteSprints = sprintRemoteDataSource.getSprintsInRange(startDate, endDate)
                    val entitySprints = remoteSprints.map { sprintMapper.mapToEntity(it) }
                    sprintLocalDataSource.insertSprints(entitySprints)
                    Timber.d("Successfully synced ${remoteSprints.size} sprints in range $startDate to $endDate from remote")
                } catch (e: Exception) {
                    Timber.e(e, "Error syncing sprints in range $startDate to $endDate from remote")
                    // Continue with local data - offline-first approach
                }
            }
        }
    }

    override suspend fun getSprintById(sprintId: String): Result<Sprint> = withContext(ioDispatcher) {
        try {
            // First try to get from local database
            var sprintEntity = sprintLocalDataSource.getSprintById(sprintId)
            
            // If not found locally or we need the latest data, try remote
            if (sprintEntity == null) {
                try {
                    val remoteSprint = sprintRemoteDataSource.getSprintById(sprintId)
                    if (remoteSprint != null) {
                        // Found on remote, save to local database
                        val entity = sprintMapper.mapToEntity(remoteSprint)
                        sprintLocalDataSource.insertSprint(entity)
                        sprintEntity = entity
                    }
                } catch (e: Exception) {
                    Timber.e(e, "Error fetching sprint from remote: $sprintId")
                    // Continue with local data (which might be null)
                }
            }
            
            sprintEntity?.let {
                Result.success(sprintMapper.mapToDomain(it))
            } ?: Result.failure(NoSuchElementException("Sprint not found with ID: $sprintId"))
        } catch (e: Exception) {
            Timber.e(e, "Error getting sprint: $sprintId")
            Result.failure(e)
        }
    }

    override suspend fun createSprint(sprint: Sprint): Result<Sprint> = withContext(ioDispatcher) {
        try {
            // First save to local database for immediate update to UI
            val sprintEntity = sprintMapper.mapToEntity(sprint)
            val insertedId = sprintLocalDataSource.insertSprint(sprintEntity)
            val insertedSprint = sprint.copy(id = insertedId)
            
            // Then try to synchronize with remote in the background
            launch {
                try {
                    val remoteSprint = sprintRemoteDataSource.createSprint(insertedSprint)
                    // Update local cache with remote response (might contain additional server-side data)
                    val updatedEntity = sprintMapper.mapToEntity(remoteSprint)
                    sprintLocalDataSource.updateSprint(updatedEntity)
                    Timber.d("Successfully synchronized sprint creation with remote: $insertedId")
                } catch (e: Exception) {
                    Timber.e(e, "Failed to sync sprint creation with remote: $insertedId")
                    // Continue with local data - offline-first approach
                    // May need to implement a sync queue for retry logic in a production app
                }
            }
            
            Result.success(insertedSprint)
        } catch (e: Exception) {
            Timber.e(e, "Error creating sprint: ${sprint.name}")
            Result.failure(e)
        }
    }

    override suspend fun updateSprint(sprint: Sprint): Result<Sprint> = withContext(ioDispatcher) {
        try {
            // First update local database for immediate update to UI
            val sprintEntity = sprintMapper.mapToEntity(sprint)
            sprintLocalDataSource.updateSprint(sprintEntity)
            
            // Then try to synchronize with remote in the background
            launch {
                try {
                    val remoteSprint = sprintRemoteDataSource.updateSprint(sprint)
                    // Update local cache with remote response (might contain additional server-side data)
                    val updatedEntity = sprintMapper.mapToEntity(remoteSprint)
                    sprintLocalDataSource.updateSprint(updatedEntity)
                    Timber.d("Successfully synchronized sprint update with remote: ${sprint.id}")
                } catch (e: Exception) {
                    Timber.e(e, "Failed to sync sprint update with remote: ${sprint.id}")
                    // Continue with local data - offline-first approach
                    // May need to implement a sync queue for retry logic in a production app
                }
            }
            
            Result.success(sprint)
        } catch (e: Exception) {
            Timber.e(e, "Error updating sprint: ${sprint.id}")
            Result.failure(e)
        }
    }

    override suspend fun deleteSprint(sprintId: String): Result<Boolean> = withContext(ioDispatcher) {
        try {
            // First delete from local database for immediate update to UI
            sprintLocalDataSource.deleteSprint(sprintId)
            
            // Then try to synchronize with remote in the background
            launch {
                try {
                    val remoteResult = sprintRemoteDataSource.deleteSprint(sprintId)
                    if (remoteResult) {
                        Timber.d("Successfully synchronized sprint deletion with remote: $sprintId")
                    } else {
                        Timber.w("Remote deletion returned false for sprint: $sprintId")
                        // This might indicate that the sprint doesn't exist remotely
                        // or couldn't be deleted due to constraints
                    }
                } catch (e: Exception) {
                    Timber.e(e, "Failed to sync sprint deletion with remote: $sprintId")
                    // Continue with local deletion - offline-first approach
                    // May need to implement a sync queue for retry logic in a production app
                }
            }
            
            Result.success(true)
        } catch (e: Exception) {
            Timber.e(e, "Error deleting sprint: $sprintId")
            Result.failure(e)
        }
    }

    // Note: Sprint review functionality is stubbed for now
    // We'll implement it properly when we add sprint review features
    override suspend fun createSprintReview(sprintId: String, review: SprintReview): Result<SprintReview> {
        return Result.failure(NotImplementedError("Sprint review functionality not yet implemented"))
    }

    override suspend fun getSprintReview(sprintId: String): Result<SprintReview> {
        return Result.failure(NotImplementedError("Sprint review functionality not yet implemented"))
    }
}
