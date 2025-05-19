package com.example.agilelifemanagement.ui.screens.task.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.agilelifemanagement.ui.model.TaskFilterType

/**
 * Displays a summary of displayed vs. total tasks.
 */
@Composable
fun TaskCountSummary(
    displayedCount: Int,
    totalCount: Int,
    filterType: TaskFilterType,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = "Showing ",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Text(
            text = "$displayedCount",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        
        Text(
            text = " of ",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Text(
            text = "$totalCount",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Text(
            text = " tasks",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.width(8.dp))
        
        // Filter info
        Text(
            text = "(${getFilterTypeDescription(filterType)})",
            style = MaterialTheme.typography.bodySmall,
            fontStyle = FontStyle.Italic,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
        )
    }
}

private fun getFilterTypeDescription(filterType: TaskFilterType): String {
    return when (filterType) {
        TaskFilterType.ALL -> "All tasks"
        TaskFilterType.ACTIVE -> "Active tasks"
        TaskFilterType.TODAY -> "Due today"
        TaskFilterType.UPCOMING -> "Upcoming tasks"
        TaskFilterType.COMPLETED -> "Completed tasks"
        TaskFilterType.OVERDUE -> "Overdue tasks"
    }
}
