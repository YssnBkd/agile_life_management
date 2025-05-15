package com.example.agilelifemanagement.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Goal entity for the Room database.
 */
@Entity(
    tableName = "goals",
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
data class GoalEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val summary: String?,
    val category: Int?,
    val deadline: Long?,
    val isCompleted: Boolean,
    val userId: String,
    val createdAt: Long,
    val updatedAt: Long,
    val description: List<String>? = null // New field for bullet points
)
