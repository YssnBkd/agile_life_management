package com.example.agilelifemanagement.ui.components.tasks

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.rounded.AttachFile
import androidx.compose.material.icons.rounded.CalendarToday
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Comment
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.Timer
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.agilelifemanagement.ui.components.cards.TaskPriority
import com.example.agilelifemanagement.ui.theme.AgileLifeTheme

/**
 * A collection of reusable task-related components following Material 3 Expressive design principles
 */

/**
 * ChecklistItemComponent displays a single checklist item with a checkbox and text
 */
@Composable
fun ChecklistItemComponent(
    text: String,
    isCompleted: Boolean,
    onToggleCompletion: () -> Unit,
    onEdit: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val checkColor by animateColorAsState(
        targetValue = if (isCompleted) 
            AgileLifeTheme.extendedColors.accentMint 
        else 
            MaterialTheme.colorScheme.outline,
        label = ""
    )
    
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(onClick = { if (onEdit != null) onEdit() else onToggleCompletion() })
    ) {
        Checkbox(
            checked = isCompleted,
            onCheckedChange = { onToggleCompletion() },
            colors = CheckboxDefaults.colors(
                checkedColor = AgileLifeTheme.extendedColors.accentMint,
                uncheckedColor = MaterialTheme.colorScheme.outline
            )
        )
        
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = if (isCompleted) 
                MaterialTheme.colorScheme.onSurfaceVariant 
            else 
                MaterialTheme.colorScheme.onSurface,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp)
        )
        
        if (onEdit != null) {
            IconButton(
                onClick = onEdit,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.MoreVert,
                    contentDescription = "Edit checklist item",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

/**
 * ChecklistEditor allows editing a list of checklist items
 */
@Composable
fun ChecklistEditor(
    items: List<ChecklistItemData>,
    onItemToggle: (Int, Boolean) -> Unit,
    onItemTextChange: (Int, String) -> Unit,
    onAddItem: () -> Unit,
    onRemoveItem: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Header with progress
        val completedCount = items.count { it.isCompleted }
        val totalCount = items.size
        val progress = if (totalCount > 0) completedCount.toFloat() / totalCount else 0f
        
        if (totalCount > 0) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "$completedCount of $totalCount completed",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Text(
                    text = "${(progress * 100).toInt()}%",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = when {
                        progress >= 1f -> AgileLifeTheme.extendedColors.accentMint
                        progress >= 0.5f -> AgileLifeTheme.extendedColors.accentSunflower
                        else -> MaterialTheme.colorScheme.primary
                    }
                )
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            // Progress bar
            val animatedProgress by animateFloatAsState(targetValue = progress, label = "")
            LinearProgressIndicator(
                progress = { animatedProgress },
                color = when {
                    progress >= 1f -> AgileLifeTheme.extendedColors.accentMint
                    progress >= 0.5f -> AgileLifeTheme.extendedColors.accentSunflower
                    else -> MaterialTheme.colorScheme.primary
                },
                trackColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(12.dp))
        }
        
        // List of items
        items.forEachIndexed { index, item ->
            ChecklistItemEditor(
                text = item.text,
                isCompleted = item.isCompleted,
                onTextChange = { onItemTextChange(index, it) },
                onCompletionToggle = { onItemToggle(index, it) },
                onRemove = { onRemoveItem(index) }
            )
        }
        
        // Add button
        TextButton(
            onClick = onAddItem,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Text("Add Item")
        }
    }
}

/**
 * ChecklistItemEditor allows editing a single checklist item
 */
