package com.example.agilelifemanagement.domain.model

import androidx.compose.ui.graphics.Color
import java.time.LocalDate

/**
 * Domain model for DayTemplate.
 * Represents a reusable template with scheduled activities for planning days.
 */
data class DayTemplate(
    val id: String = "",
    val name: String,
    val description: String = "",
    val activities: List<TemplateActivity> = emptyList(),
    val createdDate: LocalDate = LocalDate.now(),
    val lastUsedDate: LocalDate? = null,
    val useCount: Int = 0
)

/**
 * Domain model for a single activity within a day template.
 */
data class TemplateActivity(
    val id: String = "",
    val title: String,
    val startTime: String,  // Format: "HH:MM"
    val durationMinutes: Int,
    val categoryId: String? = null
)

/**
 * Domain model for activity categories with color coding.
 */
data class ActivityCategory(
    val id: String = "",
    val name: String,
    val color: Int, // Store as ARGB Int for database compatibility
    val isDefault: Boolean = false
)

/**
 * Domain model for a day schedule containing concrete activities.
 */
data class DaySchedule(
    val id: String = "",
    val date: LocalDate,
    val activities: List<DayActivity> = emptyList(),
    val notes: String = ""
)

/**
 * Domain model for a concrete activity scheduled on a specific day.
 */
data class DayActivity(
    val id: String = "",
    val title: String,
    val startTime: String,
    val endTime: String? = null,
    val description: String? = null,
    val location: String? = null,
    val categoryId: String? = null,
    val completed: Boolean = false,
    val templateActivityId: String? = null // Reference to template if created from template
)
