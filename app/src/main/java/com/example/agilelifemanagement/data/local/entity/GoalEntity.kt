package com.example.agilelifemanagement.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.agilelifemanagement.domain.model.GoalPriority
import com.example.agilelifemanagement.domain.model.GoalStatus
import java.time.LocalDate

/**
 * Room entity representing a goal in the local database.
 * Goals represent strategic objectives for users to track.
 */
@Entity(tableName = "goals")
data class GoalEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val description: String,
    val deadline: LocalDate?,
    val status: GoalStatus,
    val priority: GoalPriority
)
