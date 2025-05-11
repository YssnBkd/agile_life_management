@file:OptIn(ExperimentalMaterial3Api::class)
package com.example.agilelifemanagement.ui.screens.tasks

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState

import java.time.LocalDate
import androidx.compose.material3.Scaffold
import com.example.agilelifemanagement.domain.model.Tag
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.FilterChip
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePicker
import androidx.compose.material3.rememberDatePickerState
import java.time.Instant
import java.time.ZoneId
import androidx.compose.runtime.LaunchedEffect

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.agilelifemanagement.domain.model.Task

@Composable
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
fun TasksScreen(
    navigateToTaskDetail: (String) -> Unit,
    viewModel: TasksViewModel = androidx.hilt.navigation.compose.hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val showDialog = viewModel.showCreateDialog
    val tags by viewModel.tags.collectAsState()
    val snackbarHostState = SnackbarHostState()
    val snackbarMessage = viewModel.snackbarMessage

    // Snackbar feedback
    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearSnackbar()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(onClick = { viewModel.onAddTaskClicked() }) {
                Icon(Icons.Filled.Add, contentDescription = "Add Task")
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            // TODO: Pull-to-refresh not available in Material3 stable
        ) {
            Column(Modifier.fillMaxSize()) {
                Text(
                    text = "Tasks",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(16.dp)
                )
                when (uiState) {
                    is TasksViewModel.UiState.Loading -> {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }
                    is TasksViewModel.UiState.Error -> {
                        val message = (uiState as TasksViewModel.UiState.Error).message
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Error: $message", color = MaterialTheme.colorScheme.error)
                                Spacer(modifier = Modifier.height(16.dp))
                                OutlinedButton(onClick = { viewModel.retry() }) {
                                    Text("Retry")
                                }
                            }
                        }
                    }
                    is TasksViewModel.UiState.Empty -> {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                // You can add an illustration here if you have one
                                Text("No tasks yet!", style = MaterialTheme.typography.titleMedium)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Create your first task to get started.", style = MaterialTheme.typography.bodyMedium)
                                Spacer(modifier = Modifier.height(16.dp))
                                OutlinedButton(onClick = { viewModel.onAddTaskClicked() }) {
                                    Text("Add Task")
                                }
                            }
                        }
                    }
                    is TasksViewModel.UiState.Success -> {
                        val tasks = (uiState as TasksViewModel.UiState.Success).tasks
                        if (tasks.isEmpty()) {
                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text("No tasks yet. Tap + to add one.")
                            }
                        } else {
                            LazyColumn(Modifier.weight(1f)) {
                                items(tasks, key = { it.id }) { task ->
                                    // TODO: Swipe-to-dismiss is not available in Material3 stable. Implement custom solution or wait for official support.
                                    Card(
                                        onClick = { navigateToTaskDetail(task.id) },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 16.dp, vertical = 8.dp)
                                    ) {
                                        Column(Modifier.padding(16.dp)) {
                                            Text(task.title, style = MaterialTheme.typography.titleMedium)
                                            if (task.summary.isNotBlank()) {
                                                Text(task.summary, style = MaterialTheme.typography.bodyMedium)
                                            }
                                            Text("Priority: ${task.priority}", style = MaterialTheme.typography.labelSmall)
                                            if (task.dueDate != null) {
                                                Text("Due: ${task.dueDate}", style = MaterialTheme.typography.labelSmall)
                                            }
                                            if (task.tags.isNotEmpty()) {
                                                Row {
                                                    task.tags.forEach { tagId ->
                                                        val tag = tags.find { it.id == tagId }
                                                        if (tag != null) {
                                                            FilterChip(
                                                                selected = true,
                                                                onClick = {},
                                                                label = { Text(tag.name) },
                                                                modifier = Modifier.padding(end = 4.dp)
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            // TODO: PullRefreshIndicator not available in Material3 stable. Remove or replace when available.
            if (showDialog) {
                CreateTaskDialog(
                    tags = tags,
                    onCreate = { title, summary, priority, dueDate, selectedTagIds ->
                        viewModel.createTask(title, summary, priority, dueDate, selectedTagIds)
                    },
                    onDismiss = { viewModel.onDialogDismiss() }
                )
            }
        }
    }
}

@Composable
fun CreateTaskDialog(
    tags: List<Tag>,
    onCreate: (String, String, Task.Priority, LocalDate?, List<String>) -> Unit,
    onDismiss: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var summary by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf(Task.Priority.MEDIUM) }
    var dueDate by remember { mutableStateOf<LocalDate?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = dueDate?.atStartOfDay(ZoneId.systemDefault())?.toInstant()?.toEpochMilli()
    )
    var selectedTags by remember { mutableStateOf(listOf<String>()) }
    var validationMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create Task") },
        text = {
            Column {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = summary,
                    onValueChange = { summary = it },
                    label = { Text("Summary") },
                    maxLines = 3
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Priority:", modifier = Modifier.padding(end = 8.dp))
                    DropdownMenuBox(priority = priority, onPriorityChange = { priority = it })
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Due Date:", modifier = Modifier.padding(end = 8.dp))
                    OutlinedButton(onClick = { showDatePicker = true }) {
                        Text(dueDate?.toString() ?: "Select Date")
                    }
                }
                if (showDatePicker) {
                    DatePickerDialog(
                        onDismissRequest = { showDatePicker = false },
                        confirmButton = {
                            Button(onClick = {
                                datePickerState.selectedDateMillis?.let { millis ->
                                    dueDate = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate()
                                }
                                showDatePicker = false
                            }) { Text("OK") }
                        },
                        dismissButton = {
                            OutlinedButton(onClick = { showDatePicker = false }) { Text("Cancel") }
                        }
                    ) {
                        DatePicker(state = datePickerState)
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text("Tags:", style = MaterialTheme.typography.labelLarge)
                Row(Modifier.wrapContentWidth()) {
                    tags.forEach { tag ->
                        FilterChip(
                            selected = selectedTags.contains(tag.id),
                            onClick = {
                                selectedTags = if (selectedTags.contains(tag.id))
                                    selectedTags - tag.id else selectedTags + tag.id
                            },
                            label = { Text(tag.name) },
                            modifier = Modifier.padding(end = 4.dp)
                        )
                    }
                }
                if (validationMessage != null) {
                    Text(validationMessage!!, color = MaterialTheme.colorScheme.error)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isBlank()) {
                        validationMessage = "Title is required"
                        return@Button
                    }
                    val currentDueDate = dueDate
if (currentDueDate != null && currentDueDate.isBefore(LocalDate.now())) {
                        validationMessage = "Due date cannot be in the past"
                        return@Button
                    }
                    validationMessage = null
                    onCreate(title, summary, priority, dueDate, selectedTags)
                },
                enabled = title.isNotBlank()
            ) { Text("Create") }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun DropdownMenuBox(priority: Task.Priority, onPriorityChange: (Task.Priority) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        Button(onClick = { expanded = true }) {
            Text(priority.name.replaceFirstChar { it.uppercase() })
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            Task.Priority.values().forEach {
                DropdownMenuItem(
                    text = { Text(it.name.replaceFirstChar { c -> c.uppercase() }) },
                    onClick = {
                        onPriorityChange(it)
                        expanded = false
                    }
                )
            }
        }
    }
}
