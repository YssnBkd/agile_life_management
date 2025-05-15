package com.example.agilelifemanagement.domain.model

import java.time.LocalDate

/**
 * Domain model representing a daily wellness checkup in the AgileLifeManagement app.
 * Used to track user's daily wellness metrics and reflections.
 */
data class DailyCheckup(
    val id: String,
    val date: LocalDate,
    val moodRating: Int, // Scale typically 1-5 or 1-10
    val sleepQuality: Int, // Scale typically 1-5 or 1-10
    val stressLevel: Int, // Scale typically 1-5 or 1-10
    val energyLevel: Int, // Scale typically 1-5 or 1-10
    val notes: String
)
