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
import com.example.agilelifemanagement.ui.components.timeline.TimeBlockExtensions
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * Day Planner Screen allowing users to plan and manage their daily activities
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DayPlannerScreenSimple(
    navController: NavController,
    selectedDate: LocalDate = LocalDate.now()
) {
    // Sample data for demonstration
    val timeBlocks = remember { 
        mutableStateListOf<TimeBlock>().apply {
            addAll(TimeBlockExtensions.getSampleTimeBlocks())
        }
    }
    
    // Top app bar scroll behavior
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Day Planner: ${selectedDate.format(DateTimeFormatter.ofPattern("E, MMM d"))}") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Rounded.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* Add new activity */ },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Add Activity"
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Text(
                text = "Your Schedule",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(timeBlocks) { timeBlock ->
                    SimpleTimeBlockCard(timeBlock)
                }
            }
        }
    }
}

@Composable
private fun SimpleTimeBlockCard(timeBlock: TimeBlock) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = timeBlock.title,
                style = MaterialTheme.typography.titleMedium
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = timeBlock.timeRange,
                style = MaterialTheme.typography.bodySmall
            )
            
            if (timeBlock.description.isNotEmpty()) {
                Text(
                    text = timeBlock.description,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
            
            if (timeBlock.location.isNotEmpty()) {
                Text(
                    text = "Location: ${timeBlock.location}",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.End
            ) {
                AssistChip(
                    onClick = { /* Toggle completion */ },
                    label = { Text(timeBlock.category.label) },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                )
            }
        }
    }
}
