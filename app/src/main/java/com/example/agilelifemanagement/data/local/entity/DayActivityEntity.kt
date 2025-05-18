package com.example.agilelifemanagement.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalTime

/**
 * Room entity representing a day activity in the local database.
 * Day activities are scheduled activities for specific days with time slots.
 * 
 * This entity follows Material 3 Expressive design principles by supporting
 * proper categorization with visual cues through the category relationship.
 */
@Entity(
    tableName = "day_activities",
    foreignKeys = [
        ForeignKey(
            entity = ActivityCategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("categoryId"), Index("date")]
)
data class DayActivityEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val description: String,
    val date: LocalDate,
    val scheduledTime: LocalTime,
    val duration: Int, // Duration in minutes
    val completed: Boolean,
    val categoryId: String?
)
