package com.example.agilelifemanagement.ui.model

import com.example.agilelifemanagement.domain.model.DayActivity
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

/**
 * UI model for Day Activity display
 */
data class DayActivityUi(
    val id: String,
    val title: String,
    val description: String,
    val date: String,
    val startTime: String,
    val endTime: String,
    val isCompleted: Boolean,
    val categoryId: String
)

/**
 * Extension function to convert domain DayActivity to UI model
 */
fun DayActivity.toUiModel(): DayActivityUi {
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    
    val startTimeFormatted = scheduledTime.format(timeFormatter)
    val endTimeFormatted = scheduledTime.plusMinutes(duration.toLong()).format(timeFormatter)
    
    return DayActivityUi(
        id = id,
        title = title,
        description = description,
        date = date.format(dateFormatter),
        startTime = startTimeFormatted,
        endTime = endTimeFormatted,
        isCompleted = completed,
        categoryId = categoryId
    )
}
