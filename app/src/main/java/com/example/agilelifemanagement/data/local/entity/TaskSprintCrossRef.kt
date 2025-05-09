package com.example.agilelifemanagement.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

/**
 * Junction entity for the many-to-many relationship between Task and Sprint.
 */
@Entity(
    tableName = "task_sprint_cross_refs",
    primaryKeys = ["taskId", "sprintId"],
    foreignKeys = [
        ForeignKey(
            entity = TaskEntity::class,
            parentColumns = ["id"],
            childColumns = ["taskId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = SprintEntity::class,
            parentColumns = ["id"],
            childColumns = ["sprintId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("taskId"), Index("sprintId")]
)
data class TaskSprintCrossRef(
    val taskId: String,
    val sprintId: String
)
