package com.example.agilelifemanagement.data.remote.model

import com.example.agilelifemanagement.domain.model.GoalStatus
import kotlinx.serialization.Serializable
import java.time.LocalDate

/**
 * Data Transfer Object for Goal entities in remote API communication.
 */
@Serializable
data class GoalDto(
    val id: String = "",
    val title: String,
    val description: String = "",
    val status: GoalStatus,
    val startDate: LocalDate,
    val targetDate: LocalDate,
    val completedDate: LocalDate? = null,
    val progress: Float = 0f,
    val tags: List<String> = emptyList(),
    val sprintIds: List<String> = emptyList(),
    val createdDate: LocalDate,
    val modifiedDate: LocalDate
)
