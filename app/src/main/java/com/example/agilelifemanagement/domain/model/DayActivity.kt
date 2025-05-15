package com.example.agilelifemanagement.domain.model

import java.time.LocalDate
import java.time.LocalTime

/**
 * Domain model representing a day activity in the AgileLifeManagement app.
 * Activities are scheduled tasks for a specific day with a time slot.
 */
data class DayActivity(
    val id: String,
    val title: String,
    val description: String,
    val date: LocalDate,
    val scheduledTime: LocalTime,
    val duration: Int, // Duration in minutes
    val completed: Boolean,
    val categoryId: String
)
