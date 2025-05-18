package com.example.agilelifemanagement.domain.model

/**
 * Domain model representing an activity category in the AgileLifeManagement app.
 * Categories are used to organize and group different types of day activities.
 */
data class ActivityCategory(
    val id: String,
    val name: String,
    val color: String, // Hex color code (e.g., "#FF5733")
    val iconName: String? = null,
    val isSystemCategory: Boolean = false
)
