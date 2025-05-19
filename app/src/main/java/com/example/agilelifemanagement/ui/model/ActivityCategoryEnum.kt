package com.example.agilelifemanagement.ui.model

import androidx.compose.ui.graphics.Color
import com.example.agilelifemanagement.ui.theme.*

/**
 * UI enum representing activity categories for timeline and template displays.
 */
enum class ActivityCategoryEnum {
    WORK,
    EXERCISE,
    MEAL,
    REST,
    FOCUS,
    MEETING,
    PERSONAL,
    OTHER;
    
    /**
     * Get the color associated with this category.
     */
    val color: Color
        get() = when (this) {
            WORK -> AgileBlue
            EXERCISE -> AgileGreen
            MEAL -> WarningOrange
            REST -> AgilePurple
            FOCUS -> Color(0xFF00796B) // Teal
            MEETING -> Color(0xFF1976D2) // Blue
            PERSONAL -> Color(0xFFE91E63) // Pink
            OTHER -> Color(0xFF9E9E9E) // Gray
        }
    
    /**
     * Get display name for the category.
     */
    val displayName: String
        get() = when (this) {
            WORK -> "Work"
            EXERCISE -> "Exercise"
            MEAL -> "Meal"
            REST -> "Rest"
            FOCUS -> "Focus Time"
            MEETING -> "Meeting"
            PERSONAL -> "Personal"
            OTHER -> "Other"
        }
    
    companion object {
        /**
         * Convert domain model category to UI enum.
         */
        fun fromDomain(category: com.example.agilelifemanagement.domain.model.ActivityCategory?): ActivityCategoryEnum {
            return when (category?.name?.lowercase()) {
                "work" -> WORK
                "exercise" -> EXERCISE
                "meal" -> MEAL
                "rest" -> REST
                "focus" -> FOCUS
                "meeting" -> MEETING
                "personal" -> PERSONAL
                else -> OTHER
            }
        }
    }
}
