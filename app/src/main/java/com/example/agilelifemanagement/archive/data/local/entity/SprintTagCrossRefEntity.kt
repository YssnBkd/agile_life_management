package com.example.agilelifemanagement.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.OffsetDateTime

@Entity(tableName = "sprint_tag_cross_refs")
data class SprintTagCrossRefEntity(
    @PrimaryKey val id: String,
    val sprintId: String,
    val tagId: String,
    val createdAt: OffsetDateTime
)
