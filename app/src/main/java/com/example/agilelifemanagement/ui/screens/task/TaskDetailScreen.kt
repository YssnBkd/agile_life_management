package com.example.agilelifemanagement.ui.screens.task

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.rounded.AccessTime
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.CalendarToday
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.agilelifemanagement.ui.components.cards.ExpressiveCard
import com.example.agilelifemanagement.ui.components.cards.TaskPriority
import com.example.agilelifemanagement.ui.theme.AgileLifeTheme
import java.time.LocalDateTime

/**
 * TaskDetailScreen displays detailed information about a specific task
 * following Material 3 Expressive design principles.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDetailScreen(
    taskId: String,
    onBackClick: () -> Unit,
    onEditClick: (String) -> Unit,
    onDeleteClick: (String) -> Unit,
    onToggleCompletionClick: (String, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    // Fetch task data - would come from ViewModel in real implementation
    val task = remember { SampleTaskDataDetail.getTask(taskId) }
    var showOptionsMenu by remember { mutableStateOf(false) }
    
    // Top app bar scroll behavior
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    
    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Task Details",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Rounded.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showOptionsMenu = true }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "More options"
                        )
                        
                        DropdownMenu(
                            expanded = showOptionsMenu,
                            onDismissRequest = { showOptionsMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Duplicate task") },
                                onClick = { 
                                    showOptionsMenu = false
                                    // Duplicate action would go here
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Move to another sprint") },
                                onClick = { 
                                    showOptionsMenu = false
                                    // Move action would go here
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Delete") },
                                onClick = { 
                                    showOptionsMenu = false
                                    onDeleteClick(taskId)
                                }
                            )
                        }
                    }
                },
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Task header with title, priority, and status
            TaskHeader(task)
            
            // Task metadata (due date, estimated time, etc.)
            TaskMetadata(task)
            
            // Task description
            if (!task.description.isNullOrBlank()) {
                TaskDescription(task.description)
            }
            
            // Task checklist
            if (task.checklistItems.isNotEmpty()) {
                TaskChecklist(task)
            }
            
            // Task actions
            TaskActions(
                taskId = taskId,
                isCompleted = task.isCompleted,
                onEditClick = onEditClick,
                onToggleCompletionClick = onToggleCompletionClick
            )
            
            // Bottom spacing
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun TaskHeader(task: TaskDetailData) {
    ExpressiveCard(
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Priority indicator
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .clip(CircleShape)
                    .background(task.priority.color)
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Priority text
                    Text(
                        text = task.priority.name.lowercase().replaceFirstChar { it.uppercase() },
                        style = MaterialTheme.typography.bodyMedium,
                        color = task.priority.color
                    )
                    
                    Text(
                        text = " • ",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    // Status text
                    Text(
                        text = if (task.isCompleted) "Completed" else "Active",
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (task.isCompleted) 
                            AgileLifeTheme.extendedColors.accentMint 
                        else 
                            MaterialTheme.colorScheme.primary
                    )
                }
            }
            
            // Completion status indicator
            if (task.isCompleted) {
                Icon(
                    imageVector = Icons.Rounded.CheckCircle,
                    contentDescription = "Completed",
                    tint = AgileLifeTheme.extendedColors.accentMint,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
        
        // Sprint association if available
        if (task.sprintName != null) {
            Spacer(modifier = Modifier.height(16.dp))
            
            Surface(
                color = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                shape = MaterialTheme.shapes.small,
                modifier = Modifier.align(Alignment.End)
            ) {
                Text(
                    text = "Sprint: ${task.sprintName}",
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }
    }
}

@Composable
private fun TaskMetadata(task: TaskDetailData) {
    ExpressiveCard(
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Details",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        // Due date
        if (task.dueDate != null) {
            MetadataItem(
                icon = Icons.Rounded.CalendarToday,
                iconTint = AgileLifeTheme.extendedColors.accentCoral,
                label = "Due Date",
                value = task.dueDate
            )
            
            Spacer(modifier = Modifier.height(12.dp))
        }
        
        // Estimated time
        if (task.estimatedMinutes != null) {
            val hours = task.estimatedMinutes / 60
            val minutes = task.estimatedMinutes % 60
            val timeText = when {
                hours > 0 && minutes > 0 -> "$hours hr $minutes min"
                hours > 0 -> "$hours hr"
                else -> "$minutes min"
            }
            
            MetadataItem(
                icon = Icons.Rounded.AccessTime,
                iconTint = AgileLifeTheme.extendedColors.accentLavender,
                label = "Estimated Time",
                value = timeText
            )
            
            Spacer(modifier = Modifier.height(12.dp))
        }
        
        // Created date
        MetadataItem(
            icon = Icons.Rounded.CalendarToday,
            iconTint = MaterialTheme.colorScheme.tertiary,
            label = "Created",
            value = task.createdDate
        )
    }
}

@Composable
private fun MetadataItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: androidx.compose.ui.graphics.Color,
    label: String,
    value: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(20.dp)
        )
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column {
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
    }
}

@Composable
private fun TaskDescription(description: String) {
    ExpressiveCard(
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Description",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        Text(
            text = description,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun TaskChecklist(task: TaskDetailData) {
    ExpressiveCard(
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Checklist",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        // Checklist progress
        val completedItems = task.checklistItems.count { it.isCompleted }
        val totalItems = task.checklistItems.size
        val progressPercent = if (totalItems > 0) completedItems * 100 / totalItems else 0
        
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        ) {
            Text(
                text = "$completedItems of $totalItems completed",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Text(
                text = "$progressPercent%",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = if (progressPercent == 100) 
                    AgileLifeTheme.extendedColors.accentMint
                else 
                    MaterialTheme.colorScheme.primary
            )
        }
        
        // Progress bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(MaterialTheme.shapes.medium)
                .background(MaterialTheme.colorScheme.surfaceContainerHighest)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progressPercent / 100f)
                    .height(8.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .background(
                        if (progressPercent == 100) 
                            AgileLifeTheme.extendedColors.accentMint
                        else 
                            MaterialTheme.colorScheme.primary
                    )
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Checklist items
        task.checklistItems.forEach { item ->
            ChecklistItem(item)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun ChecklistItem(item: ChecklistItem) {
    Row(
        verticalAlignment = Alignment.Top,
        modifier = Modifier.fillMaxWidth()
    ) {
        // Checkbox
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .padding(top = 2.dp)
                .size(20.dp)
                .clip(CircleShape)
                .background(
                    if (item.isCompleted) 
                        AgileLifeTheme.extendedColors.accentMint 
                    else 
                        MaterialTheme.colorScheme.surfaceContainerHigh
                )
        ) {
            if (item.isCompleted) {
                Icon(
                    imageVector = Icons.Rounded.CheckCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
        
        Spacer(modifier = Modifier.width(12.dp))
        
        // Item text
        Text(
            text = item.text,
            style = MaterialTheme.typography.bodyLarge,
            color = if (item.isCompleted) 
                MaterialTheme.colorScheme.onSurfaceVariant
            else 
                MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun TaskActions(
    taskId: String,
    isCompleted: Boolean,
    onEditClick: (String) -> Unit,
    onToggleCompletionClick: (String, Boolean) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier.fillMaxWidth()
    ) {
        // Edit button
        FilledTonalButton(
            onClick = { onEditClick(taskId) },
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                imageVector = Icons.Rounded.Edit,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Text("Edit Task")
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        // Complete/Reopen button
        FilledTonalButton(
            onClick = { onToggleCompletionClick(taskId, !isCompleted) },
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                imageVector = if (isCompleted) 
                    Icons.Rounded.Refresh
                else 
                    Icons.Rounded.CheckCircle,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Text(
                text = if (isCompleted) "Reopen" else "Complete"
            )
        }
    }
}

/**
 * Data class for checklist items
 */
