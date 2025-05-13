package com.example.agilelifemanagement.ui.screens.tasks

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.agilelifemanagement.domain.model.Task
import com.example.agilelifemanagement.domain.model.Tag
import java.time.format.DateTimeFormatter

private const val TAG = "TaskDetailScreen"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDetailScreen(
    taskId: String,
    viewModel: TasksViewModel = hiltViewModel(),
    onBack: () -> Unit = {},
    onEdit: (Task) -> Unit = {}, // Still provided for external navigation if needed
    navigateToSprint: (String) -> Unit = {},
    navigateToGoal: (String) -> Unit = {}
) {
    val taskDetailState by viewModel.selectedTaskState.collectAsState()
    val tags by viewModel.tags.collectAsState()
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    val dateFormatter = remember { DateTimeFormatter.ofPattern("yyyy-MM-dd") }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        Log.d(TAG, "Entering TaskDetailScreen for taskId: $taskId")
    }

    LaunchedEffect(taskId) {
        if (taskId.isNotEmpty()) {
            Log.d(TAG, "Requesting task details for taskId: $taskId")
            viewModel.selectTask(taskId)
        } else {
            Log.w(TAG, "taskId is empty, cannot fetch task details.")
        }
    }

    LaunchedEffect(taskDetailState) {
        Log.d(TAG, "taskDetailState changed: $taskDetailState")
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
                    val title = when (val state = taskDetailState) {
                        is TasksViewModel.TaskDetailUiState.Success -> state.task.title
                        else -> "Task Detail"
                    }
                    Text(title)
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (taskDetailState is TasksViewModel.TaskDetailUiState.Success) {
                        val task = (taskDetailState as TasksViewModel.TaskDetailUiState.Success).task
                        IconButton(onClick = { showEditDialog = true }) {
                            Icon(Icons.Filled.Edit, contentDescription = "Edit Task")
                        }
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(Icons.Filled.Delete, contentDescription = "Delete Task")
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
            when (val state = taskDetailState) {
                is TasksViewModel.TaskDetailUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is TasksViewModel.TaskDetailUiState.Success -> {
                    val task = state.task
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp)
                    ) {
                        // Task Header
                        Text(task.title, style = MaterialTheme.typography.headlineMedium)
                        Spacer(modifier = Modifier.height(4.dp))
                        
                        // Status & Priority Chips
                        Row(modifier = Modifier.padding(vertical = 8.dp)) {
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = when(task.status) {
                                    Task.Status.DONE -> MaterialTheme.colorScheme.primaryContainer
                                    Task.Status.IN_PROGRESS -> MaterialTheme.colorScheme.tertiaryContainer
                                    Task.Status.BLOCKED -> MaterialTheme.colorScheme.errorContainer
                                    else -> MaterialTheme.colorScheme.surfaceVariant
                                },
                                modifier = Modifier.padding(end = 8.dp)
                            ) {
                                Text(
                                    text = task.status.name,
                                    style = MaterialTheme.typography.labelMedium,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }
                            
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = when(task.priority) {
                                    Task.Priority.URGENT -> MaterialTheme.colorScheme.errorContainer
                                    Task.Priority.HIGH -> MaterialTheme.colorScheme.error.copy(alpha = 0.2f)
                                    Task.Priority.MEDIUM -> MaterialTheme.colorScheme.secondaryContainer
                                    Task.Priority.LOW -> MaterialTheme.colorScheme.surfaceVariant
                                },
                                modifier = Modifier.padding(end = 8.dp)
                            ) {
                                Text(
                                    text = task.priority.name,
                                    style = MaterialTheme.typography.labelMedium,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }
                            
                            if (task.dueDate != null) {
                                Surface(
                                    shape = RoundedCornerShape(16.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                    ) {
                                        Icon(
                                            Icons.Filled.DateRange,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = task.dueDate.format(dateFormatter),
                                            style = MaterialTheme.typography.labelMedium
                                        )
                                    }
                                }
                            }
                        }
                        
                        // Tags
                        if (task.tags.isNotEmpty()) {
                            Text("Tags", style = MaterialTheme.typography.titleSmall)
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.padding(vertical = 8.dp)
                            ) {
                                items(task.tags) { tagId ->
                                    val tag = tags.find { it.id == tagId }
                                    val tagColor = tag?.color?.let { Color(android.graphics.Color.parseColor(it)) } 
                                        ?: MaterialTheme.colorScheme.primary
                                    
                                    Surface(
                                        shape = RoundedCornerShape(16.dp),
                                        color = tagColor.copy(alpha = 0.2f)
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(8.dp)
                                                    .clip(CircleShape)
                                                    .background(tagColor)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = tag?.name ?: "Unknown",
                                                style = MaterialTheme.typography.labelSmall
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        
                        // Summary
                        if (task.summary.isNotEmpty()) {
                            Text("Summary", style = MaterialTheme.typography.titleSmall)
                            Text(
                                text = task.summary,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                        
                        // Description
                        if (task.description.isNotEmpty()) {
                            Text("Description", style = MaterialTheme.typography.titleSmall)
                            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                                task.description.forEach { descLine ->
                                    Row(verticalAlignment = Alignment.Top) {
                                        Text("•", style = MaterialTheme.typography.bodyMedium)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(descLine, style = MaterialTheme.typography.bodyMedium)
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                }
                            }
                        }
                        
                        // Details Section
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Details", style = MaterialTheme.typography.titleMedium)
                                Spacer(modifier = Modifier.height(8.dp))
                                
                                // Effort
                                Row(modifier = Modifier.padding(vertical = 4.dp)) {
                                    Text("Effort Estimate:", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("${task.estimatedEffort} points", style = MaterialTheme.typography.bodyMedium)
                                }
                                
                                // Sprint
                                if (task.sprintId != null) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(vertical = 4.dp)
                                    ) {
                                        Text("Sprint:", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        TextButton(
                                            onClick = { navigateToSprint(task.sprintId) },
                                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                                        ) {
                                            Text("View Sprint", style = MaterialTheme.typography.bodyMedium)
                                            Icon(
                                                Icons.Filled.ArrowForward,
                                                contentDescription = null,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                }
                                
                                // Goal
                                if (task.goalId != null) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(vertical = 4.dp)
                                    ) {
                                        Text("Goal:", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        TextButton(
                                            onClick = { navigateToGoal(task.goalId) },
                                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                                        ) {
                                            Text("View Goal", style = MaterialTheme.typography.bodyMedium)
                                            Icon(
                                                Icons.Filled.ArrowForward,
                                                contentDescription = null,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                }
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
                            
                            Button(
                                onClick = { 
                                    // TODO: Implement mark as done functionality
                                    val updatedTask = task.copy(status = Task.Status.DONE)
                                    viewModel.updateSelectedTask(updatedTask)
                                },
                                modifier = Modifier.weight(1f),
                                enabled = task.status != Task.Status.DONE
                            ) {
                                Icon(
                                    Icons.Filled.Check,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(if (task.status == Task.Status.DONE) "Done" else "Mark Done")
                            }
                        }
                    }
                }
                is TasksViewModel.TaskDetailUiState.NotFound -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Task not found.", color = MaterialTheme.colorScheme.error)
                    }
                }
                is TasksViewModel.TaskDetailUiState.Error -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Error: ${state.message}", color = MaterialTheme.colorScheme.error)
                    }
                }
                is TasksViewModel.TaskDetailUiState.Idle -> {
                    // Initial state, usually quickly replaced by Loading or other states
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Select a task to see details.")
                    }
                }
            }

            // Delete confirmation dialog
            if (showDeleteDialog && taskDetailState is TasksViewModel.TaskDetailUiState.Success) {
                AlertDialog(
                    onDismissRequest = { showDeleteDialog = false },
                    title = { Text("Delete Task") },
                    text = { Text("Are you sure you want to delete this task?") },
                    confirmButton = {
                        Button(
                            onClick = {
                                viewModel.deleteSelectedTask()
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
            if (showEditDialog && taskDetailState is TasksViewModel.TaskDetailUiState.Success) {
                val task = (taskDetailState as TasksViewModel.TaskDetailUiState.Success).task
                TaskEditDialog(
                    task = task,
                    availableTags = tags,
                    onDismiss = { showEditDialog = false },
                    onSave = { updatedTask ->
                        viewModel.updateSelectedTask(updatedTask)
                        showEditDialog = false
                    }
                )
            }
        }
    }
}
