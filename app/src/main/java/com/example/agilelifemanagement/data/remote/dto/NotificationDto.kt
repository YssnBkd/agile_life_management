package com.example.agilelifemanagement.data.remote.dto

import com.example.agilelifemanagement.data.local.entity.NotificationEntity
import kotlinx.serialization.Serializable

/**
 * Data Transfer Object for Notification in Supabase.
 */
@Serializable
data class NotificationDto(
    val id: String,
    val title: String,
    val message: String,
    val scheduled_time: Long,
    val is_read: Boolean,
    val related_entity_id: String? = null,
    val related_entity_type: String? = null,
    val user_id: String,
    val created_at: Long
) {
    /**
     * Convert DTO to local entity.
     */
    fun toEntity(): NotificationEntity {
        return NotificationEntity(
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
    }
    
    companion object {
        /**
         * Convert local entity to DTO.
         */
        fun fromEntity(entity: NotificationEntity): NotificationDto {
            return NotificationDto(
                id = entity.id,
                title = entity.title,
                message = entity.message,
                scheduled_time = entity.scheduledTime,
                is_read = entity.isRead,
                related_entity_id = entity.relatedEntityId,
                related_entity_type = entity.relatedEntityType,
                user_id = entity.userId,
                created_at = entity.createdAt
            )
        }
    }
}
