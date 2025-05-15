package com.example.agilelifemanagement.ui.screens.sprint

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FormatListBulleted
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.FilterList
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.agilelifemanagement.ui.components.cards.ExpressiveCard
import androidx.compose.ui.text.style.TextAlign
import com.example.agilelifemanagement.ui.components.cards.SprintStatus
import com.example.agilelifemanagement.ui.components.cards.TaskCard
import com.example.agilelifemanagement.ui.components.cards.TaskPriority
import com.example.agilelifemanagement.ui.components.timeline.HorizontalTimeline
import com.example.agilelifemanagement.ui.components.timeline.TimelineDay
import com.example.agilelifemanagement.ui.screens.dashboard.SprintInfo
import com.example.agilelifemanagement.ui.screens.dashboard.TaskInfo
import com.example.agilelifemanagement.ui.theme.AgileLifeTheme

/**
 * SprintDetailScreen displays detailed information about a sprint with tabs
 * for Overview, Calendar, and Backlog
 * 
 * Features following Material 3 Expressive principles:
 * - Bold header with clear information hierarchy
 * - Tab-based navigation for content organization
 * - Visual progress indicators with expressive styling
 * - Interactive elements with tactile feedback
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SprintDetailScreen(
    sprintId: String,
    onBackClick: () -> Unit,
    onEditSprintClick: (String) -> Unit,
    onSprintReviewClick: (String) -> Unit,
    onTaskClick: (String) -> Unit,
    onDayClick: (String) -> Unit,
    onCreateTaskClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // Sample data - would come from ViewModel in real implementation
    val sprint = remember { SampleSprintDetailData.getSprint(sprintId) }
    val sprintDays = remember { SampleSprintDetailData.getSprintDays(sprintId) }
    val sprintTasks = remember { SampleSprintDetailData.getSprintTasks(sprintId) }
    
    // Tab state
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = remember { SprintDetailTab.values() }
    
    // Top app bar scroll behavior
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    
    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Sprint Details",
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
                    IconButton(onClick = { onEditSprintClick(sprintId) }) {
                        Icon(
                            imageVector = Icons.Rounded.Edit,
                            contentDescription = "Edit Sprint"
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            if (selectedTabIndex == 2) { // Backlog tab
                ExtendedFloatingActionButton(
                    onClick = { onCreateTaskClick(sprintId) },
                    icon = {
                        Icon(
                            imageVector = Icons.Rounded.Edit,
                            contentDescription = null
                        )
                    },
                    text = { Text("Add Task") },
                    expanded = true,
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Header section with sprint info and progress
            SprintHeader(
                sprint = sprint,
                days = sprintDays,
                selectedDayId = if (selectedTabIndex == 1) 
                    sprintDays.find { it.isToday }?.id ?: sprintDays.firstOrNull()?.id ?: ""
                else "",
                onDaySelected = onDayClick,
                onCompleteSprintClick = { onSprintReviewClick(sprintId) }
            )
            
            // Tab row
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary
            ) {
                tabs.forEachIndexed { index, tab ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { Text(tab.title) },
                        icon = {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = null
                            )
                        }
                    )
                }
            }
            
            // Tab content
            when (selectedTabIndex) {
                0 -> OverviewTab(sprint = sprint)
                1 -> CalendarTab(
                    days = sprintDays,
                    tasks = sprintTasks,
                    onTaskClick = onTaskClick,
                    onDayClick = onDayClick
                )
                2 -> BacklogTab(
                    tasks = sprintTasks,
                    onTaskClick = onTaskClick
                )
            }
        }
    }
}

@Composable
private fun SprintHeader(
    sprint: SprintInfo,
    days: List<TimelineDay>,
    selectedDayId: String,
    onDaySelected: (String) -> Unit,
    onCompleteSprintClick: () -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        tonalElevation = 1.dp,
        shadowElevation = 1.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Title and status
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
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
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = sprint.dateRange,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                // Status badge and action button
                if (sprint.status != SprintStatus.COMPLETED) {
                    Box(
                        modifier = Modifier
                            .clip(MaterialTheme.shapes.medium)
                            .background(sprint.status.containerColor)
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = sprint.status.label,
                            style = MaterialTheme.typography.labelLarge,
                            color = sprint.status.textColor
                        )
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(AgileLifeTheme.extendedColors.accentMint)
                            .padding(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Check,
                            contentDescription = "Completed",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Progress section
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Progress bar
                LinearProgressIndicator(
                    progress = { sprint.progressPercent },
                    modifier = Modifier
                        .weight(1f)
                        .height(8.dp)
                        .clip(MaterialTheme.shapes.small),
                    color = sprint.status.color,
                    trackColor = MaterialTheme.colorScheme.surfaceContainerHighest
                )
                
                Spacer(modifier = Modifier.width(16.dp))
                
                // Percentage
                Text(
                    text = "${(sprint.progressPercent * 100).toInt()}%",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            // Task completion
            Text(
                text = "${sprint.tasksCompleted} of ${sprint.totalTasks} tasks completed",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Only show timeline if day is selected (Calendar tab active)
            if (selectedDayId.isNotEmpty()) {
                // Day timeline
                HorizontalTimeline(
                    days = days,
                    selectedDayId = selectedDayId,
                    onDaySelected = onDaySelected
                )
            }
            
            // "Complete Sprint" button for active sprints
            if (sprint.status == SprintStatus.ACTIVE) {
                Spacer(modifier = Modifier.height(16.dp))
                Surface(
                    onClick = onCompleteSprintClick,
                    color = AgileLifeTheme.extendedColors.accentMint.copy(alpha = 0.1f),
                    contentColor = AgileLifeTheme.extendedColors.accentMint,
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Check,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Complete Sprint & Review",
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun OverviewTab(sprint: SprintInfo) {
    val scrollState = rememberScrollState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Sprint Description Card
        ExpressiveCard(
            containerColor = MaterialTheme.colorScheme.surface,
            elevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Description",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = SampleSprintDetailData.getSprintDescription(sprint.id),
                style = MaterialTheme.typography.bodyLarge
            )
        }
        
        // Sprint Stats Card
        ExpressiveCard(
            containerColor = MaterialTheme.colorScheme.surface,
            elevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Sprint Statistics",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Task completion stat
                StatItem(
                    value = "${sprint.tasksCompleted}/${sprint.totalTasks}",
                    label = "Tasks Completed",
                    color = AgileLifeTheme.extendedColors.accentCoral
                )
                
                // Days remaining stat
                StatItem(
                    value = SampleSprintDetailData.getDaysRemaining(sprint.id).toString(),
                    label = "Days Remaining",
                    color = AgileLifeTheme.extendedColors.accentLavender
                )
                
                // Progress stat
                StatItem(
                    value = "${(sprint.progressPercent * 100).toInt()}%",
                    label = "Progress",
                    color = AgileLifeTheme.extendedColors.accentMint
                )
            }
        }
        
        // Sprint Goals Card
        ExpressiveCard(
            containerColor = MaterialTheme.colorScheme.surface,
            elevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Sprint Goals",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SampleSprintDetailData.getSprintGoals(sprint.id).forEach { goal ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Bullet point
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary)
                        )
                        
                        Spacer(modifier = Modifier.width(12.dp))
                        
                        Text(
                            text = goal,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }
        
        // Bottom spacing
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun CalendarTab(
    days: List<TimelineDay>,
    tasks: List<TaskInfo>,
    onTaskClick: (String) -> Unit,
    onDayClick: (String) -> Unit
) {
    // In a real implementation, this would filter tasks by the selected day
    // For this example, we'll just show a placeholder
    
    val selectedDay = days.find { it.isToday } ?: days.firstOrNull()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (selectedDay != null) {
            Text(
                text = "Calendar view for ${selectedDay.dayOfWeek}, ${selectedDay.dayOfMonth}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            // This would be a proper calendar view in the real implementation
            // For now, just show some tasks that are scheduled for the selected day
            val dayTasks = tasks.take(3) // Just take a few for the example
            
            if (dayTasks.isNotEmpty()) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    dayTasks.forEach { task ->
                        TaskCard(
                            title = task.title,
                            priority = task.priority,
                            dueDate = task.dueDate,
                            estimatedMinutes = task.estimatedMinutes,
                            isCompleted = task.isCompleted,
                            onClick = { onTaskClick(task.id) }
                        )
                    }
                }
            } else {
                // Empty state
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No tasks scheduled for this day",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BacklogTab(
    tasks: List<TaskInfo>,
    onTaskClick: (String) -> Unit
) {
    // Filter state
    var selectedFilter by remember { mutableStateOf(BacklogFilter.ALL) }
    
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Filter chips
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 1.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Rounded.FilterList,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                
                BacklogFilter.values().forEach { filter ->
                    FilterChip(
                        selected = selectedFilter == filter,
                        onClick = { selectedFilter = filter },
                        label = { Text(filter.label) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )
                }
            }
        }
        
        // Task list
        val filteredTasks = when (selectedFilter) {
            BacklogFilter.ALL -> tasks
            BacklogFilter.SCHEDULED -> tasks.filter { it.dueDate != null }
            BacklogFilter.UNSCHEDULED -> tasks.filter { it.dueDate == null }
            BacklogFilter.COMPLETED -> tasks.filter { it.isCompleted }
        }
        
        if (filteredTasks.isEmpty()) {
            // Empty state
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No ${selectedFilter.label.lowercase()} tasks in backlog",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredTasks) { task ->
                    TaskCard(
                        title = task.title,
                        priority = task.priority,
                        dueDate = task.dueDate,
                        estimatedMinutes = task.estimatedMinutes,
                        isCompleted = task.isCompleted,
                        onClick = { onTaskClick(task.id) }
                    )
                }
                
                // Add bottom spacing for FAB
                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }
}

@Composable
private fun StatItem(
    value: String,
    label: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Circular indicator
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(80.dp)
                .padding(8.dp)
        ) {
            CircularProgressIndicator(
                progress = { 1f },
                color = color.copy(alpha = 0.2f),
                strokeWidth = 4.dp,
                modifier = Modifier.fillMaxSize()
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
        
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

/**
 * Tabs in the Sprint Detail screen
 */
