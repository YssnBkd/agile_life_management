package com.example.agilelifemanagement.domain.model

import java.time.LocalDateTime

/**
 * Domain model for Notification.
 * Represents a notification for the user.
 */
data class Notification(
    val id: String = "",
    val title: String,
    val message: String,
    val scheduledTime: LocalDateTime,
    val isRead: Boolean = false,
    val relatedEntityId: String? = null,
    val relatedEntityType: EntityType? = null
) {
    enum class EntityType {
        TASK, SPRINT, GOAL
    }
}
