package com.example.agilelifemanagement.data.repository

import com.example.agilelifemanagement.data.local.source.WellnessLocalDataSource
import com.example.agilelifemanagement.data.mapper.WellnessMapper
import com.example.agilelifemanagement.data.remote.source.WellnessRemoteDataSource
import com.example.agilelifemanagement.di.IoDispatcher
import com.example.agilelifemanagement.domain.model.DailyCheckup
import com.example.agilelifemanagement.domain.repository.WellnessRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.time.LocalDate
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of [WellnessRepository] that coordinates between local and remote data sources.
 * This implementation follows the offline-first approach, providing immediate access to local data
 * while syncing with remote sources in the background.
 */
@Singleton
class WellnessRepositoryImpl @Inject constructor(
    private val localDataSource: WellnessLocalDataSource,
    private val remoteDataSource: WellnessRemoteDataSource,
    private val wellnessMapper: WellnessMapper,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : WellnessRepository {
    
    // Repository-scoped coroutine scope for background operations
    private val repositoryScope = CoroutineScope(SupervisorJob() + ioDispatcher)

    override fun getDailyCheckup(date: LocalDate): Flow<DailyCheckup?> {
        // Launch coroutine to sync with remote in background
        repositoryScope.launch {
            try {
                syncDailyCheckup(date)
            } catch (e: Exception) {
                Timber.e(e, "Error syncing daily checkup for date $date in background")
            }
        }
        
        // Return local data immediately
        return localDataSource.getDailyCheckup(date)
            .map { entity -> entity?.let { wellnessMapper.mapToDomain(it) } }
            .flowOn(ioDispatcher)
    }

    override fun getDailyCheckupsForRange(startDate: LocalDate, endDate: LocalDate): Flow<List<DailyCheckup>> {
        // Launch coroutine to sync with remote in background
        repositoryScope.launch {
            try {
                syncDailyCheckupsForRange(startDate, endDate)
            } catch (e: Exception) {
                Timber.e(e, "Error syncing daily checkups for range $startDate to $endDate in background")
            }
        }
        
        // Return local data immediately
        return localDataSource.getDailyCheckupsForRange(startDate, endDate)
            .map { entities -> entities.map { wellnessMapper.mapToDomain(it) } }
            .flowOn(ioDispatcher)
    }

    override suspend fun saveDailyCheckup(checkup: DailyCheckup): Result<DailyCheckup> = withContext(ioDispatcher) {
        try {
            // Ensure the checkup has an ID
            val checkupWithId = if (checkup.id.isBlank()) {
                checkup.copy(id = UUID.randomUUID().toString())
            } else {
                checkup
            }
            
            // Save to local database first for immediate UI feedback
            val checkupEntity = wellnessMapper.mapToEntity(checkupWithId)
            localDataSource.insertDailyCheckup(checkupEntity)
            
            // Then try to save to remote in background
            try {
                val remoteCheckup = remoteDataSource.saveDailyCheckup(checkupWithId)
                // If remote has a different ID, update the local record
                if (remoteCheckup.id != checkupWithId.id) {
                    val updatedEntity = wellnessMapper.mapToEntity(remoteCheckup)
                    localDataSource.insertDailyCheckup(updatedEntity)
                    return@withContext Result.success(remoteCheckup)
                }
            } catch (e: Exception) {
                Timber.e(e, "Failed to sync daily checkup to remote, will retry later")
                // Continue with the local save even if remote fails
            }
            
            return@withContext Result.success(checkupWithId)
        } catch (e: Exception) {
            Timber.e(e, "Error saving daily checkup")
            return@withContext Result.failure(e)
        }
    }

    override suspend fun deleteDailyCheckup(checkupId: String): Result<Boolean> = withContext(ioDispatcher) {
        try {
            // Delete from local database first
            val deleteResult = localDataSource.deleteDailyCheckup(checkupId)
            
            if (deleteResult <= 0) {
                return@withContext Result.failure(NoSuchElementException("Daily checkup not found with ID: $checkupId"))
            }
            
            // Then try to delete from remote in background
            try {
                remoteDataSource.deleteDailyCheckup(checkupId)
            } catch (e: Exception) {
                Timber.e(e, "Failed to delete daily checkup from remote, will retry later")
                // Continue even if remote delete fails
            }
            
            return@withContext Result.success(true)
        } catch (e: Exception) {
            Timber.e(e, "Error deleting daily checkup")
            return@withContext Result.failure(e)
        }
    }

    override fun getAverageMoodForRange(startDate: LocalDate, endDate: LocalDate): Flow<Float> {
        return localDataSource.getAverageMoodForRange(startDate, endDate)
            .flowOn(ioDispatcher)
    }

    override fun getAverageSleepQualityForRange(startDate: LocalDate, endDate: LocalDate): Flow<Float> {
        return localDataSource.getAverageSleepQualityForRange(startDate, endDate)
            .flowOn(ioDispatcher)
    }
    
    // Helper functions for syncing with remote
    
    private suspend fun syncDailyCheckup(date: LocalDate) {
        try {
            val remoteCheckup = remoteDataSource.getDailyCheckup(date)
            val localCheckup = localDataSource.getDailyCheckupSync(date)
            
            // If remote exists and local doesn't exist, update local
            // Note: We can't compare updatedAt timestamps since domain model doesn't have this field
            // This will need to be enhanced when timestamps are added to the domain model
            if (remoteCheckup != null && localCheckup == null) {
                val checkupEntity = wellnessMapper.mapToEntity(remoteCheckup)
                localDataSource.insertDailyCheckup(checkupEntity)
            }
            // If local exists but not remote, push to remote
            else if (localCheckup != null && remoteCheckup == null) {
                val domainCheckup = wellnessMapper.mapToDomain(localCheckup)
                remoteDataSource.saveDailyCheckup(domainCheckup)
            }
        } catch (e: Exception) {
            Timber.e(e, "Error during daily checkup sync for date $date")
            throw e
        }
    }
    
    private suspend fun syncDailyCheckupsForRange(startDate: LocalDate, endDate: LocalDate) {
        try {
            val remoteCheckups = remoteDataSource.getDailyCheckupsForRange(startDate, endDate)
            
            for (remoteCheckup in remoteCheckups) {
                val localCheckup = localDataSource.getDailyCheckupSync(remoteCheckup.date)
                
                // If local doesn't exist, update with remote data
                // Note: We can't compare updatedAt timestamps since domain model doesn't have this field
                if (localCheckup == null) {
                    val checkupEntity = wellnessMapper.mapToEntity(remoteCheckup)
                    localDataSource.insertDailyCheckup(checkupEntity)
                }
            }
            
            // Push local checkups that aren't in remote
            val localCheckups = localDataSource.getDailyCheckupsForRangeSync(startDate, endDate)
            for (localCheckup in localCheckups) {
                // Check if any remote checkup has the same ID as this local checkup
                val remoteHasLocalCheckup = remoteCheckups.any { remoteCheckup -> remoteCheckup.id == localCheckup.id }
                if (!remoteHasLocalCheckup) {
                    val domainCheckup = wellnessMapper.mapToDomain(localCheckup)
                    remoteDataSource.saveDailyCheckup(domainCheckup)
                }
            }
        } catch (e: Exception) {
            Timber.e(e, "Error during daily checkups range sync for $startDate to $endDate")
            throw e
        }
    }
}
