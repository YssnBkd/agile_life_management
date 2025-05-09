package com.example.agilelifemanagement.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.OffsetDateTime

@Entity(tableName = "goal_sprint_cross_refs")
data class GoalSprintCrossRefEntity(
    @PrimaryKey val id: String,
    val goalId: String,
    val sprintId: String,
    val createdAt: OffsetDateTime
)
