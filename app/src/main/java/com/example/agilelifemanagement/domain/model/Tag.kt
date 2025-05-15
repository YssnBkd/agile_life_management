package com.example.agilelifemanagement.domain.model

/**
 * Domain model representing a tag in the AgileLifeManagement app.
 * Tags are used to categorize tasks for easier filtering and organization.
 */
data class Tag(
    val id: String,
    val name: String,
    val color: String // Hex color code (e.g., "#4287f5")
)
