package com.example.agilelifemanagement.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Task entity for the Room database.
 */
@Entity(
    tableName = "tasks",
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
data class TaskEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val summary: String?,
    val dueDate: Long?,
    val priority: Int,
    val status: Int,
    val estimatedEffort: Int?,
    val actualEffort: Int?,
    val isRecurring: Boolean,
    val recurringPattern: String?,
    val userId: String,
    val createdAt: Long,
    val updatedAt: Long,
    val description: List<String>? = null // New field for bullet points
)
