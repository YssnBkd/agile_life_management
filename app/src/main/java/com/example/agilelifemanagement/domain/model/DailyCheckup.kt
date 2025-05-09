package com.example.agilelifemanagement.domain.model

import java.time.LocalDate

/**
 * Domain model for DailyCheckup.
 * Represents a daily check-up for tracking progress and identifying blockers.
 */
data class DailyCheckup(
    val id: String = "",
    val date: LocalDate,
    val sprintId: String,
    val accomplishments: List<String> = emptyList(),
    val plannedTasks: List<String> = emptyList(),
    val blockers: List<String> = emptyList(),
    val notes: String = ""
)
