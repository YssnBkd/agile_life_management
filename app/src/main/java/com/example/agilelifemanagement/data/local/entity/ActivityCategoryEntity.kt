package com.example.agilelifemanagement.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity representing an activity category in the local database.
 * Categories are used to organize and group different types of day activities.
 */
@Entity(tableName = "activity_categories")
data class ActivityCategoryEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val color: String // Hex color code (e.g., "#FF5733")
)
