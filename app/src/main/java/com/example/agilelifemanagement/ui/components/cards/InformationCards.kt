package com.example.agilelifemanagement.ui.components.cards

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDownward
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.agilelifemanagement.ui.theme.AgileLifeTheme

/**
 * SprintCard displays sprint information with Material 3 Expressive design
 * 
 * Features:
 * - Vibrant status indicators
 * - Visual progress display
 * - Animated expansion for details
 * - Generous padding and spacing
 */
@Composable
fun SprintCard(
    title: String,
    status: SprintStatus,
    progressPercent: Float,
    dateRange: String,
    tasksCompleted: Int,
    totalTasks: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val arrowRotation by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        label = "arrowRotation"
    )
    
    ExpressiveCard(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick,
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        elevation = 2.dp
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Status indicator
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(status.color)
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // Date range row
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Rounded.CalendarMonth,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Spacer(modifier = Modifier.width(4.dp))
                    
                    Text(
                        text = dateRange,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            // Status chip
            Box(
                modifier = Modifier
                    .clip(MaterialTheme.shapes.small)
                    .background(status.containerColor)
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = status.label,
                    style = MaterialTheme.typography.labelMedium,
                    color = status.textColor
                )
            }
            
            // Expand/collapse button
            IconButton(
                onClick = { expanded = !expanded }
            ) {
                Icon(
                    imageVector = Icons.Rounded.KeyboardArrowDown,
                    contentDescription = if (expanded) "Collapse" else "Expand",
                    modifier = Modifier.rotate(arrowRotation)
                )
            }
        }
        
        // Progress bar
        LinearProgressIndicator(
            progress = { progressPercent },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .height(8.dp)
                .clip(MaterialTheme.shapes.small),
            color = status.color,
            trackColor = MaterialTheme.colorScheme.surfaceContainerHighest
        )
        
        // Expandable details
        AnimatedVisibility(
            visible = expanded,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                // Task completion status
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Rounded.CheckCircle,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = AgileLifeTheme.extendedColors.accentMint
                    )
                    
                    Spacer(modifier = Modifier.width(4.dp))
                    
                    Text(
                        text = "$tasksCompleted of $totalTasks tasks completed",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

/**
 * TaskCard displays task information with Material 3 Expressive design
 */
@Composable
fun TaskCard(
    title: String,
    priority: TaskPriority,
    dueDate: String?,
    estimatedMinutes: Int?,
    isCompleted: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ExpressiveCard(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick,
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        borderColor = if (isCompleted) null else priority.color,
        elevation = if (isCompleted) 1.dp else 2.dp
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Priority indicator
            if (!isCompleted) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(priority.color)
                )
                
                Spacer(modifier = Modifier.width(12.dp))
            }
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = if (isCompleted) 
                        MaterialTheme.colorScheme.onSurfaceVariant
                    else 
                        MaterialTheme.colorScheme.onSurface
                )
                
                // Date and time estimates
                if (dueDate != null || estimatedMinutes != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (dueDate != null) {
                            Icon(
                                imageVector = Icons.Rounded.CalendarMonth,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            
                            Spacer(modifier = Modifier.width(4.dp))
                            
                            Text(
                                text = dueDate,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            
                            Spacer(modifier = Modifier.width(16.dp))
                        }
                        
                        if (estimatedMinutes != null) {
                            Icon(
                                imageVector = Icons.Rounded.Timer,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            
                            Spacer(modifier = Modifier.width(4.dp))
                            
                            Text(
                                text = "$estimatedMinutes min",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
            
            // Priority chip for non-completed tasks
            if (!isCompleted) {
                Box(
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.small)
                        .background(priority.containerColor)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = priority.label,
                        style = MaterialTheme.typography.labelSmall,
                        color = priority.textColor
                    )
                }
            }
        }
    }
}

/**
 * Sprint status with color coding for Material 3 Expressive
 */
enum class SprintStatus(
    val label: String,
    val color: Color,
    val containerColor: Color,
    val textColor: Color
) {
    ACTIVE(
        label = "Active",
        color = AgileLifeTheme.extendedColors.sprintActive,
        containerColor = AgileLifeTheme.extendedColors.sprintActive.copy(alpha = 0.15f),
        textColor = AgileLifeTheme.extendedColors.sprintActive
    ),
    PLANNED(
        label = "Planned",
        color = AgileLifeTheme.extendedColors.sprintPlanned,
        containerColor = AgileLifeTheme.extendedColors.sprintPlanned.copy(alpha = 0.15f),
        textColor = AgileLifeTheme.extendedColors.sprintPlanned
    ),
    COMPLETED(
        label = "Completed",
        color = AgileLifeTheme.extendedColors.sprintCompleted,
        containerColor = AgileLifeTheme.extendedColors.sprintCompleted.copy(alpha = 0.15f),
        textColor = AgileLifeTheme.extendedColors.sprintCompleted
    )
}

/**
 * Task priority with color coding for Material 3 Expressive
 */
enum class TaskPriority(
    val label: String,
    val color: Color,
    val containerColor: Color,
    val textColor: Color
) {
    LOW(
        label = "Low",
        color = AgileLifeTheme.extendedColors.priorityLow,
        containerColor = AgileLifeTheme.extendedColors.priorityLow.copy(alpha = 0.15f),
        textColor = AgileLifeTheme.extendedColors.priorityLow
    ),
    MEDIUM(
        label = "Medium",
        color = AgileLifeTheme.extendedColors.priorityMedium,
        containerColor = AgileLifeTheme.extendedColors.priorityMedium.copy(alpha = 0.15f),
        textColor = AgileLifeTheme.extendedColors.priorityMedium.copy(alpha = 0.8f)
    ),
    HIGH(
        label = "High",
        color = AgileLifeTheme.extendedColors.priorityHigh,
        containerColor = AgileLifeTheme.extendedColors.priorityHigh.copy(alpha = 0.15f),
        textColor = AgileLifeTheme.extendedColors.priorityHigh
    ),
    CRITICAL(
        label = "Critical",
        color = AgileLifeTheme.extendedColors.priorityCritical,
        containerColor = AgileLifeTheme.extendedColors.priorityCritical.copy(alpha = 0.15f),
        textColor = AgileLifeTheme.extendedColors.priorityCritical
    )
}
