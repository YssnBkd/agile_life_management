package com.example.agilelifemanagement.ui.screens.dashboard

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.DirectionsRun
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.agilelifemanagement.ui.components.actions.QuickActionFAB
import com.example.agilelifemanagement.ui.components.actions.QuickActions
import com.example.agilelifemanagement.ui.components.cards.ExpressiveCard
import com.example.agilelifemanagement.ui.components.cards.FeatureCard
import com.example.agilelifemanagement.ui.components.cards.SprintCard
import com.example.agilelifemanagement.ui.components.cards.SprintStatus
import com.example.agilelifemanagement.ui.components.cards.TaskCard
import com.example.agilelifemanagement.ui.components.cards.TaskPriority
import com.example.agilelifemanagement.ui.components.timeline.DailyTimelineMiniView
import com.example.agilelifemanagement.ui.components.timeline.HorizontalTimeline
import com.example.agilelifemanagement.ui.components.timeline.TimeBlock
import com.example.agilelifemanagement.ui.components.timeline.TimeBlockCategory
import com.example.agilelifemanagement.ui.components.timeline.TimelineDay
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * Dashboard/Home screen for AgileLifeManagement
 * Follows Material 3 Expressive design principles with:
 * - Bold visual elements with generous spacing
 * - Clear visual hierarchy with distinctive sections
 * - Interactive elements that invite engagement
 * - Purposeful use of color and typography
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onSprintClick: (String) -> Unit,
    onTaskClick: (String) -> Unit,
    onDayClick: (String) -> Unit,
    onCreateTask: () -> Unit,
    onCheckIn: () -> Unit,
    onCreateNote: () -> Unit,
    onSearchClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onProfileClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Sample data - would come from ViewModel in real implementation
    val currentSprint = remember { SampleData.currentSprint }
    val sprintDays = remember { SampleData.sprintDays }
    val todayTimeBlocks = remember { SampleData.todayTimeBlocks }
    val upcomingTasks = remember { SampleData.upcomingTasks }
    
    // Top app bar scroll behavior
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
    
    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        text = "Dashboard",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    IconButton(onClick = onSearchClick) {
                        Icon(
                            imageVector = Icons.Rounded.Search,
                            contentDescription = "Search",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    IconButton(onClick = onNotificationsClick) {
                        Icon(
                            imageVector = Icons.Rounded.Notifications,
                            contentDescription = "Notifications",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    IconButton(onClick = onProfileClick) {
                        Icon(
                            imageVector = Icons.Rounded.AccountCircle,
                            contentDescription = "Profile",
                            modifier = Modifier.size(26.dp)
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    scrolledContainerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            QuickActionFAB(
                actions = QuickActions.AllActions,
                onActionClick = { action ->
                    when (action.id) {
                        QuickActions.NewTask.id -> onCreateTask()
                        QuickActions.CheckIn.id -> onCheckIn()
                        QuickActions.QuickNote.id -> onCreateNote()
                    }
                }
            )
        }
    ) { innerPadding ->
        DashboardContent(
            currentSprint = currentSprint,
            sprintDays = sprintDays,
            todayTimeBlocks = todayTimeBlocks,
            upcomingTasks = upcomingTasks,
            onSprintClick = onSprintClick,
            onDayClick = onDayClick,
            onTaskClick = onTaskClick,
            onViewFullSchedule = { onDayClick(LocalDate.now().toString()) },
            contentPadding = innerPadding
        )
    }
}

@Composable
private fun DashboardContent(
    currentSprint: SprintInfo,
    sprintDays: List<TimelineDay>,
    todayTimeBlocks: List<TimeBlock>,
    upcomingTasks: List<TaskInfo>,
    onSprintClick: (String) -> Unit,
    onDayClick: (String) -> Unit,
    onTaskClick: (String) -> Unit,
    onViewFullSchedule: () -> Unit,
    contentPadding: PaddingValues
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Current Sprint Section with Timeline
        SprintSummarySection(
            sprintInfo = currentSprint,
            days = sprintDays,
            selectedDayId = LocalDate.now().toString(),
            onSprintClick = { onSprintClick(currentSprint.id) },
            onDaySelected = onDayClick
        )
        
        // Today's Timeline Section
        TodayTimelineSection(
            dateTitle = "Today, ${LocalDate.now().format(DateTimeFormatter.ofPattern("MMM d"))}",
            timeBlocks = todayTimeBlocks,
            onViewFullSchedule = onViewFullSchedule
        )
        
        // Upcoming Tasks Section
        UpcomingTasksSection(
            tasks = upcomingTasks,
            onTaskClick = onTaskClick
        )
        
        // Bottom padding
        Spacer(modifier = Modifier.height(80.dp))
    }
}

@Composable
private fun SprintSummarySection(
    sprintInfo: SprintInfo,
    days: List<TimelineDay>,
    selectedDayId: String,
    onSprintClick: () -> Unit,
    onDaySelected: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Sprint header with icon
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.DirectionsRun,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(28.dp)
            )
            
            Text(
                text = "Current Sprint",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
        
        // Sprint card with progress
        SprintCard(
            title = sprintInfo.name,
            status = sprintInfo.status,
            progressPercent = sprintInfo.progressPercent,
            dateRange = sprintInfo.dateRange,
            tasksCompleted = sprintInfo.tasksCompleted,
            totalTasks = sprintInfo.totalTasks,
            onClick = onSprintClick
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Horizontal day timeline
        HorizontalTimeline(
            days = days,
            selectedDayId = selectedDayId,
            onDaySelected = onDaySelected
        )
    }
}

@Composable
private fun TodayTimelineSection(
    dateTitle: String,
    timeBlocks: List<TimeBlock>,
    onViewFullSchedule: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Today's Schedule",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        
        DailyTimelineMiniView(
            dateTitle = dateTitle,
            timeBlocks = timeBlocks,
            onViewFullSchedule = onViewFullSchedule
        )
    }
}

@Composable
private fun UpcomingTasksSection(
    tasks: List<TaskInfo>,
    onTaskClick: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Upcoming Tasks",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        
        if (tasks.isEmpty()) {
            ExpressiveCard(
                containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                elevation = 0.dp
            ) {
                Text(
                    text = "No upcoming tasks",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        } else {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                tasks.forEach { task ->
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
        }
    }
}

// Data classes for sample data
data class SprintInfo(
    val id: String,
    val name: String,
    val status: SprintStatus,
    val dateRange: String,
    val progressPercent: Float,
    val tasksCompleted: Int,
    val totalTasks: Int
)

data class TaskInfo(
    val id: String,
    val title: String,
    val priority: TaskPriority,
    val dueDate: String?,
    val estimatedMinutes: Int?,
    val isCompleted: Boolean
)

/**
 * Sample data for preview and testing
 */
object SampleData {
    val currentSprint = SprintInfo(
        id = "sprint-1",
        name = "Sprint 23: Dashboard Implementation",
        status = SprintStatus.ACTIVE,
        dateRange = "May 14 - May 28",
        progressPercent = 0.35f,
        tasksCompleted = 7,
        totalTasks = 20
    )
    
    val sprintDays = listOf(
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
    
    val todayTimeBlocks = listOf(
        TimeBlock(
            id = "block-1",
            title = "Team Stand-up",
            timeRange = "9:00 AM",
            category = TimeBlockCategory.MEETING
        ),
        TimeBlock(
            id = "block-2",
            title = "Dashboard Implementation",
            timeRange = "10:00 AM",
            category = TimeBlockCategory.TASK
        ),
        TimeBlock(
            id = "block-3",
            title = "Lunch Break",
            timeRange = "12:30 PM",
            category = TimeBlockCategory.BREAK
        ),
        TimeBlock(
            id = "block-4",
            title = "Deep Work: UI Components",
            timeRange = "1:30 PM",
            category = TimeBlockCategory.FOCUS
        )
    )
    
    val upcomingTasks = listOf(
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
        )
    )
}
