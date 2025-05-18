package com.example.agilelifemanagement.ui.components.fab

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.AddTask
import androidx.compose.material.icons.rounded.DirectionsRun
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.Note
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Represents a quick action that can be triggered from the FAB menu
 */
data class QuickAction(
    val id: String,
    val label: String,
    val icon: ImageVector,
    val description: String = ""
)

/**
 * Predefined quick actions for the app
 */
object QuickActions {
    val CREATE_TASK = QuickAction(
        id = "create_task",
        label = "New Task",
        icon = Icons.Rounded.AddTask,
        description = "Create a new task"
    )
    
    val CREATE_SPRINT = QuickAction(
        id = "create_sprint",
        label = "New Sprint",
        icon = Icons.Rounded.DirectionsRun,
        description = "Create a new sprint"
    )
    
    val PLAN_DAY = QuickAction(
        id = "plan_day",
        label = "Plan Day",
        icon = Icons.Rounded.Add,
        description = "Plan your day"
    )
    
    val CREATE_GOAL = QuickAction(
        id = "create_goal",
        label = "New Goal",
        icon = Icons.Rounded.EmojiEvents,
        description = "Create a new goal"
    )
    
    val CREATE_NOTE = QuickAction(
        id = "create_note",
        label = "Quick Note",
        icon = Icons.Rounded.Note,
        description = "Create a quick note"
    )
    
    val AllActions = listOf(
        CREATE_TASK,
        CREATE_SPRINT,
        PLAN_DAY,
        CREATE_GOAL,
        CREATE_NOTE
    )
}
