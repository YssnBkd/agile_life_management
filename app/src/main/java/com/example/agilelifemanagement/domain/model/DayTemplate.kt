package com.example.agilelifemanagement.domain.model

import java.time.LocalDate
import java.time.LocalTime

/**
 * Domain model representing a template for daily activities.
 * Templates allow users to create reusable patterns of activities
 * that can be applied to future days.
 */
data class DayTemplate(
    val id: String,
    val name: String,
    val description: String = "",
    val activities: List<TemplateActivity> = emptyList(),
    val createdDate: LocalDate = LocalDate.now(),
    val lastUsedDate: LocalDate? = null,
    val useCount: Int = 0
)

/**
 * Represents an activity within a day template.
 */
data class DayTemplateActivity(
    val title: String,
    val description: String = "",
    val categoryId: String? = null,
    val duration: Int = 60, // in minutes
    val startTime: LocalTime? = null,
    val priority: Int = 0
)
