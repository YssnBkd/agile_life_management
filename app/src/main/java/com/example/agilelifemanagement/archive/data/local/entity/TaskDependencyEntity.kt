package com.example.agilelifemanagement.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.OffsetDateTime

@Entity(tableName = "task_dependencies")
data class TaskDependencyEntity(
    @PrimaryKey val id: String,
    val taskId: String,
    val dependsOnTaskId: String,
    val createdAt: OffsetDateTime
)
