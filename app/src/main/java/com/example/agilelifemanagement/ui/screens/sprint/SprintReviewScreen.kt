package com.example.agilelifemanagement.ui.screens.sprint

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.DirectionsRun
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.Insights
import androidx.compose.material.icons.rounded.Lightbulb
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.StarRate
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.agilelifemanagement.ui.components.cards.ExpressiveCard
import com.example.agilelifemanagement.ui.components.cards.SprintStatus
import com.example.agilelifemanagement.ui.components.cards.TaskCard
import com.example.agilelifemanagement.ui.components.cards.TaskPriority
import com.example.agilelifemanagement.ui.model.SprintInfo
import com.example.agilelifemanagement.ui.model.TaskInfo
import com.example.agilelifemanagement.ui.theme.AgileLifeTheme

/**
 * SprintReviewScreen allows users to review completed sprints and plan for the next one
 * 
 * Features following Material 3 Expressive principles:
 * - Bold visual elements for completion statistics
 * - Reflective sections with expressive colors and icons
 * - Clear visual separation between retrospective areas
 * - Bold call-to-action for completing the review
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SprintReviewScreen(
    sprintId: String,
    onBackClick: () -> Unit,
    onFinishReviewClick: () -> Unit,
    onTaskClick: (String) -> Unit,
    onPlanNextSprintClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Sample data - would come from ViewModel in real implementation
    val sprint = remember { SampleSprintReviewData.getSprint(sprintId) }
    val completedTasks = remember { SampleSprintReviewData.getCompletedTasks(sprintId) }
    val incompleteTasksToCarryOver = remember { 
        mutableStateListOf<String>().apply { 
            addAll(SampleSprintReviewData.getIncompleteTasks(sprintId).map { it.id })
        }
    }
    
    // Retrospective state
    var wentWell by remember { mutableStateOf("") }
    var toImprove by remember { mutableStateOf("") }
    
    // Top app bar scroll behavior
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    
    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Sprint Review",
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
            // Sprint header with completed status
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.EmojiEvents,
                    contentDescription = null,
                    tint = AgileLifeTheme.extendedColors.accentSunflower,
                    modifier = Modifier.size(32.dp)
                )
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = sprint.name,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    
                    Text(
                        text = sprint.dateRange,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                // Completed badge
                Surface(
                    color = AgileLifeTheme.extendedColors.accentMint.copy(alpha = 0.15f),
                    contentColor = AgileLifeTheme.extendedColors.accentMint,
                    shape = MaterialTheme.shapes.medium
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Check,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Completed",
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            }
            
            // Completion summary card
            CompletionSummaryCard(
                tasksCompleted = sprint.tasksCompleted,
                totalTasks = sprint.totalTasks,
                progressPercent = sprint.progressPercent
            )
            
            // Retrospective section
            RetrospectiveSection(
                wentWell = wentWell,
                onWentWellChange = { wentWell = it },
                toImprove = toImprove,
                onToImproveChange = { toImprove = it }
            )
            
            // Carry-over section
            CarryOverSection(
                incompleteTaskIds = incompleteTasksToCarryOver,
                incompleteTasks = SampleSprintReviewData.getIncompleteTasks(sprintId),
                onTaskClick = onTaskClick,
                onCarryOverChange = { taskId, selected ->
                    if (selected) {
                        if (!incompleteTasksToCarryOver.contains(taskId)) {
                            incompleteTasksToCarryOver.add(taskId)
                        }
                    } else {
                        incompleteTasksToCarryOver.remove(taskId)
                    }
                }
            )
            
            // Action buttons
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Complete review button
                Button(
                    onClick = onFinishReviewClick,
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Icon(
                        imageVector = Icons.Rounded.CheckCircle,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Complete Sprint Review",
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
                
                // Plan next sprint button
                Surface(
                    onClick = onPlanNextSprintClick,
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.DirectionsRun,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Plan Next Sprint",
                            style = MaterialTheme.typography.titleSmall
                        )
                    }
                }
            }
            
            // Bottom spacing
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun CompletionSummaryCard(
    tasksCompleted: Int,
    totalTasks: Int,
    progressPercent: Float
) {
    ExpressiveCard(
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Completion Summary",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Progress circle with percentage
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.padding(8.dp)
            ) {
                CircularProgressIndicator(
                    progress = { progressPercent },
                    modifier = Modifier.size(100.dp),
                    strokeWidth = 8.dp,
                    color = when {
                        progressPercent >= 0.9f -> AgileLifeTheme.extendedColors.accentMint
                        progressPercent >= 0.6f -> AgileLifeTheme.extendedColors.accentSunflower
                        else -> AgileLifeTheme.extendedColors.accentCoral
                    },
                    trackColor = MaterialTheme.colorScheme.surfaceContainerHighest
                )
                
                Text(
                    text = "${(progressPercent * 100).toInt()}%",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            }
            
            // Stats
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Completed tasks stat
                Surface(
                    color = AgileLifeTheme.extendedColors.accentMint.copy(alpha = 0.1f),
                    contentColor = AgileLifeTheme.extendedColors.accentMint,
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        Text(
                            text = "$tasksCompleted",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Completed Tasks",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Incomplete tasks stat
                val incompleteTasks = totalTasks - tasksCompleted
                Surface(
                    color = if (incompleteTasks > 0) 
                        AgileLifeTheme.extendedColors.accentCoral.copy(alpha = 0.1f)
                    else 
                        MaterialTheme.colorScheme.surfaceContainerLow,
                    contentColor = if (incompleteTasks > 0) 
                        AgileLifeTheme.extendedColors.accentCoral
                    else 
                        MaterialTheme.colorScheme.onSurfaceVariant,
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        Text(
                            text = "$incompleteTasks",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Incomplete Tasks",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
        
        // Performance summary text
        Spacer(modifier = Modifier.height(16.dp))
        val summaryText = when {
            progressPercent >= 0.9f -> "Excellent work! The sprint was highly successful with most tasks completed."
            progressPercent >= 0.7f -> "Good job! The sprint was successful with a majority of tasks completed."
            progressPercent >= 0.5f -> "Moderate progress. About half of the tasks were completed."
            else -> "Limited progress. Less than half of the tasks were completed."
        }
        
        Text(
            text = summaryText,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun RetrospectiveSection(
    wentWell: String,
    onWentWellChange: (String) -> Unit,
    toImprove: String,
    onToImproveChange: (String) -> Unit
) {
    ExpressiveCard(
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Sprint Retrospective",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        // What went well
        RetrospectiveField(
            value = wentWell,
            onValueChange = onWentWellChange,
            label = "What went well?",
            placeholder = "List things that worked in this sprint...",
            icon = Icons.Rounded.Lightbulb,
            iconTint = AgileLifeTheme.extendedColors.accentMint
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // What to improve
        RetrospectiveField(
            value = toImprove,
            onValueChange = onToImproveChange,
            label = "What could be improved?",
            placeholder = "List areas for improvement in future sprints...",
            icon = Icons.Rounded.Insights,
            iconTint = AgileLifeTheme.extendedColors.accentLavender
        )
    }
}

@Composable
private fun RetrospectiveField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Label with icon
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(20.dp)
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Text(
                text = label,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
        }
        
        // Input field
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { 
                Text(
                    text = placeholder,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            textStyle = MaterialTheme.typography.bodyLarge,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Sentences
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = iconTint,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                focusedLabelColor = iconTint
            ),
            shape = MaterialTheme.shapes.medium
        )
    }
}

@Composable
private fun CarryOverSection(
    incompleteTaskIds: List<String>,
    incompleteTasks: List<TaskInfo>,
    onTaskClick: (String) -> Unit,
    onCarryOverChange: (String, Boolean) -> Unit
) {
    ExpressiveCard(
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Carry-Over Backlog",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        if (incompleteTasks.isEmpty()) {
            // No tasks to carry over
            Surface(
                color = MaterialTheme.colorScheme.surfaceContainerLow,
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.CheckCircle,
                        contentDescription = null,
                        tint = AgileLifeTheme.extendedColors.accentMint,
                        modifier = Modifier.size(24.dp)
                    )
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    Text(
                        text = "No incomplete tasks to carry over!",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        } else {
            // Instructions
            Surface(
                color = MaterialTheme.colorScheme.surfaceContainerLow,
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Warning,
                        contentDescription = null,
                        tint = AgileLifeTheme.extendedColors.accentSunflower,
                        modifier = Modifier.size(24.dp)
                    )
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    Text(
                        text = "Select tasks that should be carried over to the next sprint.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            // Task list with checkboxes
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                incompleteTasks.forEach { task ->
                    CarryOverTaskItem(
                        task = task,
                        isSelected = incompleteTaskIds.contains(task.id),
                        onSelectionChange = { selected ->
                            onCarryOverChange(task.id, selected)
                        },
                        onClick = { onTaskClick(task.id) }
                    )
                }
            }
            
            // Summary of selected tasks
            Spacer(modifier = Modifier.height(16.dp))
            
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.padding(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Refresh,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        Text(
                            text = "${incompleteTaskIds.size} tasks selected for carry-over",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    Icon(
                        imageVector = Icons.Filled.KeyboardArrowRight,
                        contentDescription = null
                    )
                }
            }
        }
    }
}

@Composable
private fun CarryOverTaskItem(
    task: TaskInfo,
    isSelected: Boolean,
    onSelectionChange: (Boolean) -> Unit,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(8.dp)
        ) {
            Checkbox(
                checked = isSelected,
                onCheckedChange = onSelectionChange
            )
            
            // Priority indicator
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(task.priority.color)
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                
                if (task.dueDate != null) {
                    Text(
                        text = "Due: ${task.dueDate}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

/**
 * Sample data for the sprint review screen
 */
