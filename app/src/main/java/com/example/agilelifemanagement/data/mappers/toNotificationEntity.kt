package com.example.agilelifemanagement.data.mappers

import com.example.agilelifemanagement.data.local.entity.NotificationEntity
import com.example.agilelifemanagement.data.remote.dto.NotificationDto

fun NotificationEntity.toNotificationDto(): NotificationDto = NotificationDto(
    id = id,
    title = title,
    message = message,
    scheduled_time = scheduledTime,
    is_read = isRead,
    related_entity_id = relatedEntityId,
    related_entity_type = relatedEntityType,
    user_id = userId,
    created_at = createdAt
)
