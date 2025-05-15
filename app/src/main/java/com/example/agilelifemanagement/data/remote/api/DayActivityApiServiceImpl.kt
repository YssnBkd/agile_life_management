package com.example.agilelifemanagement.data.remote.api

import com.example.agilelifemanagement.data.remote.model.DayActivityDto
import io.ktor.client.HttpClient
import timber.log.Timber
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of [DayActivityApiService] that serves as a stub for future API integration.
 * This implementation provides placeholder functionality that will be replaced with
 * actual API calls in future iterations.
 */
@Singleton
class DayActivityApiServiceImpl @Inject constructor(
    private val httpClient: HttpClient
) : DayActivityApiService {
    
    override suspend fun getActivitiesForDate(date: LocalDate): List<DayActivityDto> {
        Timber.d("API: getActivitiesForDate called (stub) for date: $date")
        return emptyList() // Stub implementation
    }
    
    override suspend fun getActivitiesInRange(startDate: LocalDate, endDate: LocalDate): List<DayActivityDto> {
        Timber.d("API: getActivitiesInRange called (stub) for range: $startDate to $endDate")
        return emptyList() // Stub implementation
    }
    
    override suspend fun getActivityById(activityId: String): DayActivityDto? {
        Timber.d("API: getActivityById called (stub) for activity: $activityId")
        return null // Stub implementation
    }
    
    override suspend fun createActivity(activity: DayActivityDto): DayActivityDto {
        Timber.d("API: createActivity called (stub) for activity: ${activity.title}")
        return activity.copy(id = "generated-id-${System.currentTimeMillis()}") // Stub implementation
    }
    
    override suspend fun updateActivity(activity: DayActivityDto): DayActivityDto {
        Timber.d("API: updateActivity called (stub) for activity: ${activity.id}")
        return activity // Stub implementation
    }
    
    override suspend fun deleteActivity(activityId: String): Boolean {
        Timber.d("API: deleteActivity called (stub) for activity: $activityId")
        return true // Stub implementation
    }
    
    override suspend fun toggleActivityCompletion(activityId: String, completed: Boolean): DayActivityDto {
        Timber.d("API: toggleActivityCompletion called (stub) for activity: $activityId to $completed")
        throw NotImplementedError("API toggleActivityCompletion not implemented yet") // Demonstrate error handling
    }
}
