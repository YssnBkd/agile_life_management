package com.example.agilelifemanagement.ui.screens.task.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.List
import androidx.compose.material.icons.outlined.WatchLater
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.agilelifemanagement.ui.model.TaskFilterType

/**
 * Empty state view for task lists.
 */
@Composable
fun EmptyTasksView(
    filterType: TaskFilterType,
    onAddClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Icon based on filter type
            Icon(
                imageVector = getEmptyStateIcon(filterType),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.size(80.dp)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Title
            Text(
                text = getEmptyStateTitle(filterType),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Message
            Text(
                text = getEmptyStateMessage(filterType),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Add button
            Button(
                onClick = onAddClick,
                modifier = Modifier
            ) {
                Icon(
                    imageVector = Icons.Outlined.List,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text("Add Task")
            }
        }
    }
}

private fun getEmptyStateIcon(filterType: TaskFilterType): ImageVector {
    return when (filterType) {
        TaskFilterType.ALL -> Icons.Outlined.List
        TaskFilterType.ACTIVE -> Icons.Outlined.List
        TaskFilterType.TODAY -> Icons.Outlined.DateRange
        TaskFilterType.UPCOMING -> Icons.Outlined.WatchLater
        TaskFilterType.COMPLETED -> Icons.Outlined.CheckCircle
        TaskFilterType.OVERDUE -> Icons.Outlined.Warning
    }
}

private fun getEmptyStateTitle(filterType: TaskFilterType): String {
    return when (filterType) {
        TaskFilterType.ALL -> "No Tasks Yet"
        TaskFilterType.ACTIVE -> "No Active Tasks"
        TaskFilterType.TODAY -> "No Tasks Due Today"
        TaskFilterType.UPCOMING -> "No Upcoming Tasks"
        TaskFilterType.COMPLETED -> "No Completed Tasks"
        TaskFilterType.OVERDUE -> "No Overdue Tasks"
    }
}

private fun getEmptyStateMessage(filterType: TaskFilterType): String {
    return when (filterType) {
        TaskFilterType.ALL -> "Add your first task to get started with your agile journey."
        TaskFilterType.ACTIVE -> "You have no active tasks to work on."
        TaskFilterType.TODAY -> "You have no tasks scheduled for today."
        TaskFilterType.UPCOMING -> "You have no upcoming tasks scheduled."
        TaskFilterType.COMPLETED -> "You haven't completed any tasks yet."
        TaskFilterType.OVERDUE -> "Great job! You have no overdue tasks."
    }
}
