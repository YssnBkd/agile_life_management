package com.example.agilelifemanagement.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDate

/**
 * Room entity representing a sprint review in the local database.
 * Sprint reviews capture the outcomes and learnings from completed sprints.
 */
@Entity(
    tableName = "sprint_reviews",
    foreignKeys = [
        ForeignKey(
            entity = SprintEntity::class,
            parentColumns = ["id"],
            childColumns = ["sprintId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("sprintId", unique = true)]
)
data class SprintReviewEntity(
    @PrimaryKey
    val id: String,
    val sprintId: String,
    val completionRate: Float,
    val lessonsLearned: List<String>, // Stored as JSON string via TypeConverter
    val date: LocalDate
)
