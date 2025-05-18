package com.example.agilelifemanagement.ui.screens.day

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.agilelifemanagement.ui.components.timeline.TimeBlock
import com.example.agilelifemanagement.ui.components.timeline.TimeBlockExtensions
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * Simplified version of the DayDetailScreen for navigation testing
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DayDetailScreenStub(
    navController: NavController,
    selectedDate: LocalDate = LocalDate.now()
) {
    // Sample data for the day's activities
    val timeBlocks = remember { TimeBlockExtensions.getSampleTimeBlocks() }
    
    // Top app bar scroll behavior
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Day Detail: ${selectedDate.format(DateTimeFormatter.ofPattern("EEE, MMM d"))}") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Rounded.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* Edit day */ }) {
                        Icon(
                            imageVector = Icons.Rounded.Edit,
                            contentDescription = "Edit Day"
                        )
                    }
                    IconButton(onClick = { /* Show calendar */ }) {
                        Icon(
                            imageVector = Icons.Rounded.CalendarMonth,
                            contentDescription = "Show Calendar"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = { Text("Add Activity") },
                icon = { 
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add activity"
                    )
                },
                onClick = { /* Add new activity */ },
                expanded = true,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        }
    ) { innerPadding ->
        // Content area for time blocks
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "Day Summary",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(top = 16.dp)
                )
                
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Activities: ${timeBlocks.size}",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = "Completed: ${timeBlocks.count { it.isCompleted }}/${timeBlocks.size}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Timeline",
                    style = MaterialTheme.typography.headlineSmall
                )
            }
            
            items(timeBlocks) { timeBlock ->
                TimeBlockCard(timeBlock = timeBlock)
            }
            
            item {
                Spacer(modifier = Modifier.height(80.dp)) // Space for FAB
            }
        }
    }
}

@Composable
fun TimeBlockCard(timeBlock: TimeBlock) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Time column
            Column(
                horizontalAlignment = Alignment.End,
                modifier = Modifier.width(70.dp)
            ) {
                val timeParts = timeBlock.timeRange.split(" - ")
                Text(
                    text = timeParts.firstOrNull() ?: "",
                    style = MaterialTheme.typography.bodyMedium
                )
                if (timeParts.size > 1) {
                    Text(
                        text = timeParts[1],
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Content column
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = timeBlock.title,
                    style = MaterialTheme.typography.titleMedium
                )
                
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
                        text = "📍 ${timeBlock.location}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            // Status
            Checkbox(
                checked = timeBlock.isCompleted,
                onCheckedChange = { /* Toggle completion */ },
                colors = CheckboxDefaults.colors(
                    checkedColor = MaterialTheme.colorScheme.primary,
                    uncheckedColor = MaterialTheme.colorScheme.outline
                )
            )
        }
    }
}
