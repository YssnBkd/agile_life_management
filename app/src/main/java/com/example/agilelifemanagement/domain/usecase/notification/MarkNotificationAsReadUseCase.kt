package com.example.agilelifemanagement.domain.usecase.notification

import com.example.agilelifemanagement.domain.model.Result
import com.example.agilelifemanagement.domain.repository.temporary.TempNotificationRepository
import javax.inject.Inject

/**
 * Use case for marking a notification as read.
 */
class MarkNotificationAsReadUseCase @Inject constructor(
    private val notificationRepository: TempNotificationRepository
) {
    suspend operator fun invoke(notificationId: String): Result<Unit> {
        return try {
            val result = notificationRepository.markAsRead(notificationId)
            if (result.isSuccess) Result.Success(Unit)
            else Result.Error("Failed to mark notification as read")
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to mark notification as read")
        }
    }
}
