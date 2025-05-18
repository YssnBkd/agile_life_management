package com.example.agilelifemanagement.domain.model

import androidx.compose.ui.graphics.Color

/**
 * Enum representing the different categories for time blocks.
 * Each category has a name and an associated color.
 * 
 * This enum follows Material 3 Expressive design principles by 
 * providing consistent color theming for different categories.
 */
enum class TimeBlockCategory(val color: Color) {
    TASK(Color(0xFF6750A4)), // Primary Material 3 color
    MEETING(Color(0xFF7F67BE)),
    PERSONAL(Color(0xFFEF6C00)),
    HEALTH(Color(0xFF4CAF50)),
    EXERCISE(Color(0xFF03A9F4)),
    MEAL(Color(0xFFFF5722)),
    COMMUTE(Color(0xFF795548)),
    WORK(Color(0xFF3949AB)),
    EDUCATION(Color(0xFF9C27B0)),
    LEISURE(Color(0xFF009688));
    
    companion object {
        /**
         * Get a category by its name, case insensitive.
         * Returns TASK if no match is found.
         */
        fun fromName(name: String): TimeBlockCategory {
            return values().find { it.name.equals(name, ignoreCase = true) } ?: TASK
        }
    }
}
