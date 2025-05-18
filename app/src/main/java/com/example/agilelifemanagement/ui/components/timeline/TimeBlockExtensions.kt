package com.example.agilelifemanagement.ui.components.timeline

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star

/**
 * Extension helper for TimeBlock class with utility functions
 */
object TimeBlockExtensions {
    /**
     * Get sample time blocks for preview and testing
     */
    fun getSampleTimeBlocks(): List<TimeBlock> {
        return listOf(
            TimeBlock(
                id = "1",
                title = "Morning Standup",
                description = "Daily team sync",
                location = "Conference Room A",
                timeRange = "9:00 AM - 9:30 AM",
                category = TimeBlockCategory.MEETING
            ),
            TimeBlock(
                id = "2",
                title = "Work on UI Designs",
                description = "Finish dashboard mockups",
                timeRange = "10:00 AM - 12:00 PM",
                category = TimeBlockCategory.TASK
            ),
            TimeBlock(
                id = "3",
                title = "Lunch Break",
                timeRange = "12:00 PM - 1:00 PM",
                category = TimeBlockCategory.BREAK
            ),
            TimeBlock(
                id = "4",
                title = "Workout",
                description = "Cardio + Strength",
                location = "Gym",
                timeRange = "5:30 PM - 6:30 PM",
                category = TimeBlockCategory.PERSONAL
            )
        )
    }
    
    /**
     * Calculate the duration of a time block in minutes
     */
    fun calculateDurationMinutes(timeRange: String): Int {
        // Very simple implementation for sample data
        // In a real app, you would parse the times properly
        return 60 // Default to 60 minutes
    }
}
