package com.example.agilelifemanagement.ui.screens.task

import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.agilelifemanagement.domain.model.TaskStatus

/**
 * Simple TaskViewModel implementation to be replaced later
 */
class TaskViewModel {
    data class TaskUiState(
        val backlogTasks: List<com.example.agilelifemanagement.domain.model.Task> = emptyList(),
        val isLoading: Boolean = false,
        val errorMessage: String? = null
    )
    
    val uiState = TaskUiState()
    
    fun refresh() {
        // To be implemented
    }
}

/**
 * Shows a loading indicator
 */
@Composable
fun LoadingIndicator() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

/**
 * Shows an error message with retry button
 */
@Composable
fun ErrorMessage(
    message: String,
    onRetryClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Error",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.error
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(onClick = onRetryClick) {
            Text(text = "Retry")
        }
    }
}

/**
 * Shows an empty state message with action button
 */
@Composable
fun EmptyStateMessage(
    title: String,
    message: String,
    actionLabel: String,
    onActionClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(onClick = onActionClick) {
            Text(text = actionLabel)
        }
    }
}

/**
 * Screen that displays all backlog tasks that aren't assigned to a sprint
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskBacklogScreen(
    onBackClick: () -> Unit,
    onTaskClick: (String) -> Unit,
    onCreateTaskClick: () -> Unit,
    onSprintClick: (String) -> Unit,
    onCreateSprintClick: () -> Unit,
    onSearchClick: () -> Unit,
    onProfileClick: () -> Unit,
    viewModel: TaskViewModel = TaskViewModel()
) {
    // Using a simplified approach without Flow
    val uiState = viewModel.uiState
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Task Backlog") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Rounded.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onSearchClick) {
                        Icon(
                            imageVector = Icons.Rounded.Search,
                            contentDescription = "Search Tasks"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onCreateTaskClick,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Create New Task"
                )
            }
        }
    ) { innerPadding ->
        if (uiState.isLoading) {
            LoadingIndicator()
        } else if (uiState.errorMessage != null) {
            ErrorMessage(
                message = uiState.errorMessage!!,
                onRetryClick = { viewModel.refresh() }
            )
        } else if (uiState.backlogTasks.isEmpty()) {
            EmptyStateMessage(
                title = "No Backlog Tasks",
                message = "Your backlog is empty. Create your first task or assign all tasks to sprints.",
                actionLabel = "Create Task",
                onActionClick = onCreateTaskClick
            )
        } else {
            BacklogTaskList(
                tasks = uiState.backlogTasks,
                onTaskClick = onTaskClick,
                contentPadding = innerPadding
            )
        }
    }
}

@Composable
private fun BacklogTaskList(
    tasks: List<com.example.agilelifemanagement.domain.model.Task>,
    onTaskClick: (String) -> Unit,
    contentPadding: PaddingValues
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Text(
                text = "These tasks haven't been assigned to any sprint. Assign them to sprints or break them down into smaller tasks.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
        
        items(tasks) { task ->
            // Simplified TaskCard implementation
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onTaskClick(task.id) },
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = task.title,
                        style = MaterialTheme.typography.titleMedium
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = "Status: ${task.status}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (task.status == TaskStatus.COMPLETED) 
                            MaterialTheme.colorScheme.primary
                        else 
                            MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    if (task.dueDate != null) {
                        Text(
                            text = "Due: ${task.dueDate}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
        
        item {
            Spacer(modifier = Modifier.height(80.dp)) // Space for FAB
        }
    }
}
