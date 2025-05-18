package com.example.agilelifemanagement.ui.screens.day

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.agilelifemanagement.ui.components.timeline.TimeBlock
import com.example.agilelifemanagement.ui.components.timeline.TimeBlockCategory
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * Simplified version of the DayPlannerScreen for navigation testing
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DayPlannerScreenStub(
    navController: NavController,
    selectedDate: LocalDate = LocalDate.now()
) {
    // State for time blocks (using the fixed TimeBlock class)
    val timeBlocks = remember { 
        mutableStateListOf<TimeBlock>().apply {
            addAll(getSampleTimeBlocks())
        }
    }
    
    // Top app bar scroll behavior
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Day Planner: ${selectedDate.format(DateTimeFormatter.ISO_LOCAL_DATE)}") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Rounded.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* Add new time block */ },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Add Time Block"
                )
            }
        }
    ) { innerPadding ->
        // Content area for time blocks
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(timeBlocks) { timeBlock ->
                TimeBlockItem(timeBlock = timeBlock)
            }
        }
    }
}

@Composable
fun TimeBlockItem(timeBlock: TimeBlock) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
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
                    style = MaterialTheme.typography.titleMedium
                )
                
                Text(
                    text = timeBlock.timeRange,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            
            if (timeBlock.description.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = timeBlock.description,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            
            if (timeBlock.location.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Location: ${timeBlock.location}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                AssistChip(
                    onClick = { /* Toggle completion */ },
                    label = { Text(text = timeBlock.category.label) },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
                    )
                )
            }
        }
    }
}

// Sample data
private fun getSampleTimeBlocks(): List<TimeBlock> {
    return listOf(
        TimeBlock(
            id = "1",
            title = "Morning Standup",
            description = "Daily team sync",
            location = "Conference Room A",
            timeRange = "9:00 AM - 9:30 AM",
            category = TimeBlockCategory.MEETING
        ),
        TimeBlock(
            id = "2",
            title = "Work on UI Designs",
            description = "Finish dashboard mockups",
            timeRange = "10:00 AM - 12:00 PM",
            category = TimeBlockCategory.TASK
        ),
        TimeBlock(
            id = "3",
            title = "Lunch Break",
            timeRange = "12:00 PM - 1:00 PM",
            category = TimeBlockCategory.BREAK
        ),
        TimeBlock(
            id = "4",
            title = "Workout",
            description = "Cardio + Strength",
            location = "Gym",
            timeRange = "5:30 PM - 6:30 PM",
            category = TimeBlockCategory.PERSONAL
        )
    )
}
