package com.example.agilelifemanagement.domain.usecase.day.activity

import com.example.agilelifemanagement.domain.model.DayActivity
import com.example.agilelifemanagement.domain.repository.temporary.TempDayRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Use case for retrieving a specific day activity by ID.
 * 
 * Note: This is a temporary implementation after the May 15, 2025 architectural change
 * where the data layer was archived for rebuilding.
 */
class GetDayActivityByIdUseCase @Inject constructor(
    private val dayRepository: TempDayRepository
) {
    /**
     * Gets a Flow emitting the activity with the specified ID, or null if not found.
     * @param activityId The ID of the activity to retrieve
     * @return A Flow emitting the activity or null
     */
    operator fun invoke(activityId: String): Flow<DayActivity?> = flow {
        // Temporary implementation always returns null since we don't have repository implementation
        emit(null)
    }
}
