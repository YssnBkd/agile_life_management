package com.example.agilelifemanagement.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * SprintReview entity for the Room database.
 */
@Entity(
    tableName = "sprint_reviews",
    foreignKeys = [
        ForeignKey(
            entity = SprintEntity::class,
            parentColumns = ["id"],
            childColumns = ["sprintId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("sprintId"), Index("userId")]
)
data class SprintReviewEntity(
    @PrimaryKey
    val id: String,
    val sprintId: String,
    val date: Long,
    val rating: Int,
    val userId: String,
    val createdAt: Long,
    val updatedAt: Long
)
