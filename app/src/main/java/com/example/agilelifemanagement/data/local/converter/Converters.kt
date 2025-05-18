package com.example.agilelifemanagement.data.local.converter

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Dashboard
import androidx.compose.material.icons.rounded.DateRange
import androidx.compose.material.icons.rounded.Task
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.room.TypeConverter
import com.example.agilelifemanagement.domain.model.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.UUID

/**
 * Centralized type converters for Room database.
 * Handles conversions between complex types and their SQLite-compatible representations.
 * 
 * This single class consolidates all type conversions needed for the database:
 * - Date/Time: LocalDate, LocalTime
 * - Colors: Compose UI Colors
 * - Icons: ImageVector icons
 * - UUIDs: Java UUID objects
 * - Collections: Lists, Maps
 * - Enums: TaskStatus, TimeBlockCategory, etc.
 * 
 * Follows Material 3 Expressive design principles by ensuring consistent
 * data representation across the application.
 */
class Converters {
    private val gson = Gson()
    private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE
    private val timeFormatter = DateTimeFormatter.ISO_LOCAL_TIME
    
    // Map of icon names to ImageVector objects
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
    
    //===========================================
    // DATE/TIME CONVERTERS
    //===========================================
    
    @TypeConverter
    fun fromLocalDate(date: LocalDate?): String? {
        return date?.format(dateFormatter)
    }
    
    @TypeConverter
    fun toLocalDate(dateString: String?): LocalDate? {
        return dateString?.let { LocalDate.parse(it, dateFormatter) }
    }
    
    @TypeConverter
    fun fromLocalTime(time: LocalTime?): String? {
        return time?.format(timeFormatter)
    }
    
    @TypeConverter
    fun toLocalTime(timeString: String?): LocalTime? {
        return timeString?.let { LocalTime.parse(it, timeFormatter) }
    }
    
    //===========================================
    // COLOR CONVERTERS
    //===========================================
    
    @TypeConverter
    fun fromColor(color: Color?): Int? {
        return color?.toArgb()
    }

    @TypeConverter
    fun toColor(colorInt: Int?): Color? {
        return colorInt?.let { Color(it) }
    }
    
    // Helper function to convert color to hex string
    fun colorToHex(color: Color): String {
        return String.format("#%08X", color.toArgb())
    }
    
    // Helper function to convert hex string to color
    fun hexToColor(hex: String): Color {
        return try {
            Color(android.graphics.Color.parseColor(hex))
        } catch (e: Exception) {
            // Default to primary color if parsing fails
            Color(0xFF6750A4) // Material 3 default primary
        }
    }
    
    //===========================================
    // ICON CONVERTERS
    //===========================================
    
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
    
    //===========================================
    // UUID CONVERTERS
    //===========================================
    
    @TypeConverter
    fun fromUUID(uuid: UUID?): String? {
        return uuid?.toString()
    }

    @TypeConverter
    fun toUUID(uuidString: String?): UUID? {
        return uuidString?.let { UUID.fromString(it) }
    }
    
    //===========================================
    // COLLECTION CONVERTERS
    //===========================================
    
    // List<String> converters
    @TypeConverter
    fun fromStringList(value: List<String>?): String {
        return gson.toJson(value ?: emptyList<String>())
    }
    
    @TypeConverter
    fun toStringList(value: String): List<String> {
        val listType = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(value, listType) ?: emptyList()
    }
    
    //===========================================
    // ENUM CONVERTERS
    //===========================================
    
    // TaskStatus enum converters
    @TypeConverter
    fun fromTaskStatus(status: TaskStatus?): String? {
        return status?.name
    }
    
    @TypeConverter
    fun toTaskStatus(status: String?): TaskStatus? {
        return status?.let { TaskStatus.valueOf(it) }
    }
    
    // TaskPriority enum converters
    @TypeConverter
    fun fromTaskPriority(priority: TaskPriority?): String? {
        return priority?.name
    }
    
    @TypeConverter
    fun toTaskPriority(priority: String?): TaskPriority? {
        return priority?.let { TaskPriority.valueOf(it) }
    }
    
    // SprintStatus enum converters
    @TypeConverter
    fun fromSprintStatus(status: SprintStatus?): String? {
        return status?.name
    }
    
    @TypeConverter
    fun toSprintStatus(status: String?): SprintStatus? {
        return status?.let { SprintStatus.valueOf(it) }
    }
    
    // GoalStatus enum converters
    @TypeConverter
    fun fromGoalStatus(status: GoalStatus?): String? {
        return status?.name
    }
    
    @TypeConverter
    fun toGoalStatus(status: String?): GoalStatus? {
        return status?.let { GoalStatus.valueOf(it) }
    }
    
    // GoalPriority enum converters
    @TypeConverter
    fun fromGoalPriority(priority: GoalPriority?): String? {
        return priority?.name
    }
    
    @TypeConverter
    fun toGoalPriority(priority: String?): GoalPriority? {
        return priority?.let { GoalPriority.valueOf(it) }
    }
    
    // TimeBlockCategory enum converters
    @TypeConverter
    fun fromTimeBlockCategory(category: TimeBlockCategory?): String? {
        return category?.name
    }
    
    @TypeConverter
    fun toTimeBlockCategory(categoryName: String?): TimeBlockCategory? {
        return categoryName?.let { TimeBlockCategory.valueOf(it) }
    }
}
