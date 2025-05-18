package com.example.agilelifemanagement.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity representing an activity category in the local database.
 * Categories are used to organize and group different types of day activities.
 * 
 * This entity follows Material 3 Expressive design principles by storing
 * the color information needed for consistent UI theming.
 */
@Entity(tableName = "activity_categories")
data class ActivityCategoryEntity(
    @PrimaryKey
    val id: String,
    
    val name: String,
    
    @ColumnInfo(name = "color_hex")
    val colorHex: String, // Hex color code (e.g., "#FF5733")
    
    @ColumnInfo(name = "icon_name", defaultValue = "star")
    val iconName: String = "star", // Default icon if none specified
    
    @ColumnInfo(name = "is_system_category", defaultValue = "0")
    val isSystemCategory: Boolean = false // Flag for system-defined categories
)
