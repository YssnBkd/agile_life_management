package com.example.agilelifemanagement.data.repository

import com.example.agilelifemanagement.data.local.entity.DayActivityTemplateEntity
import com.example.agilelifemanagement.data.local.source.DayActivityLocalDataSource
import com.example.agilelifemanagement.data.mapper.DayActivityMapper
import com.example.agilelifemanagement.data.remote.source.DayActivityRemoteDataSource
import com.example.agilelifemanagement.di.IoDispatcher
import com.example.agilelifemanagement.domain.model.DayActivity
import com.example.agilelifemanagement.domain.model.DayActivityTemplate
import com.example.agilelifemanagement.domain.repository.DayRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
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
class DayActivityRepositoryImpl @Inject constructor(
    private val dayActivityLocalDataSource: DayActivityLocalDataSource,
    private val dayActivityRemoteDataSource: DayActivityRemoteDataSource,
    private val dayActivityMapper: DayActivityMapper,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : DayRepository {

    override fun getActivitiesByDate(date: LocalDate): Flow<List<DayActivity>> {
        // Trigger background sync of activities for this date
        syncActivitiesForDate(date)
        
        // Return local data immediately for responsive UI
        return dayActivityLocalDataSource.observeActivitiesByDate(date)
            .map { activities -> activities.map { dayActivityMapper.mapToDomain(it) } }
            .flowOn(ioDispatcher)
    }
    
    override fun getActivitiesForDateRange(startDate: LocalDate, endDate: LocalDate): Flow<List<DayActivity>> {
        // Trigger background sync for the date range
        syncActivitiesForDateRange(startDate, endDate)
        
        // Return local data immediately for responsive UI
        return dayActivityLocalDataSource.observeActivitiesForDateRange(startDate, endDate)
            .map { activities -> activities.map { dayActivityMapper.mapToDomain(it) } }
            .flowOn(ioDispatcher)
    }
    
    /**
     * Synchronizes local activity data for a date range with remote data source in the background.
     */
    private fun syncActivitiesForDateRange(startDate: LocalDate, endDate: LocalDate) {
        // Reuse existing syncActivitiesInRange function
        syncActivitiesInRange(startDate, endDate)
    }
    
    override suspend fun getActivityById(activityId: String): kotlin.Result<DayActivity> {
        return try {
            val entity = dayActivityLocalDataSource.getActivityById(activityId)
            if (entity != null) {
                kotlin.Result.success(dayActivityMapper.mapToDomain(entity))
            } else {
                kotlin.Result.failure(NoSuchElementException("Activity not found with id: $activityId"))
            }
        } catch (e: Exception) {
            Timber.e(e, "Error getting activity by id: $activityId")
            Result.failure(e)
        }
    }
    
    override suspend fun addActivity(activity: DayActivity): kotlin.Result<DayActivity> {
        return try {
            // First save to local database for immediate update to UI
            val activityEntity = dayActivityMapper.mapToEntity(activity)
            dayActivityLocalDataSource.insertActivity(activityEntity)
            
            // Then sync to remote (fire and forget)
            CoroutineScope(ioDispatcher).launch {
                try {
                    dayActivityRemoteDataSource.createActivity(activity)
                    Timber.d("Successfully synced new activity to remote: ${activity.id}")
                } catch (e: Exception) {
                    Timber.e(e, "Error syncing new activity to remote: ${activity.id}")
                    // Queue for later retry
                }
            }
            
            // Return success with the original activity
            kotlin.Result.success(activity)
        } catch (e: Exception) {
            Timber.e(e, "Error creating activity")
            Result.failure(e)
        }
    }
    
    override suspend fun updateActivity(activity: DayActivity): kotlin.Result<DayActivity> {
        return try {
            // Update local database first for immediate UI update
            val activityEntity = dayActivityMapper.mapToEntity(activity)
            val updated = dayActivityLocalDataSource.updateActivity(activityEntity) > 0
            
            if (!updated) {
                return kotlin.Result.failure(NoSuchElementException("Activity not found or not updated: ${activity.id}"))
            }
            
            // Fetch the updated entity from the database to ensure we have the latest
            val updatedEntity = dayActivityLocalDataSource.getActivityById(activity.id)
            if (updatedEntity == null) {
                return kotlin.Result.failure(NoSuchElementException("Activity not found after update: ${activity.id}"))
            }
            
            val mappedDomain = dayActivityMapper.mapToDomain(updatedEntity)
            
            // Then try to sync with remote (don't block UI/return on this)
            CoroutineScope(ioDispatcher).launch {
                // Send the domain model directly to the remote data source
                dayActivityRemoteDataSource.updateActivity(mappedDomain)
            }
            
            kotlin.Result.success(mappedDomain)
        } catch (e: Exception) {
            Timber.e(e, "Error updating activity: ${activity.id}")
            Result.failure(e)
        }
    }
    
    override suspend fun deleteActivity(activityId: String): kotlin.Result<Boolean> {
        return try {
            // Delete locally
            val deleted = dayActivityLocalDataSource.deleteActivity(activityId) > 0
            
            // Delete from remote (fire and forget)
            CoroutineScope(ioDispatcher).launch {
                try {
                    dayActivityRemoteDataSource.deleteActivity(activityId)
                    Timber.d("Activity deleted from remote: $activityId")
                } catch (e: Exception) {
                    Timber.e(e, "Error deleting activity from remote: $activityId")
                }
            }
            
            // Return success
            kotlin.Result.success(deleted)
        } catch (e: Exception) {
            Timber.e(e, "Error deleting activity: $activityId")
            Result.failure(e)
        }
    }
    
    override suspend fun toggleActivityCompletion(activityId: String): kotlin.Result<DayActivity> {
        return try {
            // Get current activity first
            val activityResult = getActivityById(activityId)
            val activity = activityResult.getOrNull() 
                ?: return kotlin.Result.failure(NoSuchElementException("Activity not found: $activityId"))
            
            // Toggle completion status
            val updatedActivity = activity.copy(completed = !activity.completed)
            
            // Update using existing update method
            updateActivity(updatedActivity)
        } catch (e: Exception) {
            Timber.e(e, "Error toggling activity completion: $activityId")
            Result.failure(e)
        }
    }
    
    override fun getAllActivityTemplates(): Flow<List<DayActivityTemplate>> {
        return dayActivityLocalDataSource.observeAllTemplates()
            .map { templates -> templates.map { mapTemplateEntityToDomain(it) } }
            .flowOn(ioDispatcher)
    }
    
    // Helper function to map template entity to domain model
    private fun mapTemplateEntityToDomain(entity: DayActivityTemplateEntity): DayActivityTemplate {
        // Simple mapping based on available fields
        return DayActivityTemplate(
            id = entity.id,
            title = entity.title,
            description = entity.description,
            duration = entity.defaultDuration,
            categoryId = entity.categoryId ?: ""
        )
    }
    
    override suspend fun getActivityTemplateById(templateId: String): kotlin.Result<DayActivityTemplate> {
        return try {
            val templateEntity = dayActivityLocalDataSource.getTemplateById(templateId)
            if (templateEntity != null) {
                kotlin.Result.success(mapTemplateEntityToDomain(templateEntity))
            } else {
                kotlin.Result.failure(NoSuchElementException("Template not found with id: $templateId"))
            }
        } catch (e: Exception) {
            Timber.e(e, "Error getting template by id: $templateId")
            Result.failure(e)
        }
    }
    
    override suspend fun createActivityTemplate(template: DayActivityTemplate): kotlin.Result<DayActivityTemplate> {
        return try {
            val templateEntity = mapTemplateToEntity(template)
            dayActivityLocalDataSource.insertTemplate(templateEntity)
            kotlin.Result.success(template)
        } catch (e: Exception) {
            Timber.e(e, "Error creating template: ${template.id}")
            Result.failure(e)
        }
    }
    
    override suspend fun updateActivityTemplate(template: DayActivityTemplate): kotlin.Result<DayActivityTemplate> {
        return try {
            val templateEntity = mapTemplateToEntity(template)
            val updated = dayActivityLocalDataSource.updateTemplate(templateEntity) > 0
            
            if (!updated) {
                return kotlin.Result.failure(NoSuchElementException("Template not found or not updated: ${template.id}"))
            }
            
            // Return the updated template
            kotlin.Result.success(template)
        } catch (e: Exception) {
            Timber.e(e, "Error updating template: ${template.id}")
            Result.failure(e)
        }
    }
    
    override suspend fun deleteActivityTemplate(templateId: String): kotlin.Result<Boolean> {
        return try {
            val deleted = dayActivityLocalDataSource.deleteTemplate(templateId) > 0
            kotlin.Result.success(deleted)
        } catch (e: Exception) {
            Timber.e(e, "Error deleting template: $templateId")
            Result.failure(e)
        }
    }
    
    // Helper function to map domain model to entity
    private fun mapTemplateToEntity(template: DayActivityTemplate): DayActivityTemplateEntity {
        return DayActivityTemplateEntity(
            id = template.id,
            title = template.title,
            description = template.description,
            defaultDuration = template.defaultDuration,
            categoryId = template.categoryId
        )
    }
    
    /**
     * Synchronizes local activity data for a specific date with remote data source in the background.
     */
    private fun syncActivitiesForDate(date: LocalDate) {
        CoroutineScope(ioDispatcher).launch {
            try {
                val remoteActivities = dayActivityRemoteDataSource.getActivitiesByDate(date)
                val entityActivities = remoteActivities.map { dayActivityMapper.mapToEntity(it) }
                dayActivityLocalDataSource.insertActivities(entityActivities)
                Timber.d("Synced ${entityActivities.size} activities for $date")
            } catch (e: Exception) {
                Timber.e(e, "Error syncing activities for date $date")
            }
        }
    }
    // This method was causing conflicting overload issues with getActivitiesForDateRange
    // Implementation moved to getActivitiesForDateRange method
    
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

    // Implementation removed to avoid conflicting with the deleteActivity method in lines 139-152

    // Implementation removed to avoid conflicting with the toggleActivityCompletion method in lines 162-184
}
