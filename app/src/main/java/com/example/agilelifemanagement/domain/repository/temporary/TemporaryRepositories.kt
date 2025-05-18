package com.example.agilelifemanagement.domain.repository.temporary

import com.example.agilelifemanagement.domain.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Temporary repository interfaces and implementations
 * to be used while the data layer is being rebuilt (after May 15, 2025 architectural change).
 * 
 * These will be gradually replaced as the real repository implementations are built.
 */

// Template repository - temporary interface
interface TempTemplateRepository {
    fun getAllTemplates(): Flow<List<DayTemplate>>
    fun getTemplateById(id: String): Flow<DayTemplate?>
    suspend fun createTemplate(template: DayTemplate): Result<DayTemplate>
    suspend fun updateTemplate(template: DayTemplate): Result<DayTemplate>
    suspend fun deleteTemplate(id: String): Result<Boolean>
}

// Day activity repository - temporary interface
interface TempDayRepository {
    fun getActivitiesByDate(date: LocalDate): Flow<List<DayActivity>>
    suspend fun addActivity(activity: DayActivity): Result<DayActivity>
    suspend fun updateActivity(activity: DayActivity): Result<DayActivity>
    suspend fun deleteActivity(id: String): Result<Boolean>
    suspend fun toggleActivityCompletion(id: String): Result<DayActivity>
}

// Notification repository - temporary interface
interface TempNotificationRepository {
    fun getNotifications(): Flow<List<Notification>>
    suspend fun markAsRead(id: String): Result<Boolean>
}

// Category repository - temporary interface
interface TempCategoryRepository {
    fun getAllCategories(): Flow<List<GoalCategory>>
}

// Wellness repository - temporary interface
interface TempWellnessRepository {
    fun getDailyCheckup(date: LocalDate): Flow<DailyCheckup?>
    fun getWellnessAnalytics(timeFrame: Int): Flow<WellnessAnalytics?>
    suspend fun saveDailyCheckup(checkup: DailyCheckup): Result<DailyCheckup>
}

// Temporary implementations
@Singleton
class TempTemplateRepositoryImpl @Inject constructor() : TempTemplateRepository {
    override fun getAllTemplates(): Flow<List<DayTemplate>> = flow { emit(emptyList()) }
    override fun getTemplateById(id: String): Flow<DayTemplate?> = flow { emit(null) }
    override suspend fun createTemplate(template: DayTemplate): Result<DayTemplate> = Result.success(template)
    override suspend fun updateTemplate(template: DayTemplate): Result<DayTemplate> = Result.success(template)
    override suspend fun deleteTemplate(id: String): Result<Boolean> = Result.success(true)
}

@Singleton
class TempDayRepositoryImpl @Inject constructor() : TempDayRepository {
    override fun getActivitiesByDate(date: LocalDate): Flow<List<DayActivity>> = flow { emit(emptyList()) }
    override suspend fun addActivity(activity: DayActivity): Result<DayActivity> = Result.success(activity)
    override suspend fun updateActivity(activity: DayActivity): Result<DayActivity> = Result.success(activity)
    override suspend fun deleteActivity(id: String): Result<Boolean> = Result.success(true)
    override suspend fun toggleActivityCompletion(id: String): Result<DayActivity> = 
        Result.success(DayActivity(
            id = id, 
            title = "Placeholder", 
            description = "Placeholder description",
            date = LocalDate.now(),
            scheduledTime = LocalTime.now(),
            duration = 30, // 30 minutes
            completed = true,
            categoryId = "default-category"
        ))
}

@Singleton
class TempNotificationRepositoryImpl @Inject constructor() : TempNotificationRepository {
    override fun getNotifications(): Flow<List<Notification>> = flow { emit(emptyList()) }
    override suspend fun markAsRead(id: String): Result<Boolean> = Result.success(true)
}

@Singleton
class TempCategoryRepositoryImpl @Inject constructor() : TempCategoryRepository {
    override fun getAllCategories(): Flow<List<GoalCategory>> = flow { emit(emptyList()) }
}

@Singleton
class TempWellnessRepositoryImpl @Inject constructor() : TempWellnessRepository {
    override fun getDailyCheckup(date: LocalDate): Flow<DailyCheckup?> = flow { emit(null) }
    
    override fun getWellnessAnalytics(timeFrame: Int): Flow<WellnessAnalytics?> = flow {
        // Create start and end dates based on timeFrame (days)
        val endDate = LocalDate.now()
        val startDate = endDate.minusDays(timeFrame.toLong())
        emit(WellnessAnalytics(
            averageMood = 0.0f,
            averageSleepQuality = 0.0f, 
            startDate = startDate,
            endDate = endDate
        ))
    }
    
    override suspend fun saveDailyCheckup(checkup: DailyCheckup): Result<DailyCheckup> = Result.success(checkup)
}
