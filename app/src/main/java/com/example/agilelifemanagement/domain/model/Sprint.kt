package com.example.agilelifemanagement.domain.model

import java.time.LocalDate

/**
 * Domain model representing a sprint in the AgileLifeManagement app.
 * A sprint is a time-boxed period during which specific tasks are completed.
 */
data class Sprint(
    val id: String,
    val name: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val goals: List<String>,
    val status: SprintStatus
)
