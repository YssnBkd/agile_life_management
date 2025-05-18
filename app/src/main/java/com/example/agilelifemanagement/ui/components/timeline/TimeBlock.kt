package com.example.agilelifemanagement.ui.components.timeline

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.agilelifemanagement.ui.theme.AgileBlue
import com.example.agilelifemanagement.ui.theme.AgileGreen
import com.example.agilelifemanagement.ui.theme.AgilePurple
import com.example.agilelifemanagement.ui.theme.WarningOrange
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.UUID

/**
 * Categories for time blocks to visually distinguish different types of activities
 */
enum class TimeBlockCategory {
    TASK,
    MEETING,
    BREAK,
    FOCUS,
    PERSONAL;
    
    val label: String
        get() = when(this) {
            TASK -> "Task"
            MEETING -> "Meeting"
            BREAK -> "Break"
            FOCUS -> "Focus Time"
            PERSONAL -> "Personal"
        }
        
    val color: Color
        get() = when(this) {
            TASK -> AgileBlue
            MEETING -> AgilePurple
            BREAK -> WarningOrange
            FOCUS -> Color(0xFF6200EA)
            PERSONAL -> AgileGreen
        }
        
    val icon: ImageVector
        get() = when(this) {
            TASK -> Icons.Filled.Star
            MEETING -> Icons.Filled.Star
            BREAK -> Icons.Filled.Star
            FOCUS -> Icons.Filled.Star
            PERSONAL -> Icons.Filled.Star
        }
}

/**
 * Represents a block of time in a day schedule.
 * Used for visual representation of activities in timeline views.
 */
data class TimeBlock(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String = "",
    val location: String = "",
    val timeRange: String,
    val category: TimeBlockCategory = TimeBlockCategory.TASK,
    val color: Color = category.color,
    val icon: ImageVector = Icons.Filled.Star,
    val isCompleted: Boolean = false
) {
    
    companion object {
        /**
         * Converts a DayActivity to a TimeBlock for UI display
         */
        fun fromDayActivity(activity: com.example.agilelifemanagement.domain.model.DayActivity): TimeBlock {
            // Calculate end time based on scheduled time and duration
            val endTime = activity.scheduledTime.plusMinutes(activity.duration.toLong())
            
            // Format the time range string
            val timeRangeFormatter = DateTimeFormatter.ofPattern("h:mm a")
            val timeRangeString = "${activity.scheduledTime.format(timeRangeFormatter)} - ${endTime.format(timeRangeFormatter)}"
            
            // Map category from activity.categoryId
            val category = when (activity.categoryId) {
                "work" -> TimeBlockCategory.TASK
                "meeting" -> TimeBlockCategory.MEETING
                "focus" -> TimeBlockCategory.FOCUS
                "break" -> TimeBlockCategory.BREAK
                "personal" -> TimeBlockCategory.PERSONAL
                else -> TimeBlockCategory.TASK
            }
            
            return TimeBlock(
                id = activity.id,
                title = activity.title,
                description = activity.description,
                location = "", // DayActivity doesn't have a location property, using empty string
                timeRange = timeRangeString,
                category = category,
                isCompleted = activity.completed
            )
        }
        
        /**
         * Creates a list of sample time blocks for preview and testing
         */
        fun getSampleTimeBlocks(): List<TimeBlock> {
            return listOf(
                TimeBlock(
                    id = UUID.randomUUID().toString(),
                    title = "Morning Standup",
                    description = "Daily team sync",
                    location = "Conference Room A",
                    timeRange = "9:00 AM - 9:30 AM",
                    category = TimeBlockCategory.MEETING
                ),
                TimeBlock(
                    id = UUID.randomUUID().toString(),
                    title = "Work on UI Designs",
                    description = "Finish dashboard mockups",
                    timeRange = "10:00 AM - 12:00 PM",
                    category = TimeBlockCategory.TASK
                ),
                TimeBlock(
                    id = UUID.randomUUID().toString(),
                    title = "Lunch Break",
                    timeRange = "12:00 PM - 1:00 PM",
                    category = TimeBlockCategory.BREAK
                ),
                TimeBlock(
                    id = UUID.randomUUID().toString(),
                    title = "Workout",
                    description = "Cardio + Strength",
                    location = "Gym",
                    timeRange = "5:30 PM - 6:30 PM",
                    category = TimeBlockCategory.PERSONAL,
                    isCompleted = true
                )
            )
        }
    }
}
