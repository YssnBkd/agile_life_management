package com.example.agilelifemanagement.data.repository

import com.example.agilelifemanagement.data.local.source.DayActivityLocalDataSource
import com.example.agilelifemanagement.data.mapper.DayActivityMapper
import com.example.agilelifemanagement.data.remote.source.DayActivityRemoteDataSource
import com.example.agilelifemanagement.di.IoDispatcher
import com.example.agilelifemanagement.domain.model.DayActivity
import com.example.agilelifemanagement.domain.repository.DayActivityRepository
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
 * Implementation of [DayActivityRepository] that coordinates between local and remote data sources.
 * This implementation follows the offline-first approach, providing immediate access to local data
 * while syncing with remote sources in the background.
 */
@Singleton
class DayActivityRepositoryImpl @Inject constructor(
    private val dayActivityLocalDataSource: DayActivityLocalDataSource,
    private val dayActivityRemoteDataSource: DayActivityRemoteDataSource,
    private val dayActivityMapper: DayActivityMapper,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : DayActivityRepository {

    override fun getActivitiesForDate(date: LocalDate): Flow<List<DayActivity>> {
        // Trigger background sync of activities for this date
        syncActivitiesForDate(date)
        
        // Return local data immediately for responsive UI
        return dayActivityLocalDataSource.observeActivitiesForDate(date)
            .map { activities -> activities.map { dayActivityMapper.mapToDomain(it) } }
            .flowOn(ioDispatcher)
    }
    
    /**
     * Synchronizes local activity data for a specific date with remote data source in the background.
     */
    private fun syncActivitiesForDate(date: LocalDate) {
        withContext(ioDispatcher) {
            launch {
                try {
                    val remoteActivities = dayActivityRemoteDataSource.getActivitiesForDate(date)
                    val entityActivities = remoteActivities.map { dayActivityMapper.mapToEntity(it) }
                    dayActivityLocalDataSource.insertActivities(entityActivities)
                    Timber.d("Successfully synced ${remoteActivities.size} activities for date $date with remote")
                } catch (e: Exception) {
                    Timber.e(e, "Error syncing activities for date $date with remote")
                    // Continue with local data - offline-first approach
                }
            }
        }
    }

    override fun getActivitiesInRange(startDate: LocalDate, endDate: LocalDate): Flow<List<DayActivity>> {
        // Try to sync activities in this date range from remote
        syncActivitiesInRange(startDate, endDate)
        
        // Return local data immediately for responsive UI
        return dayActivityLocalDataSource.observeActivitiesInRange(startDate, endDate)
            .map { activities -> activities.map { dayActivityMapper.mapToDomain(it) } }
            .flowOn(ioDispatcher)
    }
    
    private fun syncActivitiesInRange(startDate: LocalDate, endDate: LocalDate) {
        withContext(ioDispatcher) {
            launch {
                try {
                    val remoteActivities = dayActivityRemoteDataSource.getActivitiesInRange(startDate, endDate)
                    val entityActivities = remoteActivities.map { dayActivityMapper.mapToEntity(it) }
                    dayActivityLocalDataSource.insertActivities(entityActivities)
                    Timber.d("Successfully synced ${remoteActivities.size} activities in range $startDate to $endDate from remote")
                } catch (e: Exception) {
                    Timber.e(e, "Error syncing activities in range $startDate to $endDate from remote")
                    // Continue with local data - offline-first approach
                }
            }
        }
    }
    
    override suspend fun getActivityById(activityId: String): Result<DayActivity> = withContext(ioDispatcher) {
        try {
            // First try to get from local database
            var activityEntity = dayActivityLocalDataSource.getActivityById(activityId)
            
            // If not found locally or we need the latest data, try remote
            if (activityEntity == null) {
                try {
                    val remoteActivity = dayActivityRemoteDataSource.getActivityById(activityId)
                    if (remoteActivity != null) {
                        // Found on remote, save to local database
                        val entity = dayActivityMapper.mapToEntity(remoteActivity)
                        dayActivityLocalDataSource.insertActivity(entity)
                        activityEntity = entity
                        Timber.d("Successfully fetched activity from remote: $activityId")
                    }
                } catch (e: Exception) {
                    Timber.e(e, "Error fetching activity from remote: $activityId")
                    // Continue with local data (which might be null)
                }
            }
            
            activityEntity?.let {
                Result.success(dayActivityMapper.mapToDomain(it))
            } ?: Result.failure(NoSuchElementException("Activity not found with ID: $activityId"))
        } catch (e: Exception) {
            Timber.e(e, "Error getting activity: $activityId")
            Result.failure(e)
        }
    }

    override suspend fun createActivity(activity: DayActivity): Result<DayActivity> = withContext(ioDispatcher) {
        try {
            // First save to local database for immediate update to UI
            val activityEntity = dayActivityMapper.mapToEntity(activity)
            val insertedId = dayActivityLocalDataSource.insertActivity(activityEntity)
            val insertedActivity = activity.copy(id = insertedId)
            
            // Then try to synchronize with remote in the background
            launch {
                try {
                    val remoteActivity = dayActivityRemoteDataSource.createActivity(insertedActivity)
                    // Update local cache with remote response (might contain additional server-side data)
                    val updatedEntity = dayActivityMapper.mapToEntity(remoteActivity)
                    dayActivityLocalDataSource.updateActivity(updatedEntity)
                    Timber.d("Successfully synchronized activity creation with remote: $insertedId")
                } catch (e: Exception) {
                    Timber.e(e, "Failed to sync activity creation with remote: $insertedId")
                    // Continue with local data - offline-first approach
                    // May need to implement a sync queue for retry logic in a production app
                }
            }
            
            Result.success(insertedActivity)
        } catch (e: Exception) {
            Timber.e(e, "Error creating activity: ${activity.title}")
            Result.failure(e)
        }
    }

    override suspend fun updateActivity(activity: DayActivity): Result<DayActivity> = withContext(ioDispatcher) {
        try {
            // First update local database for immediate update to UI
            val activityEntity = dayActivityMapper.mapToEntity(activity)
            dayActivityLocalDataSource.updateActivity(activityEntity)
            
            // Then try to synchronize with remote in the background
            launch {
                try {
                    val remoteActivity = dayActivityRemoteDataSource.updateActivity(activity)
                    // Update local cache with remote response (might contain additional server-side data)
                    val updatedEntity = dayActivityMapper.mapToEntity(remoteActivity)
                    dayActivityLocalDataSource.updateActivity(updatedEntity)
                    Timber.d("Successfully synchronized activity update with remote: ${activity.id}")
                } catch (e: Exception) {
                    Timber.e(e, "Failed to sync activity update with remote: ${activity.id}")
                    // Continue with local data - offline-first approach
                    // May need to implement a sync queue for retry logic in a production app
                }
            }
            
            Result.success(activity)
        } catch (e: Exception) {
            Timber.e(e, "Error updating activity: ${activity.id}")
            Result.failure(e)
        }
    }

    override suspend fun deleteActivity(activityId: String): Result<Boolean> = withContext(ioDispatcher) {
        try {
            // First delete from local database for immediate update to UI
            dayActivityLocalDataSource.deleteActivity(activityId)
            
            // Then try to synchronize with remote in the background
            launch {
                try {
                    val remoteResult = dayActivityRemoteDataSource.deleteActivity(activityId)
                    if (remoteResult) {
                        Timber.d("Successfully synchronized activity deletion with remote: $activityId")
                    } else {
                        Timber.w("Remote deletion returned false for activity: $activityId")
                        // This might indicate that the activity doesn't exist remotely
                        // or couldn't be deleted due to constraints
                    }
                } catch (e: Exception) {
                    Timber.e(e, "Failed to sync activity deletion with remote: $activityId")
                    // Continue with local deletion - offline-first approach
                    // May need to implement a sync queue for retry logic in a production app
                }
            }
            
            Result.success(true)
        } catch (e: Exception) {
            Timber.e(e, "Error deleting activity: $activityId")
            Result.failure(e)
        }
    }

    override suspend fun toggleActivityCompletion(activityId: String, completed: Boolean): Result<DayActivity> = 
        withContext(ioDispatcher) {
            try {
                // First update local database for immediate update to UI
                val activityEntity = dayActivityLocalDataSource.getActivityById(activityId)
                    ?: return@withContext Result.failure(NoSuchElementException("Activity not found with ID: $activityId"))

                val updatedEntity = activityEntity.copy(isCompleted = completed)
                dayActivityLocalDataSource.updateActivity(updatedEntity)
                val updatedActivity = dayActivityMapper.mapToDomain(updatedEntity)
                
                // Then try to synchronize with remote in the background
                launch {
                    try {
                        val remoteActivity = dayActivityRemoteDataSource.toggleActivityCompletion(activityId, completed)
                        // Update local cache with remote response (might contain additional server-side data)
                        val remoteMappedEntity = dayActivityMapper.mapToEntity(remoteActivity)
                        dayActivityLocalDataSource.updateActivity(remoteMappedEntity)
                        Timber.d("Successfully synchronized activity completion toggle with remote: $activityId")
                    } catch (e: Exception) {
                        Timber.e(e, "Failed to sync activity completion toggle with remote: $activityId")
                        // Continue with local data - offline-first approach
                        // May need to implement a sync queue for retry logic in a production app
                    }
                }
                
                Result.success(updatedActivity)
            } catch (e: Exception) {
                Timber.e(e, "Error toggling activity completion: $activityId to $completed")
                Result.failure(e)
            }
        }
}
