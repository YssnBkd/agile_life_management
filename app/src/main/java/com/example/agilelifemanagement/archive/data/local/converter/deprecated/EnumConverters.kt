package com.example.agilelifemanagement.data.local.converter

import androidx.room.TypeConverter

/**
 * Enum classes for database entities
 */
enum class TaskPriority {
    LOW, MEDIUM, HIGH
}

enum class TaskStatus {
    TODO, IN_PROGRESS, DONE, BLOCKED
}

enum class GoalCategory {
    PERSONAL, PROFESSIONAL, HEALTH, FINANCIAL, LEARNING, OTHER
}

enum class CheckupEntryType {
    ACCOMPLISHMENT, PLANNED_TASK, BLOCKER
}

enum class ReviewEntryType {
    WENT_WELL, COULD_IMPROVE, SUMMARY, ACTIONABLE_IMPROVEMENT
}

/**
 * Type converters for Room database to convert between enums and their Int representations.
 */
class EnumConverters {
    @TypeConverter
    fun fromTaskPriority(priority: TaskPriority?): Int? {
        return priority?.ordinal
    }
    
    @TypeConverter
    fun toTaskPriority(value: Int?): TaskPriority? {
        return value?.let { TaskPriority.values()[it] }
    }
    
    @TypeConverter
    fun fromTaskStatus(status: TaskStatus?): Int? {
        return status?.ordinal
    }
    
    @TypeConverter
    fun toTaskStatus(value: Int?): TaskStatus? {
        return value?.let { TaskStatus.values()[it] }
    }
    
    @TypeConverter
    fun fromGoalCategory(category: GoalCategory?): Int? {
        return category?.ordinal
    }
    
    @TypeConverter
    fun toGoalCategory(value: Int?): GoalCategory? {
        return value?.let { GoalCategory.values()[it] }
    }
    
    @TypeConverter
    fun fromCheckupEntryType(type: CheckupEntryType?): Int? {
        return type?.ordinal
    }
    
    @TypeConverter
    fun toCheckupEntryType(value: Int?): CheckupEntryType? {
        return value?.let { CheckupEntryType.values()[it] }
    }
    
    @TypeConverter
    fun fromReviewEntryType(type: ReviewEntryType?): Int? {
        return type?.ordinal
    }
    
    @TypeConverter
    fun toReviewEntryType(value: Int?): ReviewEntryType? {
        return value?.let { ReviewEntryType.values()[it] }
    }
}
