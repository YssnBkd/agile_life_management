package com.example.agilelifemanagement.ui.model

import com.example.agilelifemanagement.domain.model.Task
import com.example.agilelifemanagement.domain.model.TaskStatus
import com.example.agilelifemanagement.ui.components.cards.TaskPriority
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * UI model for Task information display
 */
data class TaskInfo(
    val id: String,
    val title: String,
    val description: String,
    val dueDate: String?,
    val priority: TaskPriority,
    val isCompleted: Boolean,
    val sprintId: String?
)

/**
 * Extension function to convert domain Task to UI TaskInfo model
 */
fun Task.toUiModel(): TaskInfo {
    val dateFormatter = DateTimeFormatter.ofPattern("MMM d, yyyy")
    
    // Map domain model priority to UI model priority using fully qualified names
    val uiPriority = when (priority) {
        com.example.agilelifemanagement.domain.model.TaskPriority.LOW -> 
            com.example.agilelifemanagement.ui.components.cards.TaskPriority.LOW
        com.example.agilelifemanagement.domain.model.TaskPriority.MEDIUM -> 
            com.example.agilelifemanagement.ui.components.cards.TaskPriority.MEDIUM
        com.example.agilelifemanagement.domain.model.TaskPriority.HIGH -> 
            com.example.agilelifemanagement.ui.components.cards.TaskPriority.HIGH
        com.example.agilelifemanagement.domain.model.TaskPriority.URGENT -> 
            com.example.agilelifemanagement.ui.components.cards.TaskPriority.CRITICAL
    }
    
    return TaskInfo(
        id = id,
        title = title,
        description = description,
        dueDate = dueDate?.format(dateFormatter),
        priority = uiPriority,
        isCompleted = status == TaskStatus.COMPLETED,
        sprintId = sprintId
    )
}
