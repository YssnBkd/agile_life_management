package com.example.agilelifemanagement.domain.model

import java.time.LocalDate

/**
 * Domain model representing analytics and insights generated from user's wellness data.
 * Used to show trends and patterns in the user's wellness metrics over time.
 */
data class WellnessAnalytics(
    val averageMood: Float,
    val averageSleepQuality: Float,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val metrics: Map<String, Float> = emptyMap() // Flexible map for additional metrics
)
