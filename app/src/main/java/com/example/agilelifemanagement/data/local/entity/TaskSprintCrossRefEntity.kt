package com.example.agilelifemanagement.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.OffsetDateTime

@Entity(tableName = "task_sprint_cross_refs")
data class TaskSprintCrossRefEntity(
    @PrimaryKey val id: String,
    val taskId: String,
    val sprintId: String,
    val createdAt: OffsetDateTime
)
