package com.example.agilelifemanagement.data.repository

import com.example.agilelifemanagement.data.local.source.DayActivityLocalDataSource
import com.example.agilelifemanagement.data.local.source.DayScheduleLocalDataSource
import com.example.agilelifemanagement.data.mapper.DayActivityMapper
import com.example.agilelifemanagement.data.mapper.DayActivityTemplateMapper
import com.example.agilelifemanagement.data.mapper.DayScheduleMapper
import com.example.agilelifemanagement.data.remote.source.DayActivityRemoteDataSource
import com.example.agilelifemanagement.data.remote.source.DayScheduleRemoteDataSource
import com.example.agilelifemanagement.di.IoDispatcher
import com.example.agilelifemanagement.domain.model.DayActivity
import com.example.agilelifemanagement.domain.model.DayActivityTemplate
import com.example.agilelifemanagement.domain.model.DaySchedule
import com.example.agilelifemanagement.domain.repository.DayRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
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
 * Implementation of [DayRepository] that coordinates between local and remote data sources.
 * This implementation follows the offline-first approach, providing immediate access to local data
 * while syncing with remote sources in the background.
 */
@Singleton
class DayRepositoryImpl @Inject constructor(
    private val activityLocalDataSource: DayActivityLocalDataSource,
    private val activityRemoteDataSource: DayActivityRemoteDataSource,
    private val scheduleLocalDataSource: DayScheduleLocalDataSource,
    private val scheduleRemoteDataSource: DayScheduleRemoteDataSource,
    private val activityMapper: DayActivityMapper,
    private val templateMapper: DayActivityTemplateMapper,
    private val scheduleMapper: DayScheduleMapper,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : DayRepository {
    
    // Repository-scoped coroutine scope for background operations
    private val repositoryScope = CoroutineScope(SupervisorJob() + ioDispatcher)

    // Activity operations

    override fun getActivitiesByDate(date: LocalDate): Flow<List<DayActivity>> {
        // Launch coroutine to sync with remote in background
        repositoryScope.launch {
            try {
                syncActivitiesForDate(date)
            } catch (e: Exception) {
                Timber.e(e, "Error syncing activities for date $date in background")
            }
        }
        
        return activityLocalDataSource.observeActivitiesByDate(date)
            .map { entities -> entities.map { activityMapper.mapToDomain(it) } }
            .flowOn(ioDispatcher)
    }
    
    private suspend fun syncActivitiesForDate(date: LocalDate) {
        withContext(ioDispatcher) {
            try {
                val remoteActivities = activityRemoteDataSource.getActivitiesForDate(date)
                val entities = remoteActivities.map { activityMapper.mapToEntity(it) }
                activityLocalDataSource.insertActivities(entities)
                Timber.d("Successfully synced ${remoteActivities.size} activities for date $date")
            } catch (e: Exception) {
                Timber.e(e, "Error syncing activities for date $date")
                // Handle error but don't propagate - offline-first approach continues with local data
            }
        }
    }

    override fun getActivitiesForDateRange(startDate: LocalDate, endDate: LocalDate): Flow<List<DayActivity>> {
        // Launch coroutine to sync with remote in background
        repositoryScope.launch {
            try {
                syncActivitiesForDateRange(startDate, endDate)
            } catch (e: Exception) {
                Timber.e(e, "Error syncing activities for date range $startDate-$endDate in background")
            }
        }
        
        return activityLocalDataSource.observeActivitiesForDateRange(startDate, endDate)
            .map { entities -> entities.map { activityMapper.mapToDomain(it) } }
            .flowOn(ioDispatcher)
    }
    
    private suspend fun syncActivitiesForDateRange(startDate: LocalDate, endDate: LocalDate) {
        withContext(ioDispatcher) {
            try {
                val remoteActivities = activityRemoteDataSource.getActivitiesInRange(startDate, endDate)
                val entities = remoteActivities.map { activityMapper.mapToEntity(it) }
                activityLocalDataSource.insertActivities(entities)
                Timber.d("Successfully synced ${remoteActivities.size} activities for range $startDate to $endDate")
            } catch (e: Exception) {
                Timber.e(e, "Error syncing activities for range $startDate to $endDate")
                // Handle error but don't propagate
            }
        }
    }
    
    // Schedule operations
    
    override fun getScheduleByDate(date: LocalDate): Flow<DaySchedule?> {
        // Launch coroutine to sync with remote in background
        repositoryScope.launch {
            try {
                syncScheduleForDate(date)
            } catch (e: Exception) {
                Timber.e(e, "Error syncing schedule for date $date in background")
            }
        }
        
        return scheduleLocalDataSource.observeScheduleByDate(date)
            .map { entity -> entity?.let { scheduleMapper.mapToDomain(it) } }
            .flowOn(ioDispatcher)
    }
    
    private suspend fun syncScheduleForDate(date: LocalDate) {
        withContext(ioDispatcher) {
            try {
                val remoteSchedule = scheduleRemoteDataSource.getScheduleForDate(date)
                if (remoteSchedule != null) {
                    val entity = scheduleMapper.mapToEntity(remoteSchedule)
                    scheduleLocalDataSource.insertSchedule(entity)
                    Timber.d("Successfully synced schedule for date $date")
                }
            } catch (e: Exception) {
                Timber.e(e, "Error syncing schedule for date $date")
                // Handle error but don't propagate - offline-first approach continues with local data
            }
        }
    }
    
    override suspend fun updateSchedule(schedule: DaySchedule): Result<Unit> {
        return runCatching {
            withContext(ioDispatcher) {
                // Update local database first
                val entity = scheduleMapper.mapToEntity(schedule)
                scheduleLocalDataSource.updateSchedule(entity)
                
                // Then synchronize with remote in background
                // This is done in a separate coroutine to not block the caller
                repositoryScope.launch {
                    try {
                        scheduleRemoteDataSource.updateSchedule(schedule)
                        Timber.d("Successfully synchronized schedule update with remote for date ${schedule.date}")
                    } catch (e: Exception) {
                        Timber.e(e, "Error synchronizing schedule update with remote for date ${schedule.date}")
                        // Mark for later sync instead of propagating the error
                    }
                }
            }
        }
    }

    override suspend fun getActivityById(activityId: String): Result<DayActivity> = withContext(ioDispatcher) {
        try {
            val localActivity = activityLocalDataSource.getActivityById(activityId)
            if (localActivity != null) {
                return@withContext Result.success(activityMapper.mapToDomain(localActivity))
            } else {
                // If not found locally, try remote
                val remoteActivity = activityRemoteDataSource.getActivityById(activityId)
                if (remoteActivity != null) {
                    // Save to local if found remotely
                    activityLocalDataSource.insertActivity(activityMapper.mapToEntity(remoteActivity))
                    return@withContext Result.success(remoteActivity)
                } else {
                    return@withContext Result.failure(Exception("Activity not found with ID: $activityId"))
                }
            }
        } catch (e: Exception) {
            Timber.e(e, "Error getting activity by ID: $activityId")
            return@withContext Result.failure(e)
        }
    }

    override suspend fun addActivity(activity: DayActivity): Result<DayActivity> {
        return runCatching {
            withContext(ioDispatcher) {
                // Generate ID if not provided
                val activityToSave = if (activity.id.isNullOrBlank()) {
                    activity.copy(id = UUID.randomUUID().toString())
                } else {
                    activity
                }
                
                // Save to local database first
                val entity = activityMapper.mapToEntity(activityToSave)
                activityLocalDataSource.insertActivity(entity)
                
                // Then synchronize with remote in background
                repositoryScope.launch {
                    try {
                        activityRemoteDataSource.createActivity(activityToSave)
                        Timber.d("Successfully synchronized new activity with remote: ${activityToSave.id}")
                    } catch (e: Exception) {
                        Timber.e(e, "Error synchronizing new activity with remote: ${activityToSave.id}")
                        // Mark for later sync instead of propagating the error
                    }
                }
                
                return@withContext activityToSave
            }
        }
    }
    
    override suspend fun updateActivity(activity: DayActivity): Result<DayActivity> {
        return runCatching {
            withContext(ioDispatcher) {
                // Validate activity has an ID
                if (activity.id.isNullOrBlank()) {
                    throw IllegalArgumentException("Cannot update activity without an ID")
                }
                
                // Update local database first
                val entity = activityMapper.mapToEntity(activity)
                activityLocalDataSource.updateActivity(entity)
                
                // Then synchronize with remote in background
                repositoryScope.launch {
                    try {
                        activityRemoteDataSource.updateActivity(activity)
                        Timber.d("Successfully synchronized activity update with remote: ${activity.id}")
                    } catch (e: Exception) {
                        Timber.e(e, "Error synchronizing activity update with remote: ${activity.id}")
                        // Mark for later sync instead of propagating the error
                    }
                }
                
                return@withContext activity
            }
        }
    }
    
    override suspend fun deleteActivity(activityId: String): Result<Boolean> {
        return runCatching {
            withContext(ioDispatcher) {
                // Delete from local database first
                val deleted = activityLocalDataSource.deleteActivity(activityId) > 0
                
                // Then synchronize with remote in background
                repositoryScope.launch {
                    try {
                        activityRemoteDataSource.deleteActivity(activityId)
                        Timber.d("Successfully synchronized activity deletion with remote: $activityId")
                    } catch (e: Exception) {
                        Timber.e(e, "Error synchronizing activity deletion with remote: $activityId")
                        // Mark for later sync instead of propagating the error
                    }
                }
                
                return@withContext deleted
            }
        }
    }



    override suspend fun toggleActivityCompletion(activityId: String): Result<DayActivity> {
        return runCatching {
            withContext(ioDispatcher) {
                // Get the activity first
                val localActivity = activityLocalDataSource.getActivityById(activityId)
                    ?: throw IllegalArgumentException("Activity not found with ID: $activityId")
                
                // Toggle the completion status
                val domainActivity = activityMapper.mapToDomain(localActivity)
                val updatedActivity = domainActivity.copy(completed = !domainActivity.completed)
                
                // Update in local database
                val entity = activityMapper.mapToEntity(updatedActivity)
                activityLocalDataSource.updateActivity(entity)
                
                // Sync with remote in background
                repositoryScope.launch {
                    try {
                        activityRemoteDataSource.updateActivity(updatedActivity)
                        Timber.d("Successfully synchronized completion toggle with remote: $activityId")
                    } catch (e: Exception) {
                        Timber.e(e, "Error synchronizing completion toggle with remote: $activityId")
                        // Mark for later sync
                    }
                }
                
                // Return the updated activity
                updatedActivity
            }
        }
    }

    // Template operations

    // Template operations currently use DayActivityLocalDataSource as a temporary solution
    // until we implement a dedicated TemplateLocalDataSource
    
    override fun getAllActivityTemplates(): Flow<List<DayActivityTemplate>> {
        // Using activityLocalDataSource for now with a missing method
        // This will be implemented properly when rebuilding the data layer
        return emptyFlow()
    }

    override suspend fun getActivityTemplateById(templateId: String): Result<DayActivityTemplate> = withContext(ioDispatcher) {
        // Template functionality to be implemented when rebuilding the data layer
        Result.failure(UnsupportedOperationException("Template functionality not yet implemented in data layer rebuild"))
    }

    override suspend fun createActivityTemplate(template: DayActivityTemplate): Result<DayActivityTemplate> = withContext(ioDispatcher) {
        // Template functionality to be implemented when rebuilding the data layer
        Result.failure(UnsupportedOperationException("Template functionality not yet implemented in data layer rebuild"))
    }

    override suspend fun updateActivityTemplate(template: DayActivityTemplate): Result<DayActivityTemplate> = withContext(ioDispatcher) {
        // Template functionality to be implemented when rebuilding the data layer
        Result.failure(UnsupportedOperationException("Template functionality not yet implemented in data layer rebuild"))
    }

    override suspend fun deleteActivityTemplate(templateId: String): Result<Boolean> = withContext(ioDispatcher) {
        // Template functionality to be implemented when rebuilding the data layer
        Result.failure(UnsupportedOperationException("Template functionality not yet implemented in data layer rebuild"))
    }
}
