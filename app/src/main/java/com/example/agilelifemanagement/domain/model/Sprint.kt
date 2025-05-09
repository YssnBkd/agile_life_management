package com.example.agilelifemanagement.domain.model

import java.time.LocalDate

/**
 * Domain model for Sprint.
 * Represents an agile sprint in the application.
 */
data class Sprint(
    val id: String = "",
    val name: String,
    val summary: String = "",
    val description: List<String> = emptyList(),
    val startDate: LocalDate,
    val endDate: LocalDate,
    val isActive: Boolean = false,
    val isCompleted: Boolean = false
)
