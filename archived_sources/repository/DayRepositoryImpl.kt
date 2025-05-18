package com.example.agilelifemanagement.data.repository

import com.example.agilelifemanagement.data.local.source.DayActivityLocalDataSource
import com.example.agilelifemanagement.data.local.source.DayScheduleLocalDataSource
import com.example.agilelifemanagement.data.local.source.DayTemplateLocalDataSource
import com.example.agilelifemanagement.data.mapper.DayActivityMapper
import com.example.agilelifemanagement.data.mapper.DayScheduleMapper
import com.example.agilelifemanagement.data.mapper.TemplateMapper
import com.example.agilelifemanagement.data.remote.source.DayActivityRemoteDataSource
import com.example.agilelifemanagement.di.IoDispatcher
import com.example.agilelifemanagement.domain.model.DayActivity
import com.example.agilelifemanagement.domain.model.DayActivityTemplate
import com.example.agilelifemanagement.domain.model.DaySchedule
import com.example.agilelifemanagement.domain.repository.DayRepository
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
 * Implementation of [DayRepository] that coordinates between local and remote data sources.
 * This implementation follows the offline-first approach, providing immediate access to local data
 * while syncing with remote sources in the background.
 */
@Singleton
class DayRepositoryImpl @Inject constructor(
    private val dayActivityLocalDataSource: DayActivityLocalDataSource,
    private val dayScheduleLocalDataSource: DayScheduleLocalDataSource,
    private val dayTemplateLocalDataSource: DayTemplateLocalDataSource,
    private val dayActivityRemoteDataSource: DayActivityRemoteDataSource,
    private val activityMapper: DayActivityMapper,
    private val scheduleMapper: DayScheduleMapper,
    private val templateMapper: TemplateMapper,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : DayRepository {

    override fun getActivitiesByDate(date: LocalDate): Flow<List<DayActivity>> {
        // Try to sync with remote in background
        syncActivitiesForDate(date)
        
        return dayActivityLocalDataSource.observeActivitiesByDate(date)
            .map { entities -> entities.map { activityMapper.mapToDomain(it) } }
            .flowOn(ioDispatcher)
    }
    
    private fun syncActivitiesForDate(date: LocalDate) {
        withContext(ioDispatcher) {
            launch {
                try {
                    val remoteActivities = dayActivityRemoteDataSource.getActivitiesByDate(date)
                    val entityActivities = remoteActivities.map { activityMapper.mapToEntity(it) }
                    dayActivityLocalDataSource.insertActivities(entityActivities)
                    Timber.d("Successfully synced ${remoteActivities.size} activities for date: $date")
                } catch (e: Exception) {
                    Timber.e(e, "Error syncing activities for date: $date")
                    // Handle error but don't propagate - offline-first approach continues with local data
                }
            }
        }
    }

    override fun getActivitiesForDateRange(startDate: LocalDate, endDate: LocalDate): Flow<List<DayActivity>> {
        // Try to sync with remote in background
        syncActivitiesForDateRange(startDate, endDate)
        
        return dayActivityLocalDataSource.observeActivitiesForDateRange(startDate, endDate)
            .map { entities -> entities.map { activityMapper.mapToDomain(it) } }
            .flowOn(ioDispatcher)
    }
    
    private fun syncActivitiesForDateRange(startDate: LocalDate, endDate: LocalDate) {
        withContext(ioDispatcher) {
            launch {
                try {
                    val remoteActivities = dayActivityRemoteDataSource.getActivitiesForDateRange(startDate, endDate)
                    val entityActivities = remoteActivities.map { activityMapper.mapToEntity(it) }
                    dayActivityLocalDataSource.insertActivities(entityActivities)
                    Timber.d("Successfully synced ${remoteActivities.size} activities for range: $startDate to $endDate")
                } catch (e: Exception) {
                    Timber.e(e, "Error syncing activities for range: $startDate to $endDate")
                    // Handle error but don't propagate - offline-first approach continues with local data
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
                        val entity = activityMapper.mapToEntity(remoteActivity)
                        dayActivityLocalDataSource.insertActivity(entity)
                        activityEntity = entity
                    }
                } catch (e: Exception) {
                    Timber.e(e, "Error fetching activity from remote: $activityId")
                    // Continue with local data (which might be null)
                }
            }
            
            activityEntity?.let {
                Result.success(activityMapper.mapToDomain(it))
            } ?: Result.failure(NoSuchElementException("Activity not found with ID: $activityId"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun addActivity(activity: DayActivity): Result<DayActivity> = withContext(ioDispatcher) {
        try {
            // First save to local database for immediate feedback
            val activityEntity = activityMapper.mapToEntity(activity)
            val insertedId = dayActivityLocalDataSource.insertActivity(activityEntity)
            val insertedActivity = activity.copy(id = insertedId.toString())
            
            // Then try to save to remote in background
            launch {
                try {
                    dayActivityRemoteDataSource.createActivity(insertedActivity)
                    Timber.d("Successfully synced new activity to remote: ${insertedActivity.id}")
                } catch (e: Exception) {
                    Timber.e(e, "Error syncing new activity to remote: ${insertedActivity.id}")
                    // Activity will be synced later when connectivity is restored
                    // Could add to a sync queue for retry mechanism
                }
            }
            
            Result.success(insertedActivity)
        } catch (e: Exception) {
            Timber.e(e, "Error creating activity locally")
            Result.failure(e)
        }
    }

    override suspend fun updateActivity(activity: DayActivity): Result<DayActivity> = withContext(ioDispatcher) {
        try {
            // Update local database first for immediate feedback
            val activityEntity = activityMapper.mapToEntity(activity)
            dayActivityLocalDataSource.updateActivity(activityEntity)
            
            // Then try to update remote in background
            launch {
                try {
                    dayActivityRemoteDataSource.updateActivity(activity)
                    Timber.d("Successfully synced updated activity to remote: ${activity.id}")
                } catch (e: Exception) {
                    Timber.e(e, "Error syncing updated activity to remote: ${activity.id}")
                    // Activity will be synced later when connectivity is restored
                    // Could add to a sync queue for retry mechanism
                }
            }
            
            Result.success(activity)
        } catch (e: Exception) {
            Timber.e(e, "Error updating activity locally: ${activity.id}")
            Result.failure(e)
        }
    }

    override suspend fun deleteActivity(activityId: String): Result<Boolean> = withContext(ioDispatcher) {
        try {
            // Delete from local database first
            dayActivityLocalDataSource.deleteActivity(activityId)
            
            // Then try to delete from remote in background
            launch {
                try {
                    dayActivityRemoteDataSource.deleteActivity(activityId)
                    Timber.d("Successfully deleted activity from remote: $activityId")
                } catch (e: Exception) {
                    Timber.e(e, "Error deleting activity from remote: $activityId")
                    // Could add to a deletion sync queue for retry mechanism
                }
            }
            
            Result.success(true)
        } catch (e: Exception) {
            Timber.e(e, "Error deleting activity locally: $activityId")
            Result.failure(e)
        }
    }

    override fun getScheduleByDate(date: LocalDate): Flow<DaySchedule?> {
        // Try to sync with remote in background
        syncScheduleForDate(date)
        
        return dayScheduleLocalDataSource.observeScheduleByDate(date)
            .map { entity -> entity?.let { scheduleMapper.mapToDomain(it) } }
            .flowOn(ioDispatcher)
    }
    
    private fun syncScheduleForDate(date: LocalDate) {
        withContext(ioDispatcher) {
            launch {
                try {
                    val remoteSchedule = dayActivityRemoteDataSource.getScheduleByDate(date)
                    remoteSchedule?.let {
                        val entity = scheduleMapper.mapToEntity(it)
                        dayScheduleLocalDataSource.insertSchedule(entity)
                        Timber.d("Successfully synced schedule for date: $date")
                    }
                } catch (e: Exception) {
                    Timber.e(e, "Error syncing schedule for date: $date")
                    // Handle error but don't propagate - offline-first approach continues with local data
                }
            }
        }
    }

    override suspend fun updateSchedule(schedule: DaySchedule): Result<DaySchedule> = withContext(ioDispatcher) {
        try {
            // Update local database first for immediate feedback
            val scheduleEntity = scheduleMapper.mapToEntity(schedule)
            dayScheduleLocalDataSource.updateSchedule(scheduleEntity)
            
            // Then try to update remote in background
            launch {
                try {
                    dayActivityRemoteDataSource.updateSchedule(schedule)
                    Timber.d("Successfully synced updated schedule to remote for date: ${schedule.date}")
                } catch (e: Exception) {
                    Timber.e(e, "Error syncing updated schedule to remote for date: ${schedule.date}")
                    // Schedule will be synced later when connectivity is restored
                    // Could add to a sync queue for retry mechanism
                }
            }
            
            Result.success(schedule)
        } catch (e: Exception) {
            Timber.e(e, "Error updating schedule locally for date: ${schedule.date}")
            Result.failure(e)
        }
    }

    override fun getAllTemplates(): Flow<List<DayActivityTemplate>> {
        return dayTemplateLocalDataSource.observeAllTemplates()
            .map { entities -> entities.map { templateMapper.mapToDomain(it) } }
            .flowOn(ioDispatcher)
    }

    override suspend fun getTemplateById(templateId: String): Result<DayActivityTemplate> = withContext(ioDispatcher) {
        try {
            val templateEntity = dayTemplateLocalDataSource.getTemplateById(templateId)
            
            templateEntity?.let {
                Result.success(templateMapper.mapToDomain(it))
            } ?: Result.failure(NoSuchElementException("Template not found with ID: $templateId"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createTemplate(template: DayActivityTemplate): Result<DayActivityTemplate> = withContext(ioDispatcher) {
        try {
            // Save template to local database
            val templateEntity = templateMapper.mapToEntity(template)
            val insertedId = dayTemplateLocalDataSource.insertTemplate(templateEntity)
            
            val insertedTemplate = template.copy(id = insertedId.toString())
            Result.success(insertedTemplate)
        } catch (e: Exception) {
            Timber.e(e, "Error creating template locally")
            Result.failure(e)
        }
    }

    override suspend fun updateTemplate(template: DayActivityTemplate): Result<DayActivityTemplate> = withContext(ioDispatcher) {
        try {
            // Update template in local database
            val templateEntity = templateMapper.mapToEntity(template)
            dayTemplateLocalDataSource.updateTemplate(templateEntity)
            
            Result.success(template)
        } catch (e: Exception) {
            Timber.e(e, "Error updating template locally: ${template.id}")
            Result.failure(e)
        }
    }

    override suspend fun deleteTemplate(templateId: String): Result<Boolean> = withContext(ioDispatcher) {
        try {
            // Delete template from local database
            dayTemplateLocalDataSource.deleteTemplate(templateId)
            
            Result.success(true)
        } catch (e: Exception) {
            Timber.e(e, "Error deleting template locally: $templateId")
            Result.failure(e)
        }
    }

    override suspend fun applyTemplate(templateId: String, date: LocalDate): Result<List<DayActivity>> = withContext(ioDispatcher) {
        try {
            // Get template
            val templateEntity = dayTemplateLocalDataSource.getTemplateById(templateId)
                ?: return@withContext Result.failure(NoSuchElementException("Template not found with ID: $templateId"))
            
            // Get template activities
            val templateActivities = dayTemplateLocalDataSource.getTemplateActivities(templateId)
            
            // Create day activities from template activities
            val domainTemplate = templateMapper.mapToDomain(templateEntity)
            val dayActivities = templateActivities.map { templateActivity ->
                val domainTemplateActivity = templateMapper.mapActivityToDomain(templateActivity)
                DayActivity(
                    id = "", // Will be assigned during insertion
                    title = domainTemplateActivity.title,
                    description = domainTemplateActivity.description,
                    date = date,
                    startTime = domainTemplateActivity.startTime,
                    endTime = domainTemplateActivity.endTime,
                    categoryId = domainTemplateActivity.categoryId,
                    isCompleted = false,
                    createdAt = System.currentTimeMillis(),
                    modifiedAt = System.currentTimeMillis()
                )
            }
            
            // Insert activities
            val insertedActivities = mutableListOf<DayActivity>()
            for (activity in dayActivities) {
                addActivity(activity).fold(
                    onSuccess = { insertedActivities.add(it) },
                    onFailure = { /* Log but continue with other activities */ }
                )
            }
            
            Result.success(insertedActivities)
        } catch (e: Exception) {
            Timber.e(e, "Error applying template to date: $date")
            Result.failure(e)
        }
    }
}
