package com.example.agilelifemanagement.ui.model

import com.example.agilelifemanagement.domain.model.Goal
import com.example.agilelifemanagement.domain.model.GoalPriority
import com.example.agilelifemanagement.domain.model.GoalStatus
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * UI model for Goal display
 */
data class GoalUi(
    val id: String,
    val title: String,
    val description: String,
    val targetDate: String?,
    val status: GoalStatus,
    val priority: GoalPriority
)

/**
 * Extension function to convert domain Goal to UI model
 */
fun Goal.toUiModel(): GoalUi {
    val dateFormatter = DateTimeFormatter.ofPattern("MMM d, yyyy")
    
    return GoalUi(
        id = id,
        title = title,
        description = description,
        targetDate = deadline?.format(dateFormatter),
        status = status,
        priority = priority
    )
}
