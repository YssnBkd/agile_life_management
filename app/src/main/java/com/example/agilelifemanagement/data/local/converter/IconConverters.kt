package com.example.agilelifemanagement.data.local.converter

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Dashboard
import androidx.compose.material.icons.rounded.DateRange
import androidx.compose.material.icons.rounded.Task
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.room.TypeConverter

/**
 * Type converters for Room database to handle ImageVector icons.
 * 
 * These converters allow Room to store references to Material Icons by
 * converting them to/from string names. This implementation maps common
 * icons used in the app to their string representations.
 * 
 * Follows Material 3 Expressive design principles for iconography.
 */
class IconConverters {
    
    private val iconMap = mapOf(
        // Filled icons
        "star" to Icons.Filled.Star,
        
        // Rounded icons
        "add" to Icons.Rounded.Add,
        "dashboard" to Icons.Rounded.Dashboard,
        "date_range" to Icons.Rounded.DateRange,
        "task" to Icons.Rounded.Task
        
        // Add more icons as needed
    )
    
    @TypeConverter
    fun fromImageVector(icon: ImageVector?): String? {
        if (icon == null) return null
        
        return iconMap.entries.find { it.value == icon }?.key ?: "star" // Default to star if not found
    }
    
    @TypeConverter
    fun toImageVector(iconName: String?): ImageVector {
        if (iconName == null) return Icons.Filled.Star // Default icon
        
        return iconMap[iconName.lowercase()] ?: Icons.Filled.Star
    }
}
