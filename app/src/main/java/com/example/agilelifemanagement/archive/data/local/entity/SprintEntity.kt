package com.example.agilelifemanagement.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Sprint entity for the Room database.
 */
@Entity(
    tableName = "sprints",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("userId")]
)
data class SprintEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val summary: String?,
    val startDate: Long,
    val endDate: Long,
    val isActive: Boolean,
    val isCompleted: Boolean,
    val userId: String,
    val createdAt: Long,
    val updatedAt: Long,
    val description: List<String>? = null // New field for bullet points
)
