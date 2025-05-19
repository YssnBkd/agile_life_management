package com.example.agilelifemanagement.ui.components.cards

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccessTime
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.agilelifemanagement.ui.theme.AgileLifeTheme

/**
 * TaskCard displays a task with completion checkbox, title, description,
 * priority indicator, and due date following Material 3 Expressive design principles.
 */
@Composable
fun TaskCard(
    title: String,
    description: String? = null,
    priority: TaskPriority,
    dueDate: String? = null,
    isCompleted: Boolean,
    estimatedMinutes: Int,
    onClick: () -> Unit,
    onCompletedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Completion checkbox
            Checkbox(
                checked = isCompleted,
                onCheckedChange = onCompletedChange,
                colors = CheckboxDefaults.colors(
                    checkedColor = priority.color,
                    uncheckedColor = priority.color.copy(alpha = 0.5f)
                )
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            // Task content
            Column(
                modifier = Modifier.weight(1f)
            ) {
                // Task title
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    textDecoration = if (isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                    color = if (isCompleted) 
                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    else 
                        MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                if (description != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    // Task description
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        textDecoration = if (isCompleted) TextDecoration.LineThrough else TextDecoration.None
                    )
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // Task metadata row
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Time estimate
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.AccessTime,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            modifier = Modifier.height(16.dp)
                        )
                        
                        Spacer(modifier = Modifier.width(4.dp))
                        
                        Text(
                            text = "${estimatedMinutes}m",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    }
                    
                    if (dueDate != null) {
                        Spacer(modifier = Modifier.width(12.dp))
                        
                        // Due date
                        Text(
                            text = dueDate,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TaskCardPreview() {
    AgileLifeTheme {
        TaskCard(
            title = "Complete project proposal",
            description = "Draft and send the project proposal to the client",
            priority = TaskPriority.HIGH,
            dueDate = "Today, 17:00",
            isCompleted = false,
            estimatedMinutes = 120,
            onClick = { },
            onCompletedChange = { }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CompletedTaskCardPreview() {
    AgileLifeTheme {
        TaskCard(
            title = "Morning team standup",
            description = null,
            priority = TaskPriority.MEDIUM,
            dueDate = "9:00 AM",
            isCompleted = true,
            estimatedMinutes = 30,
            onClick = { },
            onCompletedChange = { }
        )
    }
}
