package com.example.agilelifemanagement.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalTime

/**
 * Room entity for day activities.
 */
@Entity(
    tableName = "day_activities",
    indices = [
        Index("date"),
        Index("categoryId")
    ],
    foreignKeys = [
        ForeignKey(
            entity = ActivityCategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.SET_NULL
        )
    ]
)
data class DayActivityEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val description: String,
    val date: LocalDate,
    val startTime: LocalTime,
    val endTime: LocalTime,
    val isCompleted: Boolean = false,
    val priority: Int = 0,
    val categoryId: String? = null,
    val syncedAt: Long? = null
)
