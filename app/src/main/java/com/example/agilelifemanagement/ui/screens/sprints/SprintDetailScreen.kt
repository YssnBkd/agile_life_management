package com.example.agilelifemanagement.ui.screens.sprints

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import com.example.agilelifemanagement.domain.model.Sprint
import com.example.agilelifemanagement.domain.model.Tag

private const val TAG = "SprintDetailScreen"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SprintDetailScreen(
    sprintId: String,
    viewModel: SprintsViewModel = androidx.hilt.navigation.compose.hiltViewModel(),
    onBack: () -> Unit = {},
    onEdit: (Sprint) -> Unit = {}, // Still provided for external navigation if needed
    navigateToTask: (String) -> Unit = {},
    navigateToGoal: (String) -> Unit = {}
) {
    val selectedSprintState by viewModel.selectedSprintState.collectAsState()
    val tags by viewModel.tags.collectAsState()
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    val dateFormatter = remember { DateTimeFormatter.ofPattern("yyyy-MM-dd") }
    val snackbarHostState = remember { SnackbarHostState() }

    // Diagnostic logging
    LaunchedEffect(Unit) {
        Log.d(TAG, "Entering SprintDetailScreen for sprintId: $sprintId")
    }

    LaunchedEffect(sprintId) {
        if (sprintId.isNotEmpty()) {
            Log.d(TAG, "Requesting sprint details for sprintId: $sprintId")
            viewModel.selectSprint(sprintId)
        } else {
            Log.w(TAG, "sprintId is empty, cannot fetch sprint details.")
        }
    }

    LaunchedEffect(selectedSprintState) {
        Log.d(TAG, "selectedSprintState changed: $selectedSprintState")
    }
    
    // Handle snackbar messages
    LaunchedEffect(viewModel.snackbarMessage) {
        val message = viewModel.snackbarMessage
        if (message != null) {
            snackbarHostState.showSnackbar(message, duration = SnackbarDuration.Short)
            viewModel.clearSnackbar()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    val title = when (val state = selectedSprintState) {
                        is SprintsViewModel.SprintDetailUiState.Success -> state.sprint.name
                        else -> "Sprint Detail"
                    }
                    Text(title)
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (selectedSprintState is SprintsViewModel.SprintDetailUiState.Success) {
                        val sprint = (selectedSprintState as SprintsViewModel.SprintDetailUiState.Success).sprint
                        IconButton(onClick = { showEditDialog = true }) {
                            Icon(Icons.Filled.Edit, contentDescription = "Edit Sprint")
                        }
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(Icons.Filled.Delete, contentDescription = "Delete Sprint")
                        }
                    }
                }
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when (val state = selectedSprintState) {
                is SprintsViewModel.SprintDetailUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is SprintsViewModel.SprintDetailUiState.Success -> {
                    val sprint = state.sprint
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp)
                    ) {
                        // Sprint Header
                        Text(sprint.name, style = MaterialTheme.typography.headlineMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        // Status & Date Chips
                        Row(modifier = Modifier.padding(vertical = 8.dp)) {
                            if (sprint.isActive) {
                                Surface(
                                    shape = RoundedCornerShape(16.dp),
                                    color = MaterialTheme.colorScheme.primaryContainer,
                                    modifier = Modifier.padding(end = 8.dp)
                                ) {
                                    Text(
                                        text = "ACTIVE",
                                        style = MaterialTheme.typography.labelMedium,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                    )
                                }
                            }
                            
                            if (sprint.isCompleted) {
                                Surface(
                                    shape = RoundedCornerShape(16.dp),
                                    color = MaterialTheme.colorScheme.tertiaryContainer,
                                    modifier = Modifier.padding(end = 8.dp)
                                ) {
                                    Text(
                                        text = "COMPLETED",
                                        style = MaterialTheme.typography.labelMedium,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                    )
                                }
                            }
                            
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.padding(end = 8.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Icon(
                                        Icons.Filled.CalendarMonth,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "${ChronoUnit.DAYS.between(sprint.startDate, sprint.endDate) + 1} days",
                                        style = MaterialTheme.typography.labelMedium
                                    )
                                }
                            }
                        }
                        
                        // Date range
                        Row(modifier = Modifier.padding(vertical = 8.dp)) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.surface,
                                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                                ) {
                                    Icon(
                                        Icons.Filled.DateRange,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "${sprint.startDate.format(dateFormatter)} - ${sprint.endDate.format(dateFormatter)}",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }
                        
                        // Summary
                        if (sprint.summary.isNotEmpty()) {
                            Text("Summary", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 16.dp))
                            Text(
                                text = sprint.summary,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                        
                        // Description
                        if (sprint.description.isNotEmpty()) {
                            Text("Description", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 16.dp))
                            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                                sprint.description.forEach { descLine ->
                                    Row(verticalAlignment = Alignment.Top) {
                                        Text("•", style = MaterialTheme.typography.bodyMedium)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(descLine, style = MaterialTheme.typography.bodyMedium)
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                }
                            }
                        }
                        
                        // Tasks and Goals Sections
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Sprint Contents", style = MaterialTheme.typography.titleMedium)
                                Spacer(modifier = Modifier.height(8.dp))
                                
                                // Tasks (placeholder)
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                                ) {
                                    Text("Tasks", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                                    // TODO: Replace with actual task count
                                    Text("0 assigned", style = MaterialTheme.typography.bodyMedium)
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                
                                // If there were tasks, you would display them here
                                Text("No tasks assigned to this sprint.", 
                                    style = MaterialTheme.typography.bodyMedium, 
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f))
                                
                                Divider(modifier = Modifier.padding(vertical = 16.dp))
                                
                                // Goals (placeholder)
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                                ) {
                                    Text("Goals", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                                    // TODO: Replace with actual goal count
                                    Text("0 assigned", style = MaterialTheme.typography.bodyMedium)
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                
                                // If there were goals, you would display them here
                                Text("No goals assigned to this sprint.", 
                                    style = MaterialTheme.typography.bodyMedium, 
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f))
                            }
                        }
                        
                        // Action Buttons
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = { showEditDialog = true },
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    Icons.Filled.Edit,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Edit")
                            }
                            
                            // Toggle Active/Completed button
                            if (!sprint.isCompleted) {
                                Button(
                                    onClick = { 
                                        val updatedSprint = sprint.copy(isCompleted = true, isActive = false)
                                        viewModel.updateSelectedSprint(updatedSprint)
                                    },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(
                                        Icons.Filled.Check,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Complete")
                                }
                            } else {
                                Button(
                                    onClick = { 
                                        // TODO: Navigate to Sprint Review
                                    },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(
                                        Icons.Filled.InsertChartOutlined,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Review")
                                }
                            }
                        }
                    }
                }
                is SprintsViewModel.SprintDetailUiState.NotFound -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Sprint not found.", color = MaterialTheme.colorScheme.error)
                    }
                }
                is SprintsViewModel.SprintDetailUiState.Error -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Error: ${state.message}", color = MaterialTheme.colorScheme.error)
                    }
                }
                is SprintsViewModel.SprintDetailUiState.Idle -> {
                    // Initial state, usually quickly replaced by Loading or other states
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Select a sprint to see details.")
                    }
                }
            }
            // Delete confirmation dialog
            if (showDeleteDialog && selectedSprintState is SprintsViewModel.SprintDetailUiState.Success) {
                AlertDialog(
                    onDismissRequest = { showDeleteDialog = false },
                    title = { Text("Delete Sprint") },
                    text = { Text("Are you sure you want to delete this sprint?") },
                    confirmButton = {
                        Button(
                            onClick = {
                                viewModel.deleteSelectedSprint()
                                showDeleteDialog = false
                                onBack() // Navigate back after deletion
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                        ) { Text("Delete") }
                    },
                    dismissButton = {
                        OutlinedButton(onClick = { showDeleteDialog = false }) { Text("Cancel") }
                    }
                )
            }
            
            // Edit dialog
            if (showEditDialog && selectedSprintState is SprintsViewModel.SprintDetailUiState.Success) {
                val sprint = (selectedSprintState as SprintsViewModel.SprintDetailUiState.Success).sprint
                SprintEditDialog(
                    sprint = sprint,
                    availableTags = tags,
                    onDismiss = { showEditDialog = false },
                    onSave = { updatedSprint ->
                        viewModel.updateSelectedSprint(updatedSprint)
                        showEditDialog = false
                    }
                )
            }
        }
    }
}
