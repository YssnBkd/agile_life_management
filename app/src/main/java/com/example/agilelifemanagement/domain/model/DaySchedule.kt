package com.example.agilelifemanagement.domain.model

import java.time.LocalDate
import java.time.LocalTime

/**
 * Domain model representing a daily schedule.
 * A schedule contains the planned activities for a specific day,
 * including time slots and potential gaps.
 */
data class DaySchedule(
    val id: String,
    val date: LocalDate,
    val activities: List<DayActivity> = emptyList(),
    val startTime: LocalTime = LocalTime.of(8, 0),
    val endTime: LocalTime = LocalTime.of(22, 0),
    val isOptimized: Boolean = false,
    val userId: String = "",
    val notes: String = "",
    val createdDate: LocalDate = LocalDate.now(),
    val modifiedDate: LocalDate = LocalDate.now()
) {
    /**
     * Returns the total planned time in minutes for this schedule.
     */
    val totalPlannedMinutes: Int
        get() = activities.sumOf { it.duration }
        
    /**
     * Returns true if this schedule has conflicts (overlapping activities).
     */
    val hasTimeConflicts: Boolean
        get() {
            val sortedActivities = activities.sortedBy { it.scheduledTime }
            for (i in 0 until sortedActivities.size - 1) {
                val current = sortedActivities[i]
                val next = sortedActivities[i + 1]
                val currentEndTime = current.scheduledTime.plusMinutes(current.duration.toLong())
                if (currentEndTime.isAfter(next.scheduledTime)) {
                    return true
                }
            }
            return false
        }
}
