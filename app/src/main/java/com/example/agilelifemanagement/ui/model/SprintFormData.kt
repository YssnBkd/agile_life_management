package com.example.agilelifemanagement.ui.model

import com.example.agilelifemanagement.domain.model.SprintStatus
import java.time.LocalDate

/**
 * Represents the data in the sprint editor form.
 * Used for creating and editing sprints.
 */
data class SprintFormData(
    val name: String,
    val goal: String,
    val description: String,
    val status: SprintStatus,
    val startDate: LocalDate,
    val endDate: LocalDate
)
