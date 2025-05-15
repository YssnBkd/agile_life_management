package com.example.agilelifemanagement.data.local.converter

import androidx.room.TypeConverter
import com.example.agilelifemanagement.domain.model.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

/**
 * Type converters for Room database.
 * Handles conversions between complex types and their String representations for storage in SQLite.
 */
class Converters {
    private val gson = Gson()
    
    // Date converters
    @TypeConverter
    fun fromLocalDate(date: LocalDate?): String? {
        return date?.toString()
    }
    
    @TypeConverter
    fun toLocalDate(dateString: String?): LocalDate? {
        return dateString?.let { LocalDate.parse(it) }
    }
    
    // Time converters
    @TypeConverter
    fun fromLocalTime(time: LocalTime?): String? {
        return time?.toString()
    }
    
    @TypeConverter
    fun toLocalTime(timeString: String?): LocalTime? {
        return timeString?.let { LocalTime.parse(it) }
    }
    
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
    
    // Enum converters
    @TypeConverter
    fun fromTaskStatus(status: TaskStatus?): String? {
        return status?.name
    }
    
    @TypeConverter
    fun toTaskStatus(status: String?): TaskStatus? {
        return status?.let { TaskStatus.valueOf(it) }
    }
    
    @TypeConverter
    fun fromTaskPriority(priority: TaskPriority?): String? {
        return priority?.name
    }
    
    @TypeConverter
    fun toTaskPriority(priority: String?): TaskPriority? {
        return priority?.let { TaskPriority.valueOf(it) }
    }
    
    @TypeConverter
    fun fromSprintStatus(status: SprintStatus?): String? {
        return status?.name
    }
    
    @TypeConverter
    fun toSprintStatus(status: String?): SprintStatus? {
        return status?.let { SprintStatus.valueOf(it) }
    }
    
    @TypeConverter
    fun fromGoalStatus(status: GoalStatus?): String? {
        return status?.name
    }
    
    @TypeConverter
    fun toGoalStatus(status: String?): GoalStatus? {
        return status?.let { GoalStatus.valueOf(it) }
    }
    
    @TypeConverter
    fun fromGoalPriority(priority: GoalPriority?): String? {
        return priority?.name
    }
    
    @TypeConverter
    fun toGoalPriority(priority: String?): GoalPriority? {
        return priority?.let { GoalPriority.valueOf(it) }
    }
}
