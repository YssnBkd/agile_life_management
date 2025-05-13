@file:OptIn(ExperimentalMaterial3Api::class)
package com.example.agilelifemanagement.ui.screens.sprints

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.rememberDatePickerState
import java.time.LocalDate
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.agilelifemanagement.domain.model.Sprint

@Composable
fun SprintsScreen(
    navigateToSprintDetail: (String) -> Unit,
    viewModel: SprintsViewModel = androidx.hilt.navigation.compose.hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val showDialog = viewModel.showCreateDialog
    val snackbarMessage = viewModel.snackbarMessage
    val snackbarHostState = remember { SnackbarHostState() }

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
            FloatingActionButton(onClick = { viewModel.onAddSprintClicked() }) {
                Icon(Icons.Filled.Add, contentDescription = "Add Sprint")
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
        ) {
            Column(Modifier.fillMaxSize()) {
                Text(
                    text = "Sprints",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(16.dp)
                )
                when (uiState) {
                    is SprintsViewModel.UiState.Loading -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }
                    is SprintsViewModel.UiState.Error -> {
                        val message = (uiState as SprintsViewModel.UiState.Error).message
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = message, color = MaterialTheme.colorScheme.error)
                                Spacer(modifier = Modifier.height(8.dp))
                                Button(onClick = { viewModel.retry() }) {
                                    Text("Retry")
                                }
                            }
                        }
                    }
                    is SprintsViewModel.UiState.Empty -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(
                                text = "No sprints found."
                            )
                        }
                    }
                    is SprintsViewModel.UiState.Success -> {
                        val sprints = (uiState as SprintsViewModel.UiState.Success).sprints
                        LazyColumn(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(sprints) { sprint ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 8.dp)
                                        .clickable { navigateToSprintDetail(sprint.id) },
                                    shape = RoundedCornerShape(12.dp),
                                    elevation = CardDefaults.cardElevation(2.dp)
                                ) {
                                    Column(Modifier.padding(16.dp)) {
                                        Text(sprint.name, style = MaterialTheme.typography.titleMedium)
                                        if (sprint.summary.isNotBlank()) {
                                            Text(sprint.summary, style = MaterialTheme.typography.bodyMedium)
                                        }
                                        if (sprint.isCompleted) {
                                            Text(
                                                text = "Completed",
                                                color = MaterialTheme.colorScheme.secondary,
                                                style = MaterialTheme.typography.labelSmall
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (showDialog) {
                CreateSprintDialog(
                    onCreate = { name, startDate, endDate, description ->
                        viewModel.createSprint(
                            name = name,
                            startDate = startDate,
                            endDate = endDate
                        )
                        // If you want to store description, add a custom handler here
                    },
                    onDismiss = { viewModel.hideCreateDialog() }
                )
            }
        }
    }
}

@Composable
fun CreateSprintDialog(
    onCreate: (String, java.time.LocalDate, java.time.LocalDate, List<String>) -> Unit, // name, startDate, endDate, description
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var summary by remember { mutableStateOf("") }
    var descriptionText by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf<java.time.LocalDate?>(null) }
    var endDate by remember { mutableStateOf<java.time.LocalDate?>(null) }
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create Sprint") },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name*") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = summary,
                    onValueChange = { summary = it },
                    label = { Text("Summary") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = descriptionText,
                    onValueChange = { descriptionText = it },
                    label = { Text("Description (bullet points, one per line)") },
                    modifier = Modifier.fillMaxWidth().height(80.dp),
                    maxLines = 4
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedButton(onClick = { showStartDatePicker = true }) {
                        Text(startDate?.toString() ?: "Start Date*")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedButton(onClick = { showEndDatePicker = true }) {
                        Text(endDate?.toString() ?: "End Date*")
                    }
                }
                if (error != null) {
                    Text(error!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                // Validation
                if (name.isBlank()) {
                    error = "Name is required"
                    return@Button
                }
                if (startDate == null) {
                    error = "Start date is required"
                    return@Button
                }
                if (endDate == null) {
                    error = "End date is required"
                    return@Button
                }
                if (endDate!!.isBefore(startDate)) {
                    error = "End date must be after start date"
                    return@Button
                }
                error = null
                val description = descriptionText.lines().filter { it.isNotBlank() }
                onCreate(name, startDate!!, endDate!!, description)
            }) {
                Text("Create")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )

    if (showStartDatePicker) {
        val dateState = rememberDatePickerState(
            initialSelectedDateMillis = startDate?.atStartOfDay(java.time.ZoneId.systemDefault())?.toInstant()?.toEpochMilli()
        )
        DatePickerDialog(
            onDismissRequest = { showStartDatePicker = false },
            confirmButton = {
                Button(onClick = {
                    dateState.selectedDateMillis?.let { millis ->
                        startDate = java.time.Instant.ofEpochMilli(millis).atZone(java.time.ZoneId.systemDefault()).toLocalDate()
                    }
                    showStartDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                OutlinedButton(onClick = { showStartDatePicker = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(
                state = dateState,
                showModeToggle = false
            )
        }
    }
    if (showEndDatePicker) {
        val dateState = rememberDatePickerState(
            initialSelectedDateMillis = endDate?.atStartOfDay(java.time.ZoneId.systemDefault())?.toInstant()?.toEpochMilli()
        )
        DatePickerDialog(
            onDismissRequest = { showEndDatePicker = false },
            confirmButton = {
                Button(onClick = {
                    dateState.selectedDateMillis?.let { millis ->
                        endDate = java.time.Instant.ofEpochMilli(millis).atZone(java.time.ZoneId.systemDefault()).toLocalDate()
                    }
                    showEndDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                OutlinedButton(onClick = { showEndDatePicker = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(
                state = dateState,
                showModeToggle = false
            )
        }
    }
}
