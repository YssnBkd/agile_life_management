package com.example.agilelifemanagement.data.remote.model

import com.example.agilelifemanagement.domain.model.GoalStatus
import kotlinx.serialization.Contextual
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
    @Contextual val startDate: LocalDate,
    @Contextual val targetDate: LocalDate,
    @Contextual val completedDate: LocalDate? = null,
    val progress: Float = 0f,
    val tags: List<String> = emptyList(),
    val sprintIds: List<String> = emptyList(),
    @Contextual val createdDate: LocalDate,
    @Contextual val modifiedDate: LocalDate
)
