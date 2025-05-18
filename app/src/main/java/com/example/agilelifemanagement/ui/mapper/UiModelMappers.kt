package com.example.agilelifemanagement.ui.mapper

import androidx.compose.ui.graphics.Color
import com.example.agilelifemanagement.domain.model.DayActivity
import com.example.agilelifemanagement.domain.model.DaySchedule
import com.example.agilelifemanagement.domain.model.TimeBlock
import com.example.agilelifemanagement.ui.screens.day.ActivityCategory
import com.example.agilelifemanagement.ui.theme.AgileBlue
import com.example.agilelifemanagement.ui.theme.AgileGreen
import com.example.agilelifemanagement.ui.theme.WarningOrange
import com.example.agilelifemanagement.ui.theme.AgilePurple
import java.time.LocalTime
import java.time.format.DateTimeFormatter

/**
 * Maps domain models to UI-specific models
 */
object UiModelMappers {
    
    private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    
    /**
     * Maps a domain TimeBlock to a UI TimeBlock
     */
    fun mapTimeBlockToUiTimeBlock(timeBlock: TimeBlock): UiTimeBlock {
        val category = mapCategoryIdToActivityCategory(timeBlock.categoryId)
        
        return UiTimeBlock(
            id = timeBlock.id,
            title = timeBlock.title,
            startTime = timeBlock.startTime.format(timeFormatter),
            endTime = timeBlock.endTime?.format(timeFormatter) ?: "",
            category = category
        )
    }
    
    /**
     * Maps a domain DayActivity to a UI TimeBlock
     */
    fun mapDayActivityToUiTimeBlock(activity: DayActivity): UiTimeBlock {
        val endTime = activity.scheduledTime.plusMinutes(activity.duration.toLong())
        val category = mapCategoryIdToActivityCategory(activity.categoryId)
        
        return UiTimeBlock(
            id = activity.id,
            title = activity.title,
            startTime = activity.scheduledTime.format(timeFormatter),
            endTime = endTime.format(timeFormatter),
            category = category
        )
    }
    
    /**
     * Maps a UI TimeBlock to a domain TimeBlock
     */
    fun mapUiTimeBlockToTimeBlock(uiTimeBlock: UiTimeBlock): TimeBlock {
        return TimeBlock(
            id = uiTimeBlock.id,
            title = uiTimeBlock.title,
            startTime = parseTimeOrDefault(uiTimeBlock.startTime),
            endTime = parseTimeOrDefault(uiTimeBlock.endTime),
            categoryId = mapActivityCategoryToCategoryId(uiTimeBlock.category),
            color = uiTimeBlock.category.getColor()
        )
    }
    
    /**
     * Maps a UI TimeBlock to a domain DayActivity
     */
    fun mapUiTimeBlockToDayActivity(uiTimeBlock: UiTimeBlock, date: java.time.LocalDate): DayActivity {
        val startTime = parseTimeOrDefault(uiTimeBlock.startTime)
        val endTime = parseTimeOrDefault(uiTimeBlock.endTime)
        
        // Calculate duration in minutes
        val duration = if (endTime != null) {
            val startMinutes = startTime.hour * 60 + startTime.minute
            val endMinutes = endTime.hour * 60 + endTime.minute
            endMinutes - startMinutes
        } else {
            60 // Default to 1 hour if endTime is not provided
        }
        
        return DayActivity(
            id = uiTimeBlock.id,
            title = uiTimeBlock.title,
            description = "",
            date = date,
            scheduledTime = startTime,
            duration = duration,
            completed = false,
            categoryId = mapActivityCategoryToCategoryId(uiTimeBlock.category)
        )
    }
    
    /**
     * Maps a category ID to an ActivityCategory enum
     */
    private fun mapCategoryIdToActivityCategory(categoryId: String?): ActivityCategory {
        return when (categoryId) {
            "WORK" -> ActivityCategory.WORK
            "PERSONAL" -> ActivityCategory.PERSONAL
            "FITNESS" -> ActivityCategory.FITNESS
            "BREAK" -> ActivityCategory.BREAK
            else -> ActivityCategory.PERSONAL
        }
    }
    
    /**
     * Maps an ActivityCategory enum to a category ID
     */
    private fun mapActivityCategoryToCategoryId(category: ActivityCategory): String {
        return category.name
    }
    
    /**
     * Parses a time string to LocalTime
     */
    private fun parseTimeOrDefault(timeString: String): LocalTime {
        return try {
            LocalTime.parse(timeString, timeFormatter)
        } catch (e: Exception) {
            LocalTime.of(8, 0) // Default to 8:00 AM
        }
    }
}

/**
 * UI-specific model for TimeBlock to be used in the UI layer
 */
data class UiTimeBlock(
    val id: String,
    val title: String,
    val startTime: String,
    val endTime: String,
    val category: ActivityCategory
)

/**
 * Extension function to get color from ActivityCategory
 */
fun ActivityCategory.getColor(): Color {
    return when (this) {
        ActivityCategory.WORK -> AgileBlue
        ActivityCategory.PERSONAL -> AgilePurple
        ActivityCategory.FITNESS -> AgileGreen
        ActivityCategory.BREAK -> WarningOrange
    }
}
