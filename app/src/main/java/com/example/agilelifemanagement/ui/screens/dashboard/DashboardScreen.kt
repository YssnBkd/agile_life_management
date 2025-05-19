package com.example.agilelifemanagement.ui.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Flag
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.RunCircle
import androidx.compose.material.icons.rounded.SentimentSatisfied
import androidx.compose.material.icons.rounded.Today
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.agilelifemanagement.ui.components.dashboard.DashboardActivityItem
import com.example.agilelifemanagement.ui.components.dashboard.DashboardGoalItem
import com.example.agilelifemanagement.ui.components.dashboard.DashboardSection
import com.example.agilelifemanagement.ui.components.dashboard.DashboardSprintItem
import com.example.agilelifemanagement.ui.components.dashboard.DashboardTaskItem
import com.example.agilelifemanagement.ui.components.dashboard.DashboardWellnessItem
import com.example.agilelifemanagement.ui.components.dashboard.EmptyStateMessage
import com.example.agilelifemanagement.ui.model.toSprintInfo
import com.example.agilelifemanagement.ui.model.toUiModel
import com.example.agilelifemanagement.ui.viewmodel.DashboardViewModel
import java.time.format.DateTimeFormatter

/**
 * The dashboard screen is the main entry point of the app and displays an overview of the user's
 * tasks, sprints, goals, and other relevant information.
 * 
 * This screen connects to the DashboardViewModel to display real user data including:
 * - Active sprints
 * - Tasks and their status
 * - Goals
 * - Daily activities
 * - Wellness data
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onTaskClick: (String) -> Unit,
    onSprintClick: (String) -> Unit,
    onGoalClick: (String) -> Unit,
    onDayActivityClick: (String) -> Unit,
    onWellnessClick: () -> Unit,
    onAllTasksClick: () -> Unit,
    onAllSprintsClick: () -> Unit,
    onAllGoalsClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    // Collect UI state from ViewModel using StateFlow
    val uiState by viewModel.uiState.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = "Dashboard",
                        style = MaterialTheme.typography.headlineMedium
                    )
                },
                actions = {
                    IconButton(onClick = { viewModel.refresh() }) {
                        Icon(
                            imageVector = Icons.Rounded.Refresh,
                            contentDescription = "Refresh Dashboard"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            IconButton(
                onClick = { onAllTasksClick() },
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Add,
                    contentDescription = "Add Task",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Error message if any
                if (uiState.errorMessage != null) {
                    Text(
                        text = uiState.errorMessage!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = MaterialTheme.colorScheme.errorContainer,
                                shape = MaterialTheme.shapes.medium
                            )
                            .padding(16.dp)
                    )
                }
                
                // Current Date Display
                Text(
                    text = uiState.selectedDate.format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy")),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                // Today's Tasks Section
                DashboardSection(
                    title = "Today's Tasks",
                    icon = Icons.Rounded.Today,
                    viewAllAction = onAllTasksClick
                ) {
                    if (uiState.todaysTasks.isEmpty()) {
                        EmptyStateMessage(message = "No tasks scheduled for today")
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            uiState.todaysTasks.take(3).forEach { task ->
                                DashboardTaskItem(
                                    task = task,
                                    onClick = { onTaskClick(task.id) }
                                )
                            }
                            
                            if (uiState.todaysTasks.size > 3) {
                                Text(
                                    text = "+ ${uiState.todaysTasks.size - 3} more tasks",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { onAllTasksClick() }
                                        .padding(8.dp),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
                
                // Active Sprint Section
                DashboardSection(
                    title = "Active Sprint",
                    icon = Icons.Rounded.RunCircle,
                    viewAllAction = onAllSprintsClick
                ) {
                    if (uiState.activeSprint == null) {
                        EmptyStateMessage(message = "No active sprint")
                    } else {
                        val sprintInfo = uiState.activeSprint!!.toSprintInfo(uiState.sprintTasks)
                        DashboardSprintItem(
                            sprint = uiState.activeSprint!!,
                            taskCount = sprintInfo.totalTasks,
                            completedTaskCount = sprintInfo.tasksCompleted,
                            onClick = { onSprintClick(uiState.activeSprint!!.id) }
                        )
                    }
                }
                
                // Goals Section
                if (uiState.activeGoals.isNotEmpty()) {
                    DashboardSection(
                        title = "Goals",
                        icon = Icons.Rounded.Flag,
                        viewAllAction = onAllGoalsClick
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            uiState.activeGoals.take(2).forEach { goal ->
                                val goalUi = goal.toUiModel()
                                DashboardGoalItem(
                                    goal = goalUi,
                                    onClick = { onGoalClick(goal.id) }
                                )
                            }
                            
                            if (uiState.activeGoals.size > 2) {
                                Text(
                                    text = "+ ${uiState.activeGoals.size - 2} more goals",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { onAllGoalsClick() }
                                        .padding(8.dp),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
                
                // Today's Activities Section
                if (uiState.todaysActivities.isNotEmpty()) {
                    DashboardSection(
                        title = "Today's Schedule",
                        icon = Icons.Rounded.Today,
                        viewAllAction = { onDayActivityClick(uiState.selectedDate.toString()) }
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            uiState.todaysActivities.take(3).forEach { activity ->
                                val activityUi = activity.toUiModel()
                                DashboardActivityItem(
                                    activity = activityUi,
                                    onClick = { onDayActivityClick(uiState.selectedDate.toString()) }
                                )
                            }
                            
                            if (uiState.todaysActivities.size > 3) {
                                Text(
                                    text = "+ ${uiState.todaysActivities.size - 3} more activities",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { onDayActivityClick(uiState.selectedDate.toString()) }
                                        .padding(8.dp),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
                
                // Wellness Section
                uiState.wellnessData?.let { checkup ->
                    DashboardSection(
                        title = "Wellness",
                        icon = Icons.Rounded.SentimentSatisfied,
                        viewAllAction = onWellnessClick
                    ) {
                        DashboardWellnessItem(
                            dailyCheckup = checkup,
                            onClick = onWellnessClick
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
