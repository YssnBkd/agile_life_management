package com.example.agilelifemanagement.data.local.source

import com.example.agilelifemanagement.data.local.dao.DayActivityDao
import com.example.agilelifemanagement.data.local.dao.DayActivityTemplateDao
import com.example.agilelifemanagement.data.local.entity.DayActivityEntity
import com.example.agilelifemanagement.data.local.entity.DayActivityTemplateEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import javax.inject.Inject

/**
 * Local data source for day activities and templates.
 * Uses Room DAOs to perform database operations.
 */
class DayActivityLocalDataSource @Inject constructor(
    private val dayActivityDao: DayActivityDao,
    private val templateDao: DayActivityTemplateDao
) {
    // Activity Operations
    
    /**
     * Get activities for a specific date.
     */
    fun observeActivitiesByDate(date: LocalDate): Flow<List<DayActivityEntity>> = 
        dayActivityDao.getActivitiesByDate(date)
    
    /**
     * Get activities for a date range.
     */
    fun observeActivitiesForDateRange(startDate: LocalDate, endDate: LocalDate): Flow<List<DayActivityEntity>> = 
        dayActivityDao.getActivitiesForDateRange(startDate, endDate)
    
    /**
     * Get activities by category.
     */
    fun observeActivitiesByCategory(categoryId: String): Flow<List<DayActivityEntity>> = 
        dayActivityDao.getActivitiesByCategory(categoryId)
    
    /**
     * Get activities by completion status for a specific date.
     */
    fun observeActivitiesByCompletionStatus(completed: Boolean, date: LocalDate): Flow<List<DayActivityEntity>> = 
        dayActivityDao.getActivitiesByCompletionStatus(completed, date)
    
    /**
     * Get a specific activity by ID.
     */
    suspend fun getActivityById(activityId: String): DayActivityEntity? = 
        dayActivityDao.getActivityById(activityId)
    
    /**
     * Insert an activity.
     */
    suspend fun insertActivity(activity: DayActivityEntity) {
        dayActivityDao.insertActivity(activity)
    }
    
    /**
     * Insert multiple activities.
     */
    suspend fun insertActivities(activities: List<DayActivityEntity>) {
        dayActivityDao.insertActivities(activities)
    }
    
    /**
     * Update an activity.
     */
    suspend fun updateActivity(activity: DayActivityEntity): Int =
        dayActivityDao.updateActivity(activity)
    
    /**
     * Update an activity's completion status.
     */
    suspend fun updateActivityCompletion(activityId: String, completed: Boolean): Int =
        dayActivityDao.updateActivityCompletion(activityId, completed)
    
    /**
     * Delete an activity.
     */
    suspend fun deleteActivity(activityId: String): Int =
        dayActivityDao.deleteActivity(activityId)
    
    // Template Operations
    
    /**
     * Get all activity templates.
     */
    fun observeAllTemplates(): Flow<List<DayActivityTemplateEntity>> = 
        templateDao.getAllTemplates()
    
    /**
     * Get templates by category.
     */
    fun observeTemplatesByCategory(categoryId: String): Flow<List<DayActivityTemplateEntity>> = 
        templateDao.getTemplatesByCategory(categoryId)
    
    /**
     * Search templates by query.
     */
    fun searchTemplates(query: String): Flow<List<DayActivityTemplateEntity>> = 
        templateDao.searchTemplates("%$query%")
    
    /**
     * Get a specific template by ID.
     */
    suspend fun getTemplateById(templateId: String): DayActivityTemplateEntity? = 
        templateDao.getTemplateById(templateId)
    
    /**
     * Insert a template.
     */
    suspend fun insertTemplate(template: DayActivityTemplateEntity) {
        templateDao.insertTemplate(template)
    }
    
    /**
     * Insert multiple templates.
     */
    suspend fun insertTemplates(templates: List<DayActivityTemplateEntity>) {
        templateDao.insertTemplates(templates)
    }
    
    /**
     * Update a template.
     */
    suspend fun updateTemplate(template: DayActivityTemplateEntity): Int =
        templateDao.updateTemplate(template)
    
    /**
     * Delete a template.
     */
    suspend fun deleteTemplate(templateId: String): Int =
        templateDao.deleteTemplate(templateId)
}