data class ChecklistItem(
    val id: String,
    val text: String,
    val isCompleted: Boolean
)

/**
 * Data class for task details
 */
data class TaskDetailData(
    val id: String,
    val title: String,
    val description: String? = null,
    val priority: TaskPriority,
    val dueDate: String? = null,
    val estimatedMinutes: Int? = null,
    val isCompleted: Boolean = false,
    val sprintName: String? = null,
    val createdDate: String,
    val checklistItems: List<ChecklistItem> = emptyList()
)

/**
 * Sample data for the task detail screen
 */
object SampleTaskDataDetail {
    fun getTask(taskId: String): TaskDetailData {
        return TaskDetailData(
            id = taskId,
            title = "Create UI mockups for dashboard",
            description = "Design comprehensive UI mockups for the new dashboard. Include all required components: statistics widgets, activity timeline, and quick action buttons. Make sure to follow our brand guidelines and accessibility standards.",
            priority = TaskPriority.HIGH,
            dueDate = "Today, 5:00 PM",
            estimatedMinutes = 120,
            isCompleted = false,
            sprintName = "Sprint 22",
            createdDate = "May 13, 2025",
            checklistItems = listOf(
                ChecklistItem(
                    id = "cl1",
                    text = "Create wireframes",
                    isCompleted = true
                ),
                ChecklistItem(
                    id = "cl2",
                    text = "Review wireframes with team",
                    isCompleted = true
                ),
                ChecklistItem(
                    id = "cl3",
                    text = "Create high-fidelity mockups",
                    isCompleted = false
                ),
                ChecklistItem(
                    id = "cl4",
                    text = "Export assets for development",
                    isCompleted = false
                )
            )
        )
    }
}
