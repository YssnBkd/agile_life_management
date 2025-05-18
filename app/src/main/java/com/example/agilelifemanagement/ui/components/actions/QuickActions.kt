package com.example.agilelifemanagement.ui.components.actions

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Dashboard
import androidx.compose.material.icons.rounded.DateRange
import androidx.compose.material.icons.rounded.Task
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

/**
 * Represents different quick actions available from the dashboard
 */
enum class QuickActions(
    val id: String,
    val title: String,
    val icon: ImageVector,
    val description: String
) {
    CREATE_TASK(
        id = "create_task",
        title = "New Task",
        icon = Icons.Rounded.Task,
        description = "Create a new task"
    ),
    CREATE_SPRINT(
        id = "create_sprint",
        title = "New Sprint",
        icon = Icons.Rounded.DateRange,
        description = "Start a new sprint"
    ),
    PLAN_DAY(
        id = "plan_day",
        title = "Plan Day",
        icon = Icons.Rounded.Dashboard,
        description = "Plan your day schedule"
    ),
    CREATE_GOAL(
        id = "create_goal",
        title = "New Goal",
        icon = Icons.Rounded.Add,
        description = "Create a new goal"
    );
    
    companion object {
        /**
         * Get all quick actions as a list
         */
        val AllActions: List<com.example.agilelifemanagement.ui.components.fab.QuickAction> = values().map { it.toQuickAction() }
    }
    
    /**
     * Convert this enum to a QuickAction data class instance
     */
    fun toQuickAction(): com.example.agilelifemanagement.ui.components.fab.QuickAction {
        return com.example.agilelifemanagement.ui.components.fab.QuickAction(
            id = this.id,
            label = this.title,
            icon = this.icon,
            description = this.description
        )
    }
}

/**
 * Displays a quick action item with an icon and label
 */
@Composable
fun QuickActionItem(
    action: QuickActions,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = action.icon,
            contentDescription = action.description,
            tint = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.width(8.dp))
        
        Column {
            Text(
                text = action.title,
                style = MaterialTheme.typography.labelLarge
            )
            
            Spacer(modifier = Modifier.height(2.dp))
            
            Text(
                text = action.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
