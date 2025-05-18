package com.example.agilelifemanagement.ui.screens.day

import androidx.compose.animation.animateContentSize
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
import com.example.agilelifemanagement.ui.components.timeline.TimeBlockCategory
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.EmojiFoodBeverage
import androidx.compose.material.icons.rounded.FitnessCenter
import androidx.compose.material.icons.rounded.MeetingRoom
import com.example.agilelifemanagement.ui.components.timeline.TimeBlock
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.Pets
import androidx.compose.material.icons.rounded.SelfImprovement
import androidx.compose.material.icons.rounded.StarRate
import androidx.compose.material.icons.rounded.WbSunny
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.agilelifemanagement.ui.components.cards.ExpressiveCard
import com.example.agilelifemanagement.ui.components.cards.TaskCard
import com.example.agilelifemanagement.ui.components.cards.TaskPriority
import com.example.agilelifemanagement.ui.screens.dashboard.TaskInfo
import com.example.agilelifemanagement.ui.theme.AgileLifeTheme
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * DayDetailScreen shows a comprehensive view of a selected day with timeline, tasks, 
 * and wellness metrics following Material 3 Expressive design principles.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DayDetailScreen(
    dayId: String,
    onBackClick: () -> Unit,
    onEditDayPlanClick: () -> Unit,
    onAddTaskClick: () -> Unit,
    onTaskClick: (String) -> Unit,
    onEditMoodClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Sample data - would come from ViewModel in real implementation
    val dayData = remember { SampleDayData.getDay(dayId) }
    
    // Top app bar scroll behavior
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    
    // More options menu state
    var showOptionsMenu by remember { mutableStateOf(false) }
    
    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = dayData.formattedDate,
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
                            imageVector = Icons.Rounded.MoreVert,
                            contentDescription = "More options"
                        )
                        
                        DropdownMenu(
                            expanded = showOptionsMenu,
                            onDismissRequest = { showOptionsMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Export day summary") },
                                onClick = { 
                                    showOptionsMenu = false
                                    // Export functionality would go here 
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Mark day as template") },
                                onClick = { 
                                    showOptionsMenu = false
                                    // Template functionality would go here
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
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = { Text("Add Task") },
                icon = { 
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add task"
                    )
                },
                onClick = onAddTaskClick,
                expanded = true,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
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
            // Day header with date and day of week
            DayHeader(dayData)
            
            // Day mood and wellness tracker
            DayMoodCard(dayData, onEditMoodClick)
            
            // Day timeline
            DayTimeline(dayData, onEditDayPlanClick)
            
            // Day tasks
            DayTasks(dayData.tasks, onTaskClick)
            
            // Bottom spacing
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun DayHeader(dayData: DayData) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
    ) {
        // Date icon with circular background
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier.size(48.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Rounded.CalendarMonth,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = dayData.dayOfWeek,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = dayData.longFormattedDate,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        // Sprint name if day is part of sprint
        dayData.sprintName?.let { sprintName ->
            Surface(
                color = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                shape = MaterialTheme.shapes.small
            ) {
                Text(
                    text = sprintName,
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun DayMoodCard(
    dayData: DayData,
    onEditMoodClick: () -> Unit
) {
    ExpressiveCard(
        modifier = Modifier.fillMaxWidth(),
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Day Wellness",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            IconButton(onClick = onEditMoodClick) {
                Icon(
                    imageVector = Icons.Rounded.Edit,
                    contentDescription = "Edit mood and wellness",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Mood indicator
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // Energy level
            WellnessMetric(
                icon = Icons.Rounded.WbSunny,
                iconTint = AgileLifeTheme.extendedColors.accentSunflower,
                label = "Energy",
                value = "${dayData.energyLevel}/10",
                backgroundColor = AgileLifeTheme.extendedColors.accentSunflower.copy(alpha = 0.1f)
            )
            
            // Focus level
            WellnessMetric(
                icon = Icons.Rounded.SelfImprovement,
                iconTint = AgileLifeTheme.extendedColors.accentLavender,
                label = "Focus",
                value = "${dayData.focusLevel}/10",
                backgroundColor = AgileLifeTheme.extendedColors.accentLavender.copy(alpha = 0.1f)
            )
            
            // Productivity level
            WellnessMetric(
                icon = Icons.Rounded.StarRate,
                iconTint = AgileLifeTheme.extendedColors.accentMint,
                label = "Productivity",
                value = "${dayData.productivityLevel}/10",
                backgroundColor = AgileLifeTheme.extendedColors.accentMint.copy(alpha = 0.1f)
            )
        }
        
        // Daily note if available
        dayData.dailyNote?.let { note ->
            Spacer(modifier = Modifier.height(12.dp))
            Divider(color = MaterialTheme.colorScheme.outlineVariant)
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = "Daily Reflection",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = note,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun WellnessMetric(
    icon: ImageVector,
    iconTint: Color,
    label: String,
    value: String,
    backgroundColor: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Icon with background
        Surface(
            color = backgroundColor,
            contentColor = iconTint,
            shape = CircleShape,
            modifier = Modifier.size(56.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun DayTimeline(
    dayData: DayData,
    onEditDayPlanClick: () -> Unit
) {
    ExpressiveCard(
        modifier = Modifier.fillMaxWidth(),
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Day Timeline",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            IconButton(onClick = onEditDayPlanClick) {
                Icon(
                    imageVector = Icons.Rounded.Edit,
                    contentDescription = "Edit day plan",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Timeline items
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (dayData.timeBlocks.isEmpty()) {
                // No timeline blocks
                Surface(
                    color = MaterialTheme.colorScheme.surfaceContainerLow,
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(vertical = 32.dp, horizontal = 16.dp)
                    ) {
                        Text(
                            text = "No activities planned for today",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        Surface(
                            onClick = onEditDayPlanClick,
                            color = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            shape = MaterialTheme.shapes.small
                        ) {
                            Text(
                                text = "Plan Your Day",
                                style = MaterialTheme.typography.labelLarge,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
            } else {
                (dayData.timeBlocks as? List<TimeBlock>)?.forEachIndexed { index, timeBlock ->
                    TimelineItem(
                        timeBlock = timeBlock,
                        isLastItem = index == (dayData.timeBlocks as? List<TimeBlock>)?.lastIndex
                    )
                }
            }
        }
    }
}

@Composable
private fun TimelineItem(
    timeBlock: TimeBlock,
    isLastItem: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        // Time column
        Column(
            horizontalAlignment = Alignment.End,
            modifier = Modifier.width(50.dp)
        ) {
            Text(
                text = timeBlock.timeRange.split(" - ")[0],
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = timeBlock.timeRange.split(" - ").getOrNull(1) ?: "",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        // Timeline line and dot
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 12.dp)
        ) {
            // Circle dot for timeline point
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .background(
                        color = timeBlock.category.color,
                        shape = CircleShape
                    )
            )
            
            // Line connecting to next item (if not the last item)
            if (!isLastItem) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(64.dp)
                        .background(MaterialTheme.colorScheme.outlineVariant)
                )
            }
        }
        
        // Content
        Surface(
            color = timeBlock.category.color.copy(alpha = 0.1f),
            contentColor = MaterialTheme.colorScheme.onSurface,
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier
                .weight(1f)
                .animateContentSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                // Title with category icon
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = timeBlock.icon,
                        contentDescription = null,
                        tint = timeBlock.category.color,
                        modifier = Modifier.size(20.dp)
                    )
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Text(
                        text = timeBlock.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                if (timeBlock.description.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = timeBlock.description,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                
                if (timeBlock.location.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = "Location: ${timeBlock.location}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun DayTasks(
    tasks: List<TaskInfo>,
    onTaskClick: (String) -> Unit
) {
    ExpressiveCard(
        modifier = Modifier.fillMaxWidth(),
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Text(
            text = "Day Tasks",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        if (tasks.isEmpty()) {
            // No tasks
            Surface(
                color = MaterialTheme.colorScheme.surfaceContainerLow,
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "No tasks assigned for today",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 32.dp, horizontal = 16.dp)
                )
            }
        } else {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Task stats
                val completedCount = tasks.count { it.isCompleted }
                val totalCount = tasks.size
                
                // Task completion stats
                Surface(
                    color = MaterialTheme.colorScheme.surfaceContainerLow,
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 12.dp, horizontal = 16.dp)
                    ) {
                        Text(
                            text = "$completedCount of $totalCount completed",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        
                        Text(
                            text = "${(completedCount * 100 / totalCount)}%",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (completedCount == totalCount) 
                                AgileLifeTheme.extendedColors.accentMint
                            else 
                                MaterialTheme.colorScheme.primary
                        )
                    }
                }
                
                // Tasks
                tasks.forEach { task ->
                    TaskCard(
                        title = task.title,
                        priority = task.priority,
                        isCompleted = task.isCompleted,
                        dueDate = task.dueDate,
                        estimatedMinutes = task.estimatedMinutes,
                        onClick = { onTaskClick(task.id) }
                    )
                }
            }
        }
    }
}

/**
 * Activity categories for time blocks
 */
enum class ActivityCategory(val icon: ImageVector) {
    WORK(Icons.Rounded.MeetingRoom),
    PERSONAL(Icons.Rounded.Pets),
    FITNESS(Icons.Rounded.FitnessCenter),
    BREAK(Icons.Rounded.EmojiFoodBeverage);
    
    @Composable
    fun getCategoryColor(): Color {
        return when (this) {
            WORK -> AgileLifeTheme.extendedColors.accentCoral
            PERSONAL -> AgileLifeTheme.extendedColors.accentLavender
            FITNESS -> AgileLifeTheme.extendedColors.accentMint
            BREAK -> AgileLifeTheme.extendedColors.accentSunflower
        }
    }
    
    // For backward compatibility
    val color: Color
        @Composable get() = getCategoryColor()
}

/**
 * Data class representing a day's data
 */
data class DayData(
    val id: String,
    val date: LocalDate,
    val energyLevel: Int,
    val focusLevel: Int,
    val productivityLevel: Int,
    val dailyNote: String? = null,
    val sprintName: String? = null,
    val timeBlocks: List<TimeBlock> = emptyList(),
    val tasks: List<TaskInfo> = emptyList()
) {
    val formattedDate: String
        get() = date.format(DateTimeFormatter.ofPattern("MMM d, yyyy"))
        
    val longFormattedDate: String
        get() = date.format(DateTimeFormatter.ofPattern("MMMM d, yyyy"))
        
    val dayOfWeek: String
        get() = date.format(DateTimeFormatter.ofPattern("EEEE"))
}

/**
 * Sample data for the day detail screen
 */
object SampleDayData {
    fun getDay(dayId: String): DayData {
        return DayData(
            id = dayId,
            date = LocalDate.now(),
            energyLevel = 8,
            focusLevel = 7,
            productivityLevel = 9,
            dailyNote = "Today was productive. I completed most of my tasks and had a good team meeting in the morning. Need to follow up on API integration issues tomorrow.",
            sprintName = "Sprint 22",
            timeBlocks = listOf(
                TimeBlock(
                    id = "block1",
                    title = "Morning Standup",
                    timeRange = "9:00 AM - 9:30 AM",
                    description = "Daily team sync meeting",
                    location = "Conference Room A",
                    category = TimeBlockCategory.MEETING
                ),
                TimeBlock(
                    id = "block2",
                    title = "UI Development",
                    timeRange = "10:00 AM - 12:00 PM",
                    description = "Implement new dashboard components",
                    category = TimeBlockCategory.TASK
                ),
                TimeBlock(
                    id = "block3",
                    title = "Lunch Break",
                    timeRange = "12:00 PM - 1:00 PM",
                    category = TimeBlockCategory.BREAK
                ),
                TimeBlock(
                    id = "block4",
                    title = "Code Review",
                    timeRange = "1:30 PM - 3:00 PM",
                    category = TimeBlockCategory.TASK
                ),
                TimeBlock(
                    id = "block5",
                    title = "Gym Session",
                    timeRange = "6:00 PM - 7:00 PM",
                    location = "Fitness Center",
                    category = TimeBlockCategory.PERSONAL
                )
            ),
            tasks = listOf(
                TaskInfo(
                    id = "task1",
                    title = "Complete dashboard UI components",
                    priority = TaskPriority.HIGH,
                    dueDate = "Today, 5:00 PM",
                    estimatedMinutes = 180,
                    isCompleted = true
                ),
                TaskInfo(
                    id = "task2",
                    title = "Review pull requests from team",
                    priority = TaskPriority.MEDIUM,
                    dueDate = "Today, 3:00 PM",
                    estimatedMinutes = 60,
                    isCompleted = true
                ),
                TaskInfo(
                    id = "task3",
                    title = "Fix API integration issues",
                    priority = TaskPriority.HIGH,
                    dueDate = "Today, 4:00 PM",
                    estimatedMinutes = 120,
                    isCompleted = false
                ),
                TaskInfo(
                    id = "task4",
                    title = "Prepare for tomorrow's demo",
                    priority = TaskPriority.MEDIUM,
                    dueDate = "Today, 6:00 PM",
                    estimatedMinutes = 90,
                    isCompleted = false
                )
            )
        )
    }
}
