package com.example.agilelifemanagement.data.remote.source

import com.example.agilelifemanagement.domain.model.Sprint
import com.example.agilelifemanagement.domain.model.SprintReview
import timber.log.Timber
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of [SprintRemoteDataSource] that serves as a stub for future remote integration.
 * This implementation provides placeholder functionality that will be replaced with
 * actual remote API calls in future iterations.
 */
@Singleton
class SprintRemoteDataSourceImpl @Inject constructor() : SprintRemoteDataSource {
    
    override suspend fun getAllSprints(): List<Sprint> {
        Timber.d("Remote: getAllSprints called (stub)")
        return emptyList() // Stub implementation
    }
    
    override suspend fun getSprintById(sprintId: String): Sprint? {
        Timber.d("Remote: getSprintById called (stub) for sprint: $sprintId")
        return null // Stub implementation
    }
    
    override suspend fun getActiveSprintAtDate(date: LocalDate): Sprint? {
        Timber.d("Remote: getActiveSprintAtDate called (stub) for date: $date")
        return null // Stub implementation
    }
    
    override suspend fun getSprintsInRange(startDate: LocalDate, endDate: LocalDate): List<Sprint> {
        Timber.d("Remote: getSprintsInRange called (stub) for range: $startDate to $endDate")
        return emptyList() // Stub implementation
    }
    
    override suspend fun createSprint(sprint: Sprint): Sprint {
        Timber.d("Remote: createSprint called (stub) for sprint: ${sprint.name}")
        return sprint // Stub implementation, just returns the input sprint
    }
    
    override suspend fun updateSprint(sprint: Sprint): Sprint {
        Timber.d("Remote: updateSprint called (stub) for sprint: ${sprint.id}")
        return sprint // Stub implementation, just returns the input sprint
    }
    
    override suspend fun deleteSprint(sprintId: String): Boolean {
        Timber.d("Remote: deleteSprint called (stub) for sprint: $sprintId")
        return true // Stub implementation, pretend it always succeeds
    }
    
    override suspend fun createSprintReview(sprintId: String, review: SprintReview): SprintReview {
        Timber.d("Remote: createSprintReview called (stub) for sprint: $sprintId")
        return review // Stub implementation, just returns the input review
    }
    
    override suspend fun getSprintReview(sprintId: String): SprintReview? {
        Timber.d("Remote: getSprintReview called (stub) for sprint: $sprintId")
        return null // Stub implementation
    }
}
