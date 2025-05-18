package com.example.agilelifemanagement.domain.model

import java.time.LocalDate
import java.time.LocalDateTime

/**
 * Helper models for Goal entity in the AgileLifeManagement app.
 * The primary Goal model is defined in Goal.kt
 */

/**
 * Represents a milestone within a goal.
 */
data class GoalMilestone(
    val id: String,
    val goalId: String,
    val title: String,
    val dueDate: LocalDate? = null,
    val isCompleted: Boolean = false,
    val completedAt: LocalDateTime? = null
)

/**
 * Represents a category for goals.
 */
data class GoalCategory(
    val id: String,
    val name: String,
    val color: String = "#6750A4" // Default to primary Material You color
)

// GoalPriority enum is defined in GoalPriority.kt
