package com.example.agilelifemanagement.ui.screens.dashboard

import com.example.agilelifemanagement.ui.components.cards.TaskPriority

/**
 * UI model for displaying task information in list and dashboard views.
 */
data class TaskInfo(
    val id: String,
    val title: String,
    val description: String = "",
    val priority: TaskPriority = TaskPriority.MEDIUM,
    val dueDate: String? = null,
    val isCompleted: Boolean = false,
    val estimatedMinutes: Int = 0,
    val relatedSprintId: String? = null,
    val tagIds: List<String> = emptyList()
)
