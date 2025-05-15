package com.example.agilelifemanagement.data.remote.api

import com.example.agilelifemanagement.data.remote.model.SprintDto
import com.example.agilelifemanagement.data.remote.model.SprintReviewDto
import io.ktor.client.HttpClient
import timber.log.Timber
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of [SprintApiService] that serves as a stub for future API integration.
 * This implementation provides placeholder functionality that will be replaced with
 * actual API calls in future iterations.
 */
@Singleton
class SprintApiServiceImpl @Inject constructor(
    private val httpClient: HttpClient
) : SprintApiService {
    
    override suspend fun getAllSprints(): List<SprintDto> {
        Timber.d("API: getAllSprints called (stub)")
        return emptyList() // Stub implementation
    }
    
    override suspend fun getSprintById(sprintId: String): SprintDto? {
        Timber.d("API: getSprintById called (stub) for sprint: $sprintId")
        return null // Stub implementation
    }
    
    override suspend fun getActiveSprintAtDate(date: LocalDate): SprintDto? {
        Timber.d("API: getActiveSprintAtDate called (stub) for date: $date")
        return null // Stub implementation
    }
    
    override suspend fun getSprintsInRange(startDate: LocalDate, endDate: LocalDate): List<SprintDto> {
        Timber.d("API: getSprintsInRange called (stub) for range: $startDate to $endDate")
        return emptyList() // Stub implementation
    }
    
    override suspend fun createSprint(sprint: SprintDto): SprintDto {
        Timber.d("API: createSprint called (stub) for sprint: ${sprint.name}")
        return sprint.copy(id = "generated-id-${System.currentTimeMillis()}") // Stub implementation
    }
    
    override suspend fun updateSprint(sprint: SprintDto): SprintDto {
        Timber.d("API: updateSprint called (stub) for sprint: ${sprint.id}")
        return sprint // Stub implementation
    }
    
    override suspend fun deleteSprint(sprintId: String): Boolean {
        Timber.d("API: deleteSprint called (stub) for sprint: $sprintId")
        return true // Stub implementation
    }
    
    override suspend fun createSprintReview(sprintId: String, review: SprintReviewDto): SprintReviewDto {
        Timber.d("API: createSprintReview called (stub) for sprint: $sprintId")
        return review.copy(id = "generated-id-${System.currentTimeMillis()}") // Stub implementation
    }
    
    override suspend fun getSprintReview(sprintId: String): SprintReviewDto? {
        Timber.d("API: getSprintReview called (stub) for sprint: $sprintId")
        return null // Stub implementation
    }
}