object SampleSprintReviewData {
    fun getSprint(sprintId: String): SprintInfo {
        return SprintInfo(
            id = sprintId,
            name = "Sprint 22: Task Management",
            status = com.example.agilelifemanagement.ui.components.cards.SprintStatus.COMPLETED,
            dateRange = "Apr 29 - May 13",
            progressPercent = 0.85f,
            tasksCompleted = 17,
            totalTasks = 20
        )
    }
    
    fun getCompletedTasks(sprintId: String): List<TaskInfo> {
        return listOf(
            TaskInfo(
                id = "task-5",
                title = "Create UI Element Library",
                description = "Create a comprehensive library of UI components following Material 3 guidelines",
                priority = TaskPriority.HIGH,
                dueDate = "May 6",
                isCompleted = true,
                sprintId = sprintId
            ),
            TaskInfo(
                id = "task-6",
                title = "Implement Login Screen",
                description = "Design and implement the user login interface",
                priority = TaskPriority.MEDIUM,
                dueDate = "May 8",
                isCompleted = true,
                sprintId = sprintId
            ),
            TaskInfo(
                id = "task-7",
                title = "Setup Project Structure",
                description = "Initialize the project with Clean Architecture layers",
                priority = TaskPriority.HIGH,
                dueDate = "May 3",
                isCompleted = true,
                sprintId = sprintId
            )
        )
    }
    
    fun getIncompleteTasks(sprintId: String): List<TaskInfo> {
        return listOf(
            TaskInfo(
                id = "task-8",
                title = "Implement Task Detail Screen",
                description = "Create the detailed view for individual tasks with all metadata",
                priority = TaskPriority.HIGH,
                dueDate = "May 12",
                isCompleted = false,
                sprintId = sprintId
            ),
            TaskInfo(
                id = "task-9",
                title = "Add Task Filtering Options",
                description = "Implement filters for tasks by status, priority, and date",
                priority = TaskPriority.MEDIUM,
                dueDate = "May 11",
                isCompleted = false,
                sprintId = sprintId
            ),
            TaskInfo(
                id = "task-10",
                title = "Create Task Priority Visualization",
                description = "Add visual indicators for task priorities",
                priority = TaskPriority.LOW,
                dueDate = null,
                isCompleted = false,
                sprintId = sprintId
            )
        )
    }
}
