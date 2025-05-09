package com.example.agilelifemanagement.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.OffsetDateTime

@Entity(tableName = "task_goal_cross_refs")
data class TaskGoalCrossRefEntity(
    @PrimaryKey val id: String,
    val taskId: String,
    val goalId: String,
    val createdAt: OffsetDateTime
)
