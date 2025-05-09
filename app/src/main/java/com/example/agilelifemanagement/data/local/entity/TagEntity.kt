package com.example.agilelifemanagement.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Tag entity for the Room database.
 */
@Entity(
    tableName = "tags",
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
data class TagEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val color: String,
    val userId: String,
    val createdAt: Long
)
