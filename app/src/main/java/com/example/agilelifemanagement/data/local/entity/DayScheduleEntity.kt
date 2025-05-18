package com.example.agilelifemanagement.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.agilelifemanagement.data.local.converter.Converters
import java.time.LocalDate
import java.time.LocalTime

/**
 * Entity class for day schedules in the local database.
 */
@Entity(tableName = "day_schedules")
@TypeConverters(Converters::class)
data class DayScheduleEntity(
    @PrimaryKey
    val id: String,
    val date: LocalDate,
    val startTime: LocalTime,
    val endTime: LocalTime,
    val isOptimized: Boolean,
    val userId: String,
    val notes: String,
    val createdDate: LocalDate,
    val modifiedDate: LocalDate
    // Activities are handled through a one-to-many relationship and fetched separately
)
