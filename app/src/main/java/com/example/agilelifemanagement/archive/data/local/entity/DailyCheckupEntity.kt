package com.example.agilelifemanagement.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * DailyCheckup entity for the Room database.
 */
@Entity(
    tableName = "daily_checkups",
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
data class DailyCheckupEntity(
    @PrimaryKey
    val id: String,
    val date: Long,
    val sprintId: String,
    val userId: String,
    val notes: String?,
    val createdAt: Long,
    val updatedAt: Long
)
