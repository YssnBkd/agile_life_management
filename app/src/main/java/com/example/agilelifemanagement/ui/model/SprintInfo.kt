package com.example.agilelifemanagement.ui.model

import com.example.agilelifemanagement.domain.model.Sprint
import com.example.agilelifemanagement.domain.model.Task
import com.example.agilelifemanagement.domain.model.TaskStatus
import com.example.agilelifemanagement.ui.components.cards.SprintStatus

/**
 * UI model for displaying Sprint information on the dashboard and other screens
 */
data class SprintInfo(
    val id: String,
    val name: String,
    val dateRange: String,
    val status: SprintStatus,
    val progressPercent: Float,
    val tasksCompleted: Int,
    val totalTasks: Int
)

/**
 * Convert domain Sprint model to UI SprintInfo model
 */
fun Sprint.toSprintInfo(tasks: List<Task>): SprintInfo {
    val totalTasks = tasks.size
    val completedTasks = tasks.count { it.status == TaskStatus.COMPLETED }
    val progressPercent = if (totalTasks > 0) completedTasks.toFloat() / totalTasks else 0f
    
    // Map domain model status to UI model status
    val uiStatus = when (status) {
        com.example.agilelifemanagement.domain.model.SprintStatus.COMPLETED -> 
            com.example.agilelifemanagement.ui.components.cards.SprintStatus.COMPLETED
        com.example.agilelifemanagement.domain.model.SprintStatus.ACTIVE -> 
            com.example.agilelifemanagement.ui.components.cards.SprintStatus.ACTIVE
        com.example.agilelifemanagement.domain.model.SprintStatus.PLANNED -> 
            com.example.agilelifemanagement.ui.components.cards.SprintStatus.PLANNED
    }
    
    return SprintInfo(
        id = id,
        name = name,
        dateRange = "$startDate - $endDate",
        status = uiStatus,
        progressPercent = progressPercent,
        tasksCompleted = completedTasks,
        totalTasks = totalTasks
    )
}
