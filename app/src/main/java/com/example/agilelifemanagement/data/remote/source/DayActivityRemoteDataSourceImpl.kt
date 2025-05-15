package com.example.agilelifemanagement.data.remote.source

import com.example.agilelifemanagement.domain.model.DayActivity
import timber.log.Timber
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of [DayActivityRemoteDataSource] that serves as a stub for future remote integration.
 * This implementation provides placeholder functionality that will be replaced with
 * actual remote API calls in future iterations.
 */
@Singleton
class DayActivityRemoteDataSourceImpl @Inject constructor() : DayActivityRemoteDataSource {
    
    override suspend fun getActivitiesForDate(date: LocalDate): List<DayActivity> {
        Timber.d("Remote: getActivitiesForDate called (stub) for date: $date")
        return emptyList() // Stub implementation
    }
    
    override suspend fun getActivitiesInRange(startDate: LocalDate, endDate: LocalDate): List<DayActivity> {
        Timber.d("Remote: getActivitiesInRange called (stub) for range: $startDate to $endDate")
        return emptyList() // Stub implementation
    }
    
    override suspend fun getActivityById(activityId: String): DayActivity? {
        Timber.d("Remote: getActivityById called (stub) for activity: $activityId")
        return null // Stub implementation
    }
    
    override suspend fun createActivity(activity: DayActivity): DayActivity {
        Timber.d("Remote: createActivity called (stub) for activity: ${activity.title}")
        return activity // Stub implementation, just returns the input activity
    }
    
    override suspend fun updateActivity(activity: DayActivity): DayActivity {
        Timber.d("Remote: updateActivity called (stub) for activity: ${activity.id}")
        return activity // Stub implementation, just returns the input activity
    }
    
    override suspend fun deleteActivity(activityId: String): Boolean {
        Timber.d("Remote: deleteActivity called (stub) for activity: $activityId")
        return true // Stub implementation, pretend it always succeeds
    }
    
    override suspend fun toggleActivityCompletion(activityId: String, completed: Boolean): DayActivity {
        Timber.d("Remote: toggleActivityCompletion called (stub) for activity: $activityId to $completed")
        throw NotImplementedError("Remote toggleActivityCompletion not implemented yet") // Demonstrate error handling
    }
}
