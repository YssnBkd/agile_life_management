package com.example.agilelifemanagement.data.remote.model

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.time.LocalTime

/**
 * Data Transfer Object for DayActivity entities in remote API communication.
 */
@Serializable
data class DayActivityDto(
    val id: String = "",
    val title: String,
    val description: String = "",
    @Contextual val date: LocalDate,
    @Contextual val startTime: LocalTime? = null,
    @Contextual val endTime: LocalTime? = null,
    val isCompleted: Boolean = false,
    val categoryId: String? = null,
    val priority: Int = 0,
    @Contextual val createdDate: LocalDate,
    @Contextual val modifiedDate: LocalDate
)
