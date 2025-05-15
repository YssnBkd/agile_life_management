package com.example.agilelifemanagement.domain.model

import java.time.LocalDate

/**
 * Domain model representing a goal in the AgileLifeManagement app.
 * Goals represent strategic objectives that a user wants to achieve.
 */
data class Goal(
    val id: String,
    val title: String,
    val description: String,
    val deadline: LocalDate?,
    val status: GoalStatus,
    val priority: GoalPriority
)
