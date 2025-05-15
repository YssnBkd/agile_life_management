package com.example.agilelifemanagement.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Room entity representing a day activity template in the local database.
 * Templates are reusable activity definitions that can be scheduled multiple times.
 */
@Entity(
    tableName = "day_activity_templates",
    foreignKeys = [
        ForeignKey(
            entity = ActivityCategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("categoryId")]
)
data class DayActivityTemplateEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val description: String,
    val defaultDuration: Int, // Duration in minutes
    val categoryId: String?
)
