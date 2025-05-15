package com.example.agilelifemanagement.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.agilelifemanagement.domain.model.SprintStatus
import java.time.LocalDate

/**
 * Room entity representing a sprint in the local database.
 * Sprints are time-boxed periods for completing tasks.
 */
@Entity(tableName = "sprints")
data class SprintEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val goals: List<String>, // Stored as JSON string via TypeConverter
    val status: SprintStatus
)
