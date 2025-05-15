package com.example.agilelifemanagement.data.local.entity

import androidx.room.Entity
import androidx.room.Index

/**
 * Room entity representing the many-to-many relationship between tasks and tags.
 * This is a junction table that associates tasks with their tags.
 */
@Entity(
    tableName = "task_tag_cross_ref",
    primaryKeys = ["taskId", "tagId"],
    indices = [
        Index(value = ["taskId"]),
        Index(value = ["tagId"])
    ]
)
data class TaskTagCrossRef(
    val taskId: String,
    val tagId: String
)