@Composable
fun ChecklistItemEditor(
    text: String,
    isCompleted: Boolean,
    onTextChange: (String) -> Unit,
    onCompletionToggle: (Boolean) -> Unit,
    onRemove: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        // Checkbox
        Checkbox(
            checked = isCompleted,
            onCheckedChange = { onCompletionToggle(!isCompleted) },
            colors = CheckboxDefaults.colors(
                checkedColor = AgileLifeTheme.extendedColors.accentMint,
                uncheckedColor = MaterialTheme.colorScheme.outline
            )
        )
        
        // Text field
        OutlinedTextField(
            value = text,
            onValueChange = onTextChange,
            placeholder = { Text("Enter checklist item") },
            modifier = Modifier.weight(1f),
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Sentences
            ),
            singleLine = true
        )
        
        // Delete button
        IconButton(
            onClick = onRemove,
            modifier = Modifier.size(40.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Remove item",
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

/**
 * PrioritySelector allows selection of task priority
 */
@Composable
fun PrioritySelector(
    selectedPriority: TaskPriority,
    onPrioritySelected: (TaskPriority) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        TaskPriority.values().forEach { priority ->
            val isSelected = selectedPriority == priority
            val animatedColor by animateColorAsState(
                targetValue = if (isSelected) 
                    priority.color.copy(alpha = 0.15f) 
                else 
                    MaterialTheme.colorScheme.surfaceContainerLow,
                label = ""
            )
            
            Surface(
                onClick = { onPrioritySelected(priority) },
                color = animatedColor,
                contentColor = priority.color,
                shape = MaterialTheme.shapes.medium,
                border = if (isSelected) 
                    androidx.compose.foundation.BorderStroke(1.dp, priority.color) 
                else 
                    null,
                modifier = Modifier.weight(1f)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(vertical = 12.dp, horizontal = 8.dp)
                ) {
                    // Priority indicator
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .clip(CircleShape)
                            .background(priority.color)
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = priority.name.lowercase().replaceFirstChar { it.uppercase() },
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
        }
    }
}

/**
 * TaskMetadataItem displays a metadata field for a task
 */
@Composable
fun TaskMetadataItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    label: String,
    value: String,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val rowModifier = if (onClick != null) {
        modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp)
    } else {
        modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    }
    
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = rowModifier
    ) {
        Surface(
            color = iconTint.copy(alpha = 0.1f),
            contentColor = iconTint,
            shape = CircleShape,
            modifier = Modifier.size(40.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
        }
        
        if (onClick != null) {
            Icon(
                imageVector = Icons.Rounded.CalendarToday,
                contentDescription = "Change",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

/**
 * TaskActionRow displays a row of action buttons for a task
 */
@Composable
fun TaskActionRow(
    onCommentClick: (() -> Unit)? = null,
    onAttachmentClick: (() -> Unit)? = null,
    onTimerClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        // Comment action
        if (onCommentClick != null) {
            FilledTonalIconButton(
                onClick = onCommentClick,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Comment,
                    contentDescription = "Add comment",
                    modifier = Modifier.size(20.dp)
                )
            }
        }
        
        // Attachment action
        if (onAttachmentClick != null) {
            FilledTonalIconButton(
                onClick = onAttachmentClick,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.AttachFile,
                    contentDescription = "Add attachment",
                    modifier = Modifier.size(20.dp)
                )
            }
        }
        
        // Timer action
        if (onTimerClick != null) {
            FilledTonalIconButton(
                onClick = onTimerClick,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Timer,
                    contentDescription = "Start timer",
                    modifier = Modifier.size(20.dp)
                )
            }
        }
        
        // Spacer to push complete button to the right
        Spacer(modifier = Modifier.weight(1f))
    }
}

/**
 * TaskStatusIndicator shows the completion status of a task with color and icon
 */
@Composable
fun TaskStatusIndicator(
    isCompleted: Boolean,
    modifier: Modifier = Modifier
) {
    val color by animateColorAsState(
        targetValue = if (isCompleted) 
            AgileLifeTheme.extendedColors.accentMint 
        else 
            MaterialTheme.colorScheme.primary,
        label = ""
    )
    
    Surface(
        color = color.copy(alpha = 0.1f),
        contentColor = color,
        shape = MaterialTheme.shapes.small,
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Icon(
                imageVector = if (isCompleted) 
                    Icons.Default.Check 
                else 
                    Icons.Rounded.Timer,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
            
            Spacer(modifier = Modifier.width(6.dp))
            
            Text(
                text = if (isCompleted) "Completed" else "In Progress",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

/**
 * Data class for checklist items
 */
data class ChecklistItemData(
    val id: String,
    val text: String,
    val isCompleted: Boolean
)