enum class SprintDetailTab(val title: String, val icon: ImageVector) {
    OVERVIEW("Overview", Icons.Filled.Description),
    CALENDAR("Calendar", Icons.Filled.DateRange),
    BACKLOG("Backlog", Icons.Filled.FormatListBulleted)
}

/**
 * Filter options for the backlog tab
 */
enum class BacklogFilter(val label: String) {
    ALL("All"),
    SCHEDULED("Scheduled"),
    UNSCHEDULED("Unscheduled"),
    COMPLETED("Completed")
}

/**
 * Sample data for the sprint detail screen
 */
object SampleSprintDetailData {
    fun getSprint(sprintId: String): SprintInfo {
        return SampleSprintData.allSprints.find { it.id == sprintId }
            ?: SprintInfo(
                id = "sprint-1",
                name = "Sprint 23: Dashboard Implementation",
                status = SprintStatus.ACTIVE,
                dateRange = "May 14 - May 28",
                progressPercent = 0.35f,
                tasksCompleted = 7,
                totalTasks = 20
            )
    }
    
    fun getSprintDays(sprintId: String): List<TimelineDay> {
        return listOf(
            TimelineDay(
                id = "2025-05-13",
                dayOfWeek = "Mon",
                dayOfMonth = "13",
                isPast = true,
                completionPercentage = 0.9f
            ),
            TimelineDay(
                id = "2025-05-14",
                dayOfWeek = "Tue",
                dayOfMonth = "14",
                isToday = true
            ),
            TimelineDay(
                id = "2025-05-15",
                dayOfWeek = "Wed",
                dayOfMonth = "15"
            ),
            TimelineDay(
                id = "2025-05-16",
                dayOfWeek = "Thu",
                dayOfMonth = "16"
            ),
            TimelineDay(
                id = "2025-05-17",
                dayOfWeek = "Fri",
                dayOfMonth = "17"
            ),
            TimelineDay(
                id = "2025-05-18",
                dayOfWeek = "Sat",
                dayOfMonth = "18"
            ),
            TimelineDay(
                id = "2025-05-19",
                dayOfWeek = "Sun",
                dayOfMonth = "19"
            ),
            TimelineDay(
                id = "2025-05-20",
                dayOfWeek = "Mon",
                dayOfMonth = "20"
            ),
            TimelineDay(
                id = "2025-05-21",
                dayOfWeek = "Tue",
                dayOfMonth = "21"
            )
        )
    }
    
