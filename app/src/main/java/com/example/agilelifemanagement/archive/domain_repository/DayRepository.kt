package com.example.agilelifemanagement.domain.repository

import com.example.agilelifemanagement.domain.model.*
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

/**
 * Repository interface for day planning operations.
 * Handles day templates, schedules, and activities.
 */
interface DayRepository {
    // Day Templates
    fun getTemplates(): Flow<List<DayTemplate>>
    fun getTemplateById(id: String): Flow<DayTemplate?>
    suspend fun insertTemplate(template: DayTemplate): String
    suspend fun updateTemplate(template: DayTemplate)
    suspend fun deleteTemplate(id: String)
    
    // Day Schedules
    fun getSchedules(): Flow<List<DaySchedule>>
    fun getScheduleByDate(date: LocalDate): Flow<DaySchedule?>
    fun getScheduleById(id: String): Flow<DaySchedule?>
    suspend fun insertSchedule(schedule: DaySchedule): String
    suspend fun updateSchedule(schedule: DaySchedule)
    suspend fun deleteSchedule(id: String)
    
    // Day Activities
    fun getActivitiesByDate(date: LocalDate): Flow<List<DayActivity>>
    fun getActivityById(id: String): Flow<DayActivity?>
    suspend fun insertActivity(activity: DayActivity): String
    suspend fun updateActivity(activity: DayActivity)
    suspend fun deleteActivity(id: String)
    suspend fun toggleActivityCompletion(id: String, completed: Boolean)
    
    // Activity Categories
    fun getCategories(): Flow<List<ActivityCategory>>
    fun getCategoryById(id: String): Flow<ActivityCategory?>
    suspend fun insertCategory(category: ActivityCategory): String
    suspend fun updateCategory(category: ActivityCategory)
    suspend fun deleteCategory(id: String)
    
    // Template Application
    suspend fun applyTemplate(templateId: String, date: LocalDate): String
}
