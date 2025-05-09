package com.example.agilelifemanagement.domain.repository

import com.example.agilelifemanagement.domain.model.Notification
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for Notification operations.
 */
interface NotificationRepository {
    fun getNotifications(): Flow<List<Notification>>
    fun getNotificationById(id: String): Flow<Notification?>
    fun getUnreadNotifications(): Flow<List<Notification>>
    suspend fun insertNotification(notification: Notification): String
    suspend fun updateNotification(notification: Notification)
    suspend fun deleteNotification(id: String)
    suspend fun markAsRead(id: String)
    suspend fun markAllAsRead()
}
