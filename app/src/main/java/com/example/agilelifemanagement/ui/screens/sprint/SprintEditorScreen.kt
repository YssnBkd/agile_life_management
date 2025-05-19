package com.example.agilelifemanagement.ui.screens.sprint

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import com.example.agilelifemanagement.ui.components.datepicker.MaterialDatePickerDialog
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.agilelifemanagement.domain.model.Sprint
import com.example.agilelifemanagement.domain.model.SprintStatus
import com.example.agilelifemanagement.ui.model.SprintFormData
import com.example.agilelifemanagement.ui.viewmodel.SprintViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * SprintEditorScreen allows users to create or edit sprints with Material 3 Expressive design principles.
 * 
 * @param navController Navigation controller for screen navigation
 * @param sprintId The ID of the sprint to edit, or null for a new sprint
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SprintEditorScreen(
    navController: NavController,
    sprintId: String?
) {
    val isNewSprint = sprintId == null
    val nameFocusRequester = remember { FocusRequester() }
    val viewModel = hiltViewModel<SprintViewModel>()
    
    // Load sprint data if editing an existing sprint
    LaunchedEffect(sprintId) {
        if (!isNewSprint && sprintId != null) {
            viewModel.loadSprint(sprintId)
        }
    }
    
    // Collect state from ViewModel
    val uiState by viewModel.uiState.collectAsState()
    val sprintToEdit = uiState.selectedSprint
    
    // Handle navigation after operation completes
    LaunchedEffect(uiState.operationSuccess) {
        if (uiState.operationSuccess) {
            // Navigate back to the sprint detail screen or list
            if (isNewSprint && uiState.selectedSprint != null) {
                // Navigate to the newly created sprint detail
                navController.navigate("sprints/${uiState.selectedSprint!!.id}")
            } else if (!isNewSprint) {
                // Go back to detail screen
                navController.popBackStack()
            }
            // Reset the operation success state
            viewModel.resetOperationState()
        }
    }
    
    // Check for sprint saved/deleted state and navigate back when done
    LaunchedEffect(uiState.isSprintSaved, uiState.isSprintDeleted) {
        if (uiState.isSprintSaved || uiState.isSprintDeleted) {
            navController.navigateUp()
        }
    }
    
    // Form state
    var name by remember { mutableStateOf("") }
    var goal by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var status by remember { mutableStateOf(SprintStatus.PLANNED) }
    var startDate by remember { mutableStateOf(LocalDate.now()) }
    var endDate by remember { mutableStateOf(LocalDate.now().plusDays(14)) }
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }
    var isStatusExpanded by remember { mutableStateOf(false) }
    
    // Initialize form with existing sprint data when available
    LaunchedEffect(sprintToEdit) {
        sprintToEdit?.let { sprint ->
            name = sprint.name
            goal = sprint.goal
            description = sprint.description
            status = sprint.status
            startDate = sprint.startDate
            endDate = sprint.endDate
        }
    }
    
    // Top app bar scroll behavior with Material 3 Expressive motion
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = if (isNewSprint) "Create Sprint" else "Edit Sprint",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                actions = {
                    if (!isNewSprint) {
                        IconButton(onClick = { showDeleteConfirmation = true }) {
                            Icon(
                                imageVector = Icons.Filled.Delete,
                                contentDescription = "Delete",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                    IconButton(
                        onClick = { 
                            // Form validation
                            if (name.isNotBlank() && goal.isNotBlank()) {
                                val formData = SprintFormData(
                                    name = name.trim(),
                                    goal = goal.trim(),
                                    description = description.trim(),
                                    status = status,
                                    startDate = startDate,
                                    endDate = endDate
                                )
                                
                                saveSprint(isNewSprint, sprintId, formData, viewModel)
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Save,
                            contentDescription = "Save",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    scrolledContainerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            )
        }
    ) { padding ->
        // Delete confirmation dialog
        if (showDeleteConfirmation) {
            AlertDialog(
                onDismissRequest = { showDeleteConfirmation = false },
                title = { Text("Delete Sprint") },
                text = { Text("Are you sure you want to delete this sprint? This action cannot be undone.") },
                confirmButton = {
                    Button(
                        onClick = {
                            sprintId?.let { viewModel.deleteSprint(it) }
                            showDeleteConfirmation = false
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("Delete")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteConfirmation = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
        
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
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "Error: ${uiState.errorMessage}",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        TextButton(
                            onClick = {
                                if (!isNewSprint && sprintId != null) {
                                    viewModel.loadSprint(sprintId)
                                } else {
                                    viewModel.clearError()
                                }
                            }
                        ) {
                            Text("Retry")
                        }
                    }
                }
            }
            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Name field with Material 3 Expressive styling
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Sprint Name") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(nameFocusRequester),
                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                        singleLine = true,
                        isError = name.isBlank(),
                        supportingText = {
                            if (name.isBlank()) {
                                Text("Name is required", color = MaterialTheme.colorScheme.error)
                            }
                        }
                    )
                    
                    // Goal field with Material 3 Expressive styling
                    OutlinedTextField(
                        value = goal,
                        onValueChange = { goal = it },
                        label = { Text("Sprint Goal") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                        singleLine = true,
                        isError = goal.isBlank(),
                        supportingText = {
                            if (goal.isBlank()) {
                                Text("Goal is required", color = MaterialTheme.colorScheme.error)
                            }
                        }
                    )
                    
                    // Description field with Material 3 Expressive styling
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Description") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                        maxLines = 5
                    )
                    
                    // Status dropdown with Material 3 Expressive styling
                    if (!isNewSprint) {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = MaterialTheme.shapes.medium,
                            tonalElevation = 1.dp
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    "Sprint Status",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                
                                ExposedDropdownMenuBox(
                                    expanded = isStatusExpanded,
                                    onExpandedChange = { isStatusExpanded = it }
                                ) {
                                    OutlinedTextField(
                                        value = status.name.replace('_', ' '),
                                        onValueChange = { },
                                        readOnly = true,
                                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isStatusExpanded) },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .menuAnchor(),
                                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                                    )
                                    
                                    ExposedDropdownMenu(
                                        expanded = isStatusExpanded,
                                        onDismissRequest = { isStatusExpanded = false }
                                    ) {
                                        SprintStatus.values().forEach { sprintStatus ->
                                            DropdownMenuItem(
                                                text = { Text(sprintStatus.name.replace('_', ' ')) },
                                                onClick = {
                                                    status = sprintStatus
                                                    isStatusExpanded = false
                                                },
                                                contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                    
                    // Date fields with Material 3 Expressive styling
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.medium,
                        tonalElevation = 1.dp
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                "Sprint Duration",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            // Start date
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "Start Date:",
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier.width(100.dp)
                                )
                                Text(
                                    startDate.format(DateTimeFormatter.ofPattern("MMM d, yyyy")),
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier.weight(1f)
                                )
                                IconButton(onClick = { showStartDatePicker = true }) {
                                    Icon(
                                        imageVector = Icons.Filled.DateRange,
                                        contentDescription = "Select start date",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            // End date
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "End Date:",
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier.width(100.dp)
                                )
                                Text(
                                    endDate.format(DateTimeFormatter.ofPattern("MMM d, yyyy")),
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier.weight(1f)
                                )
                                IconButton(onClick = { showEndDatePicker = true }) {
                                    Icon(
                                        imageVector = Icons.Filled.DateRange,
                                        contentDescription = "Select end date",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }
                    
                    // Sprint duration indicator
                    val durationDays = java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate)
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.medium,
                        color = MaterialTheme.colorScheme.secondaryContainer
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Duration: $durationDays days",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }
                    
                    // Bottom spacer for better scrolling experience
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
    
    // Material 3 Expressive date picker dialogs
    if (showStartDatePicker) {
        MaterialDatePickerDialog(
            onDateSelected = { selectedDate ->
                startDate = selectedDate
                // Ensure end date is not before start date
                if (endDate.isBefore(startDate)) {
                    endDate = startDate.plusDays(14)
                }
            },
            onDismiss = { showStartDatePicker = false },
            initialDate = startDate
        )
    }
    
    if (showEndDatePicker) {
        MaterialDatePickerDialog(
            onDateSelected = { selectedDate ->
                // Ensure end date is not before start date
                endDate = if (selectedDate.isBefore(startDate)) {
                    startDate.plusDays(1)
                } else {
                    selectedDate
                }
            },
            onDismiss = { showEndDatePicker = false },
            initialDate = endDate
        )
    }
    
    // Request focus for the name field when the screen appears
    LaunchedEffect(Unit) {
        if (isNewSprint) {
            // Add a small delay to ensure the composition is complete before requesting focus
            kotlinx.coroutines.delay(100)
            try {
                nameFocusRequester.requestFocus()
            } catch (e: IllegalStateException) {
                // Handle case where focus requester is not attached yet
                e.printStackTrace()
            }
        }
    }
}

/**
 * Handles saving a sprint based on form data
 */
private fun saveSprint(
    isNewSprint: Boolean,
    sprintId: String?,
    formData: SprintFormData,
    viewModel: SprintViewModel
) {
    if (isNewSprint) {
        // Create a new sprint with default values for fields not in the form
        val newSprint = Sprint(
            id = "",  // Will be generated by the backend
            name = formData.name,
            goal = formData.goal,
            description = formData.description,
            status = formData.status,
            startDate = formData.startDate,
            endDate = formData.endDate,
            progress = 0,
            taskCount = 0,
            completedTaskCount = 0,
            createdDate = LocalDate.now()
        )
        viewModel.createSprint(newSprint)
    } else {
        // Update an existing sprint, preserving fields not in the form
        val sprintToUpdate = viewModel.uiState.value.selectedSprint
        if (sprintToUpdate != null && sprintId != null) {
            val updatedSprint = sprintToUpdate.copy(
                name = formData.name,
                goal = formData.goal,
                description = formData.description,
                status = formData.status,
                startDate = formData.startDate,
                endDate = formData.endDate
            )
            viewModel.updateSprint(updatedSprint)
        }
    }
}
