package com.example.agilelifemanagement.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDate

/**
 * Room entity representing a daily wellness checkup in the local database.
 * Used to track daily wellness metrics and self-assessment.
 */
@Entity(
    tableName = "daily_checkups",
    indices = [Index("date", unique = true)]
)
data class DailyCheckupEntity(
    @PrimaryKey
    val id: String,
    val date: LocalDate,
    val moodRating: Int, // Scale typically 1-5 or 1-10
    val sleepQuality: Int, // Scale typically 1-5 or 1-10
    val stressLevel: Int, // Scale typically 1-5 or 1-10
    val energyLevel: Int, // Scale typically 1-5 or 1-10
    val notes: String,
    val updatedAt: Long = System.currentTimeMillis() // Timestamp for syncing purposes
)