    fun getSprintTasks(sprintId: String): List<TaskInfo> {
        return listOf(
            TaskInfo(
                id = "task-1",
                title = "Implement Material 3 Expressive Theme",
                priority = TaskPriority.HIGH,
                dueDate = "Today",
                estimatedMinutes = 90,
                isCompleted = false
            ),
            TaskInfo(
                id = "task-2",
                title = "Create Dashboard Components",
                priority = TaskPriority.MEDIUM,
                dueDate = "Tomorrow",
                estimatedMinutes = 120,
                isCompleted = false
            ),
            TaskInfo(
                id = "task-3",
                title = "Write Unit Tests for Timeline Component",
                priority = TaskPriority.LOW,
                dueDate = "May 18",
                estimatedMinutes = 60,
                isCompleted = false
            ),
            TaskInfo(
                id = "task-4",
                title = "Implement Navigation Architecture",
                priority = TaskPriority.MEDIUM,
                dueDate = null,
                estimatedMinutes = 180,
                isCompleted = false
            ),
            TaskInfo(
                id = "task-5",
                title = "Create UI Element Library",
                priority = TaskPriority.HIGH,
                dueDate = "Yesterday",
                estimatedMinutes = 240,
                isCompleted = true
            ),
            TaskInfo(
                id = "task-6",
                title = "Implement Login Screen",
                priority = TaskPriority.MEDIUM,
                dueDate = "May 13",
                estimatedMinutes = 120,
                isCompleted = true
            ),
            TaskInfo(
                id = "task-7",
                title = "Setup Project Structure",
                priority = TaskPriority.HIGH,
                dueDate = "May 12",
                estimatedMinutes = 60,
                isCompleted = true
            )
        )
    }
    
    fun getSprintDescription(sprintId: String): String {
        return "In this sprint, we are focusing on implementing the main dashboard interface and its core components. This includes creating the timeline view, sprint summary cards, and quick-action FAB. We'll also set up the navigation architecture and shared UI components that will be used throughout the app."
    }
    
    fun getSprintGoals(sprintId: String): List<String> {
        return listOf(
            "Complete the Material 3 Expressive design system implementation",
            "Implement dashboard with timeline and quick actions",
            "Create reusable card components for sprints and tasks",
            "Establish navigation architecture for the entire app",
            "Setup automated tests for key components"
        )
    }
    
    fun getDaysRemaining(sprintId: String): Int {
        return 14
    }
}
