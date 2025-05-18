package com.example.agilelifemanagement.domain.model

import java.time.LocalDateTime

/**
 * Domain model representing a notification in the application.
 * Notifications provide users with important updates about their
 * activities, goals, sprints, and other relevant information.
 */
data class Notification(
    val id: String,
    val title: String,
    val message: String,
    val type: NotificationType,
    val timestamp: LocalDateTime = LocalDateTime.now(),
    val isRead: Boolean = false,
    val actionUrl: String? = null,
    val relatedItemId: String? = null,
    val userId: String = ""
)

/**
 * Enumeration representing the different types of notifications
 * that can be shown to the user.
 */
enum class NotificationType {
    // Activity notifications
    ACTIVITY_DUE_SOON,
    ACTIVITY_OVERDUE,
    ACTIVITY_REMINDER,
    
    // Goal notifications
    GOAL_DEADLINE_APPROACHING,
    GOAL_DEADLINE_MISSED,
    GOAL_COMPLETED,
    
    // Sprint notifications
    SPRINT_STARTED,
    SPRINT_ENDING_SOON,
    SPRINT_COMPLETED,
    
    // System notifications
    SYSTEM_UPDATE,
    SYSTEM_MAINTENANCE,
    
    // User action notifications
    ACTION_REQUIRED,
    
    // Wellness notifications
    WELLNESS_REMINDER,
    WELLNESS_INSIGHT
}
