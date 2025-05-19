package com.example.agilelifemanagement.ui.screens.day

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.agilelifemanagement.ui.components.timeline.TimeBlock
import com.example.agilelifemanagement.ui.components.timeline.TimeBlockCategory
import com.example.agilelifemanagement.ui.viewmodel.DayPlannerViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.*

/**
 * Enhanced Day Planner Screen with timeline view and advanced features
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnhancedDayPlannerScreen(
    navController: NavController,
    selectedDate: LocalDate = LocalDate.now(),
    viewModel: DayPlannerViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Column {
                        Text(
                            text = "Day Planner",
                            style = MaterialTheme.typography.headlineSmall
                        )
                        Text(
                            text = formatDate(selectedDate),
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Rounded.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* Show date picker */ }) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = "Select Date"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { /* Add activity */ },
                icon = { Icon(Icons.Filled.Add, contentDescription = "Add Activity") },
                text = { Text("Add Activity") },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            // Progress indicator
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Today's Progress",
                        style = MaterialTheme.typography.titleMedium
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    LinearProgressIndicator(
                        progress = { uiState.completionRate },
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "${(uiState.completionRate * 100).toInt()}% Complete",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        
                        Text(
                            text = "${uiState.timeBlocks.size} Activities",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
            
            // Time blocks list
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (uiState.timeBlocks.isEmpty()) {
                EmptyDayView()
            } else {
                Text(
                    text = "Your Schedule",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.timeBlocks) { timeBlock ->
                        EnhancedTimeBlockCard(
                            timeBlock = timeBlock,
                            onTimeBlockClick = { 
                                navController.navigate("day/activities/${timeBlock.id}")
                            },
                            onCompleteClick = {
                                /* Toggle completion */
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EnhancedTimeBlockCard(
    timeBlock: TimeBlock,
    onTimeBlockClick: () -> Unit,
    onCompleteClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (timeBlock.isCompleted) 
                timeBlock.color.copy(alpha = 0.7f)
            else
                timeBlock.color
        ),
        onClick = onTimeBlockClick
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = timeBlock.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                
                IconButton(onClick = onCompleteClick) {
                    Icon(
                        imageVector = if (timeBlock.isCompleted) 
                            Icons.Filled.Add
                        else 
                            Icons.Filled.Add,
                        contentDescription = "Toggle completion",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = timeBlock.timeRange,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
            )
            
            if (timeBlock.description.isNotEmpty()) {
                Text(
                    text = timeBlock.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
            
            if (timeBlock.location.isNotEmpty()) {
                Text(
                    text = "Location: ${timeBlock.location}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun EmptyDayView() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "No Activities Planned",
                style = MaterialTheme.typography.headlineSmall
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Your day is open. Add activities to plan your day.",
                style = MaterialTheme.typography.bodyMedium
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(onClick = { /* Add activity */ }) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Add Your First Activity")
            }
        }
    }
}

/**
 * Formats a date for display
 */
private fun formatDate(date: LocalDate): String {
    val dayOfWeek = date.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault())
    val formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy")
    return "$dayOfWeek, ${date.format(formatter)}"
}
