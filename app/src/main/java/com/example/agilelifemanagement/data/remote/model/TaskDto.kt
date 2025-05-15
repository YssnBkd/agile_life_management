package com.example.agilelifemanagement.data.remote.model

import com.example.agilelifemanagement.domain.model.TaskStatus
import kotlinx.serialization.Serializable
import java.time.LocalDate

/**
 * Data Transfer Object for Task entities in remote API communication.
 */
@Serializable
data class TaskDto(
    val id: String = "",
    val title: String,
    val description: String = "",
    val status: TaskStatus,
    val dueDate: LocalDate? = null,
    val createdDate: LocalDate,
    val modifiedDate: LocalDate,
    val sprintId: String? = null,
    val tags: List<String> = emptyList(),
    val priority: Int = 0,
    val estimatedHours: Float? = null,
    val actualHours: Float? = null
)
