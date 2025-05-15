package com.example.agilelifemanagement.data.remote.model

import kotlinx.serialization.Serializable
import java.time.LocalDate

/**
 * Data Transfer Object for DailyCheckup entities in remote API communication.
 */
@Serializable
data class DailyCheckupDto(
    val id: String = "",
    val date: LocalDate,
    val mood: Int,
    val energyLevel: Int,
    val sleepQuality: Int,
    val sleepHours: Float,
    val stressLevel: Int,
    val productivityRating: Int,
    val notes: String = "",
    val focusRating: Int,
    val physicalActivityMinutes: Int,
    val createdDate: LocalDate,
    val modifiedDate: LocalDate
)
