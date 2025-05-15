package com.example.agilelifemanagement.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.OffsetDateTime

@Entity(tableName = "goal_tag_cross_refs")
data class GoalTagCrossRefEntity(
    @PrimaryKey val id: String,
    val goalId: String,
    val tagId: String,
    val createdAt: OffsetDateTime
)
