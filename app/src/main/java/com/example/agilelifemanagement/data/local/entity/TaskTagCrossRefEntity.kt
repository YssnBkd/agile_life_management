package com.example.agilelifemanagement.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.OffsetDateTime

@Entity(tableName = "task_tag_cross_refs")
data class TaskTagCrossRefEntity(
    @PrimaryKey val id: String,
    val taskId: String,
    val tagId: String,
    val createdAt: OffsetDateTime
)
