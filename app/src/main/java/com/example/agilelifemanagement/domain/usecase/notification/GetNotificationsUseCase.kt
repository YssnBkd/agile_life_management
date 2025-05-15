package com.example.agilelifemanagement.domain.usecase.notification

import com.example.agilelifemanagement.domain.model.Notification
import com.example.agilelifemanagement.domain.repository.NotificationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for retrieving all notifications.
 */
class GetNotificationsUseCase @Inject constructor(
    private val notificationRepository: NotificationRepository
) {
    operator fun invoke(): Flow<List<Notification>> {
        return notificationRepository.getAllNotifications()
    }
}
