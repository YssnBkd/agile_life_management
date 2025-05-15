package com.example.agilelifemanagement.domain.usecase.notification

import com.example.agilelifemanagement.domain.model.Result
import com.example.agilelifemanagement.domain.repository.NotificationRepository
import javax.inject.Inject

/**
 * Use case for marking a notification as read.
 */
class MarkNotificationAsReadUseCase @Inject constructor(
    private val notificationRepository: NotificationRepository
) {
    suspend operator fun invoke(notificationId: String): Result<Unit> {
        return try {
            notificationRepository.markNotificationAsRead(notificationId)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to mark notification as read")
        }
    }
}
