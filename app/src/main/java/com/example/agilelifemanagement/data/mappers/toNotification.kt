package com.example.agilelifemanagement.data.mappers

import com.example.agilelifemanagement.data.local.entity.NotificationEntity
import com.example.agilelifemanagement.data.remote.dto.NotificationDto
import com.example.agilelifemanagement.domain.model.Notification
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

fun NotificationEntity.toNotification(): Notification = Notification(
    id = id,
    title = title,
    message = message,
    scheduledTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(scheduledTime), ZoneId.systemDefault()),
    isRead = isRead,
    relatedEntityId = relatedEntityId,
    relatedEntityType = relatedEntityType?.let { typeString ->
        try {
            Notification.EntityType.valueOf(typeString)
        } catch (e: IllegalArgumentException) {
            null
        }
    }
)

fun NotificationDto.toNotificationEntity(): NotificationEntity = NotificationEntity(
    id = id,
    title = title,
    message = message,
    scheduledTime = scheduled_time,
    isRead = is_read,
    relatedEntityId = related_entity_id,
    relatedEntityType = related_entity_type,
    userId = user_id,
    createdAt = created_at
)
