package com.example.agilelifemanagement.data.remote.model

import kotlinx.serialization.Serializable
import java.time.LocalDate

/**
 * Data Transfer Object for Sprint entities in remote API communication.
 */
@Serializable
data class SprintDto(
    val id: String = "",
    val name: String,
    val description: String = "",
    val startDate: LocalDate,
    val endDate: LocalDate,
    val goal: String = "",
    val isActive: Boolean = false,
    val createdDate: LocalDate,
    val modifiedDate: LocalDate
)

/**
 * Data Transfer Object for Sprint Review entities in remote API communication.
 */
@Serializable
data class SprintReviewDto(
    val id: String = "",
    val sprintId: String,
    val completionPercentage: Float,
    val accomplishments: List<String> = emptyList(),
    val challenges: List<String> = emptyList(),
    val lessonsLearned: List<String> = emptyList(),
    val actionItems: List<String> = emptyList(),
    val notes: String = "",
    val createdDate: LocalDate,
    val modifiedDate: LocalDate
)
