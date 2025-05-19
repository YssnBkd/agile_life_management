package com.example.agilelifemanagement.ui.screens.day

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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.agilelifemanagement.ui.screens.day.viewmodel.DayViewModel
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.agilelifemanagement.ui.components.day.DayMoodCard
import com.example.agilelifemanagement.ui.components.day.DayTasks
import com.example.agilelifemanagement.ui.components.timeline.TimeBlock
import com.example.agilelifemanagement.ui.theme.AgileLifeTheme
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * DayDetailScreen shows a comprehensive view of a selected day with timeline, tasks, 
 * and wellness metrics following Material 3 Expressive design principles.
 * 
 * This implementation uses extracted components for better maintainability.
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
    // Use the DayViewModel to fetch real data
    val viewModel = hiltViewModel<DayViewModel>()
    val uiState by viewModel.uiState.collectAsState()
    
    // Load day data based on the dayId (which contains the date)
    LaunchedEffect(dayId) {
        viewModel.loadDay(LocalDate.parse(dayId))
    }
    
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
                        text = uiState.currentDate.format(DateTimeFormatter.ofPattern("EEEE, MMMM d")),
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
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            uiState.errorMessage != null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Error: ${uiState.errorMessage}",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Convert DayActivity to TimeBlock for UI rendering
                    val timeBlocks = uiState.activities.map { activity ->
                        TimeBlock.fromDayActivity(activity)
                    }
                    
                    // Create formatted date for display
                    val formattedDate = uiState.currentDate.format(
                        DateTimeFormatter.ofPattern("MMMM d, yyyy")
                    )
                    
                    // Day header with date
                    Text(
                        text = formattedDate,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    // Day mood and wellness tracker - simplify for MVP
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Daily Mood Tracking - Coming Soon")
                    }
                    
                    // Day timeline with real activities from ViewModel
                    if (uiState.activities.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "No activities scheduled for today. Tap the + button to add an activity.",
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    } else {
                        // Display timeline directly using the timeBlocks
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                        ) {
                            Text(
                                text = "Today's Schedule",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            
                            // Sort timeBlocks by start time
                            val sortedTimeBlocks = timeBlocks.sortedBy { 
                                it.timeRange.substringBefore(" - ") 
                            }
                            
                            sortedTimeBlocks.forEach { block ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = block.timeRange,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.width(90.dp)
                                    )
                                    Surface(
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(8.dp),
                                        color = block.color,
                                        tonalElevation = 2.dp
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(8.dp)
                                        ) {
                                            Text(
                                                text = block.title,
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = FontWeight.Bold
                                            )
                                            if (block.description.isNotEmpty()) {
                                                Text(
                                                    text = block.description,
                                                    style = MaterialTheme.typography.bodySmall
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                            
                            Button(
                                onClick = onEditDayPlanClick,
                                modifier = Modifier
                                    .align(Alignment.End)
                                    .padding(top = 8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Edit Schedule",
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Edit Schedule")
                            }
                        }
                    }
                    
                    // Bottom spacing
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

// Removing DayHeader function as it's no longer needed

@Preview(showBackground = true)
@Composable
private fun DayDetailScreenPreview() {
    AgileLifeTheme {
        // Create a simple preview with mock UI state
        
        DayDetailScreen(
            dayId = "1",
            onBackClick = {},
            onEditDayPlanClick = {},
            onAddTaskClick = {},
            onTaskClick = {},
            onEditMoodClick = {}
        )
    }
}

// Removed custom Box wrapper as it's unnecessary
