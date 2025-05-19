package com.example.agilelifemanagement.domain.model

import java.time.LocalDate

/**
 * Represents a sprint in the Agile Life Management system.
 */
data class Sprint(
    val id: String,
    val name: String,
    val goal: String,
    val description: String,
    val status: SprintStatus,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val progress: Int, // 0-100
    val taskCount: Int,
    val completedTaskCount: Int,
    val createdDate: LocalDate
)

// SprintStatus is now defined in SprintStatus.kt
