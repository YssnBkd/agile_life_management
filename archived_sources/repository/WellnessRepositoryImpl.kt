package com.example.agilelifemanagement.data.repository

import com.example.agilelifemanagement.data.local.source.DailyCheckupLocalDataSource
import com.example.agilelifemanagement.data.mapper.WellnessMapper
import com.example.agilelifemanagement.data.remote.source.WellnessRemoteDataSource
import com.example.agilelifemanagement.di.IoDispatcher
import com.example.agilelifemanagement.domain.model.DailyCheckup
import com.example.agilelifemanagement.domain.repository.WellnessRepository
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
 * Implementation of [WellnessRepository] that coordinates between local and remote data sources.
 * This implementation follows the offline-first approach, providing immediate access to local data
 * while syncing with remote sources in the background.
 */
@Singleton
class WellnessRepositoryImpl @Inject constructor(
    private val dailyCheckupLocalDataSource: DailyCheckupLocalDataSource,
    private val wellnessRemoteDataSource: WellnessRemoteDataSource,
    private val wellnessMapper: WellnessMapper,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : WellnessRepository {

    override fun getDailyCheckup(date: LocalDate): Flow<DailyCheckup?> {
        // Try to sync with remote in background
        syncDailyCheckupForDate(date)
        
        return dailyCheckupLocalDataSource.observeDailyCheckup(date)
            .map { entity -> entity?.let { wellnessMapper.mapToDomain(it) } }
            .flowOn(ioDispatcher)
    }

    private fun syncDailyCheckupForDate(date: LocalDate) {
        withContext(ioDispatcher) {
            launch {
                try {
                    val remoteCheckup = wellnessRemoteDataSource.getDailyCheckup(date)
                    remoteCheckup?.let {
                        val entity = wellnessMapper.mapToEntity(it)
                        dailyCheckupLocalDataSource.insertDailyCheckup(entity)
                        Timber.d("Successfully synced daily checkup for date: $date")
                    }
                } catch (e: Exception) {
                    Timber.e(e, "Error syncing daily checkup for date: $date")
                    // Handle error but don't propagate - offline-first approach continues with local data
                }
            }
        }
    }

    override fun getDailyCheckupsForRange(startDate: LocalDate, endDate: LocalDate): Flow<List<DailyCheckup>> {
        // Try to sync with remote in background
        syncDailyCheckupsForRange(startDate, endDate)
        
        return dailyCheckupLocalDataSource.observeDailyCheckupsForRange(startDate, endDate)
            .map { entities -> entities.map { wellnessMapper.mapToDomain(it) } }
            .flowOn(ioDispatcher)
    }

    private fun syncDailyCheckupsForRange(startDate: LocalDate, endDate: LocalDate) {
        withContext(ioDispatcher) {
            launch {
                try {
                    val remoteCheckups = wellnessRemoteDataSource.getDailyCheckupsForRange(startDate, endDate)
                    val entities = remoteCheckups.map { wellnessMapper.mapToEntity(it) }
                    dailyCheckupLocalDataSource.insertDailyCheckups(entities)
                    Timber.d("Successfully synced ${remoteCheckups.size} daily checkups for range: $startDate to $endDate")
                } catch (e: Exception) {
                    Timber.e(e, "Error syncing daily checkups for range: $startDate to $endDate")
                    // Handle error but don't propagate - offline-first approach continues with local data
                }
            }
        }
    }

    override suspend fun saveDailyCheckup(checkup: DailyCheckup): Result<DailyCheckup> = withContext(ioDispatcher) {
        try {
            // Save to local database first for immediate feedback
            val checkupEntity = wellnessMapper.mapToEntity(checkup)
            dailyCheckupLocalDataSource.insertDailyCheckup(checkupEntity)
            
            // Then try to save to remote in background
            launch {
                try {
                    wellnessRemoteDataSource.saveDailyCheckup(checkup)
                    Timber.d("Successfully synced daily checkup to remote for date: ${checkup.date}")
                } catch (e: Exception) {
                    Timber.e(e, "Error syncing daily checkup to remote for date: ${checkup.date}")
                    // Checkup will be synced later when connectivity is restored
                    // Could add to a sync queue for retry mechanism
                }
            }
            
            Result.success(checkup)
        } catch (e: Exception) {
            Timber.e(e, "Error saving daily checkup locally for date: ${checkup.date}")
            Result.failure(e)
        }
    }

    override suspend fun deleteDailyCheckup(checkupId: String): Result<Boolean> = withContext(ioDispatcher) {
        try {
            // Parse date from ID (assuming ID format includes date)
            val date = LocalDate.parse(checkupId)
            
            // Delete from local database first
            dailyCheckupLocalDataSource.deleteDailyCheckup(date)
            
            // Then try to delete from remote in background
            launch {
                try {
                    wellnessRemoteDataSource.deleteDailyCheckup(date)
                    Timber.d("Successfully deleted daily checkup from remote for date: $date")
                } catch (e: Exception) {
                    Timber.e(e, "Error deleting daily checkup from remote for date: $date")
                    // Could add to a deletion sync queue for retry mechanism
                }
            }
            
            Result.success(true)
        } catch (e: Exception) {
            Timber.e(e, "Error deleting daily checkup locally: $checkupId")
            Result.failure(e)
        }
    }

    override fun getAverageMoodForRange(startDate: LocalDate, endDate: LocalDate): Flow<Float> {
        // Ensure we have the latest data first
        syncDailyCheckupsForRange(startDate, endDate)
        
        return dailyCheckupLocalDataSource.observeAverageMoodForRange(startDate, endDate)
            .flowOn(ioDispatcher)
    }

    override fun getAverageSleepQualityForRange(startDate: LocalDate, endDate: LocalDate): Flow<Float> {
        // Ensure we have the latest data first
        syncDailyCheckupsForRange(startDate, endDate)
        
        return dailyCheckupLocalDataSource.observeAverageSleepQualityForRange(startDate, endDate)
            .flowOn(ioDispatcher)
    }

    override fun getAverageEnergyForRange(startDate: LocalDate, endDate: LocalDate): Flow<Float> {
        // Ensure we have the latest data first
        syncDailyCheckupsForRange(startDate, endDate)
        
        return dailyCheckupLocalDataSource.observeAverageEnergyForRange(startDate, endDate)
            .flowOn(ioDispatcher)
    }
}
