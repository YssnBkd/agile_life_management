package com.example.agilelifemanagement.ui.components.tasks

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CalendarToday
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Error
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.agilelifemanagement.domain.model.Task
import com.example.agilelifemanagement.domain.model.TaskPriority as DomainTaskPriority
import com.example.agilelifemanagement.domain.model.TaskStatus
import com.example.agilelifemanagement.ui.components.cards.TaskPriority as UiTaskPriority
import java.time.format.DateTimeFormatter

/**
 * A Material 3 Expressive TaskCard component that displays a task's details.
 * This component properly integrates with the domain Task model.
 *
 * @param task The domain task model to display
 * @param onClick Callback when the card is clicked
 * @param onStatusChange Callback when the task status is changed
 * @param modifier Modifier for the card
 */
@Composable
fun TaskCard(
    task: Task,
    onClick: () -> Unit,
    onStatusChange: (TaskStatus) -> Unit,
    modifier: Modifier = Modifier
) {
    // Map domain model TaskPriority to UI TaskPriority for consistent display
    val uiPriority = when (task.priority) {
        DomainTaskPriority.LOW -> UiTaskPriority.LOW
        DomainTaskPriority.MEDIUM -> UiTaskPriority.MEDIUM
        DomainTaskPriority.HIGH -> UiTaskPriority.HIGH
        DomainTaskPriority.URGENT -> UiTaskPriority.CRITICAL
    }
    
    val isCompleted = task.status == TaskStatus.COMPLETED
    
    // Format due date if present
    val formattedDueDate = task.dueDate?.format(DateTimeFormatter.ofPattern("MMM d"))
    
    var showStatusDropdown by remember { mutableStateOf(false) }
    
    Card(
        onClick = onClick,
        modifier = modifier.animateContentSize(
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isCompleted) 1.dp else 2.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Priority indicator
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(uiPriority.color)
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Task content
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = if (isCompleted) 
                        MaterialTheme.colorScheme.onSurfaceVariant
                    else 
                        MaterialTheme.colorScheme.onSurface
                )
                
                if (task.description.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = task.description,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                // Due date row
                if (formattedDueDate != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.CalendarToday,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        
                        Spacer(modifier = Modifier.width(4.dp))
                        
                        Text(
                            text = formattedDueDate,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                
                // Status chip row
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = MaterialTheme.shapes.small,
                        color = when (task.status) {
                            TaskStatus.TODO -> MaterialTheme.colorScheme.surfaceVariant
                            TaskStatus.IN_PROGRESS -> MaterialTheme.colorScheme.secondaryContainer
                            TaskStatus.COMPLETED -> MaterialTheme.colorScheme.tertiaryContainer
                            TaskStatus.BLOCKED -> MaterialTheme.colorScheme.errorContainer
                        },
                        contentColor = when (task.status) {
                            TaskStatus.TODO -> MaterialTheme.colorScheme.onSurfaceVariant
                            TaskStatus.IN_PROGRESS -> MaterialTheme.colorScheme.onSecondaryContainer
                            TaskStatus.COMPLETED -> MaterialTheme.colorScheme.onTertiaryContainer
                            TaskStatus.BLOCKED -> MaterialTheme.colorScheme.onErrorContainer
                        }
                    ) {
                        Text(
                            text = task.status.name.replace('_', ' '),
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
            
            // Status button
            Box {
                IconButton(onClick = { showStatusDropdown = !showStatusDropdown }) {
                    Icon(
                        imageVector = when (task.status) {
                            TaskStatus.TODO -> Icons.Rounded.Check
                            TaskStatus.IN_PROGRESS -> Icons.Rounded.Check
                            TaskStatus.COMPLETED -> Icons.Rounded.Check
                            TaskStatus.BLOCKED -> Icons.Rounded.Error
                        },
                        contentDescription = "Change status",
                        tint = when (task.status) {
                            TaskStatus.COMPLETED -> MaterialTheme.colorScheme.tertiary
                            TaskStatus.BLOCKED -> MaterialTheme.colorScheme.error
                            else -> MaterialTheme.colorScheme.primary
                        }
                    )
                }
                
                // Status dropdown menu
                DropdownMenu(
                    expanded = showStatusDropdown,
                    onDismissRequest = { showStatusDropdown = false }
                ) {
                    TaskStatus.values().forEach { status ->
                        DropdownMenuItem(
                            text = { 
                                Text(
                                    text = status.name.replace('_', ' '),
                                    style = MaterialTheme.typography.bodyMedium
                                ) 
                            },
                            onClick = { 
                                onStatusChange(status)
                                showStatusDropdown = false
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = when (status) {
                                        TaskStatus.BLOCKED -> Icons.Rounded.Error
                                        else -> Icons.Rounded.Check
                                    },
                                    contentDescription = null,
                                    tint = when (status) {
                                        TaskStatus.TODO -> MaterialTheme.colorScheme.onSurfaceVariant
                                        TaskStatus.IN_PROGRESS -> MaterialTheme.colorScheme.secondary
                                        TaskStatus.COMPLETED -> MaterialTheme.colorScheme.tertiary
                                        TaskStatus.BLOCKED -> MaterialTheme.colorScheme.error
                                    }
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}
