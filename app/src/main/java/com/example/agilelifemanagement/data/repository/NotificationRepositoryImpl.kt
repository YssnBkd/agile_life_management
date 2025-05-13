package com.example.agilelifemanagement.data.repository

import com.example.agilelifemanagement.data.local.dao.NotificationDao
import com.example.agilelifemanagement.data.local.entity.NotificationEntity
import com.example.agilelifemanagement.data.local.entity.PendingOperation
import com.example.agilelifemanagement.data.mappers.toNotification
import com.example.agilelifemanagement.data.remote.api.NotificationApiService
import com.example.agilelifemanagement.data.remote.SupabaseManager
import com.example.agilelifemanagement.data.remote.SyncManager
import com.example.agilelifemanagement.data.remote.dto.NotificationDto
import com.example.agilelifemanagement.domain.model.Notification
import com.example.agilelifemanagement.domain.model.Result
import com.example.agilelifemanagement.domain.repository.NotificationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.time.ZoneId
import java.util.UUID
import javax.inject.Inject
import com.example.agilelifemanagement.util.NetworkMonitor

/**
 * Implementation of [NotificationRepository] that follows the offline-first strategy.
 */
class NotificationRepositoryImpl @Inject constructor(
    private val notificationDao: NotificationDao,
    private val notificationApiService: NotificationApiService,
    private val syncManager: SyncManager,
    private val supabaseManager: SupabaseManager,
    private val networkMonitor: NetworkMonitor
) : NotificationRepository {

    override fun getNotifications(): Flow<List<Notification>> {
        return notificationDao.getAllNotifications().map { entities -> entities.map { it.toNotification() } }
    }

    override fun getUnreadNotifications(): Flow<List<Notification>> {
        return notificationDao.getUnreadNotifications().map { entities -> entities.map { it.toNotification() } }
    }

    override fun getNotificationById(id: String): Flow<Notification?> {
        return notificationDao.getNotificationById(id).map { entity -> entity?.toNotification() }
    }

    override suspend fun insertNotification(notification: Notification): String {
        val id = notification.id.ifEmpty { UUID.randomUUID().toString() }
        val userId = supabaseManager.getCurrentUserId().first() ?: error("User ID must not be null")
        val notificationEntity = NotificationEntity(
            id = id,
            title = notification.title,
            message = notification.message,
            scheduledTime = notification.scheduledTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
            isRead = notification.isRead,
            relatedEntityId = notification.relatedEntityId,
            relatedEntityType = notification.relatedEntityType?.name,
            userId = userId,
            createdAt = System.currentTimeMillis()
        )
        notificationDao.insert(notificationEntity)
        syncManager.scheduleSyncOperation(id, "notification", PendingOperation.CREATE)
        return id
    }

    override suspend fun updateNotification(notification: Notification) {
        val existingEntity = notificationDao.getNotificationByIdSync(notification.id)
        if (existingEntity != null) {
            val updatedEntity = existingEntity.copy(
                title = notification.title,
                message = notification.message,
                scheduledTime = notification.scheduledTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
                isRead = notification.isRead,
                relatedEntityId = notification.relatedEntityId,
                relatedEntityType = notification.relatedEntityType?.name
            )
            notificationDao.updateNotification(updatedEntity)
            syncManager.scheduleSyncOperation(notification.id, "notification", PendingOperation.UPDATE)
        }
    }

    override suspend fun deleteNotification(id: String) {
        notificationDao.deleteById(id)
        try {
            notificationApiService.deleteNotification(id)
        } catch (e: Exception) {
            // Log error but don't throw
        }
    }

    override suspend fun markAsRead(id: String) {
        val notification = notificationDao.getNotificationByIdSync(id)
        if (notification != null && !notification.isRead) {
            val updatedNotification = notification.copy(isRead = true)
            notificationDao.updateNotification(updatedNotification)
            syncManager.scheduleSyncOperation(id, "notification", PendingOperation.UPDATE)
        }
    }

    override suspend fun markAllAsRead() {
        notificationDao.markAllAsRead()
        syncManager.scheduleSyncOperation("bulk_notification", "notification", PendingOperation.UPDATE)
    }

    // Private helper methods
    private suspend fun syncNotification(entity: NotificationEntity): Result<Unit> {
        return try {
            val userId = supabaseManager.getCurrentUserId().first() ?: error("User ID must not be null")
            val dto = NotificationDto(
                id = entity.id,
                title = entity.title,
                message = entity.message,
                scheduled_time = entity.scheduledTime,
                is_read = entity.isRead,
                related_entity_id = entity.relatedEntityId,
                related_entity_type = entity.relatedEntityType,
                user_id = userId,
                created_at = entity.createdAt
            )
            notificationApiService.upsertNotification(dto)
            syncManager.markSynced(entity.id, "notification")
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error("Failed to sync notification: ${e.message}", e)
        }
    }
}
