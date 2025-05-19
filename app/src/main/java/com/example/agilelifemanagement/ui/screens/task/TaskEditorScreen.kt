package com.example.agilelifemanagement.ui.screens.task

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.agilelifemanagement.domain.model.Task
import com.example.agilelifemanagement.domain.model.TaskPriority
import com.example.agilelifemanagement.domain.model.TaskStatus
import com.example.agilelifemanagement.ui.components.cards.TaskPriority as UiTaskPriority
import com.example.agilelifemanagement.ui.viewmodel.TaskViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * TaskEditorScreen allows users to create or edit tasks with Material 3 Expressive design principles.
 * 
 * @param navController Navigation controller for screen navigation
 * @param taskId The ID of the task to edit, or null for a new task
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskEditorScreen(
    navController: NavController,
    taskId: String?
) {
    val isNewTask = taskId == null
    val titleFocusRequester = remember { FocusRequester() }
    val viewModel = hiltViewModel<TaskViewModel>()
    
    // Load task data if editing an existing task
    LaunchedEffect(taskId) {
        if (!isNewTask && taskId != null) {
            viewModel.loadTask(taskId)
        }
    }
    
    // Collect state from ViewModel
    val uiState by viewModel.uiState.collectAsState()
    val taskToEdit = uiState.selectedTask
    
    // Check for task saved/deleted state and navigate back when done
    LaunchedEffect(uiState.isTaskSaved, uiState.isTaskDeleted) {
        if (uiState.isTaskSaved || uiState.isTaskDeleted) {
            navController.navigateUp()
        }
    }
    
    // Form state
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf(TaskPriority.MEDIUM) }
    var hasDueDate by remember { mutableStateOf(false) }
    var dueDate by remember { mutableStateOf<LocalDate?>(null) }
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    
    // Initialize form with existing task data when available
    LaunchedEffect(taskToEdit) {
        taskToEdit?.let { task ->
            title = task.title
            description = task.description
            priority = task.priority
            hasDueDate = task.dueDate != null
            dueDate = task.dueDate
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
                        text = if (isNewTask) "Create Task" else "Edit Task",
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
                    if (!isNewTask) {
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
                            if (title.isNotBlank()) {
                                val newTask = Task(
                                    id = taskToEdit?.id ?: "",
                                    title = title.trim(),
                                    description = description.trim(),
                                    status = taskToEdit?.status ?: TaskStatus.TODO,
                                    priority = priority,
                                    dueDate = if (hasDueDate) dueDate else null,
                                    createdDate = taskToEdit?.createdDate ?: LocalDate.now(),
                                    sprintId = taskToEdit?.sprintId,
                                    tags = taskToEdit?.tags ?: emptyList()
                                )
                                
                                if (isNewTask) {
                                    viewModel.createTask(newTask)
                                } else {
                                    viewModel.updateTask(newTask)
                                }
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
                title = { Text("Delete Task") },
                text = { Text("Are you sure you want to delete this task? This action cannot be undone.") },
                confirmButton = {
                    Button(
                        onClick = {
                            taskId?.let { viewModel.deleteTask(it) }
                            showDeleteConfirmation = false
                        },
                        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
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
                                if (!isNewTask && taskId != null) {
                                    viewModel.loadTask(taskId)
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
                    // Title field with Material 3 Expressive styling
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Title") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(titleFocusRequester),
                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                        singleLine = true,
                        isError = title.isBlank(),
                        supportingText = {
                            if (title.isBlank()) {
                                Text("Title is required", color = MaterialTheme.colorScheme.error)
                            }
                        }
                    )
                    
                    // Priority selector row with Material 3 Expressive styling
                    Text(
                        "Priority:",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // LOW priority button
                        FilledTonalButton(
                            onClick = { priority = TaskPriority.LOW },
                            modifier = Modifier.weight(1f),
                            border = if (priority == TaskPriority.LOW) {
                                BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
                            } else null
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(12.dp)
                                        .clip(CircleShape)
                                        .background(UiTaskPriority.LOW.color)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Low")
                            }
                        }
                        
                        // MEDIUM priority button
                        FilledTonalButton(
                            onClick = { priority = TaskPriority.MEDIUM },
                            modifier = Modifier.weight(1f),
                            border = if (priority == TaskPriority.MEDIUM) {
                                BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
                            } else null
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(12.dp)
                                        .clip(CircleShape)
                                        .background(UiTaskPriority.MEDIUM.color)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Medium")
                            }
                        }
                        
                        // HIGH priority button
                        FilledTonalButton(
                            onClick = { priority = TaskPriority.HIGH },
                            modifier = Modifier.weight(1f),
                            border = if (priority == TaskPriority.HIGH) {
                                BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
                            } else null
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(12.dp)
                                        .clip(CircleShape)
                                        .background(UiTaskPriority.HIGH.color)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("High")
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    // URGENT priority button
                    FilledTonalButton(
                        onClick = { priority = TaskPriority.URGENT },
                        modifier = Modifier.fillMaxWidth(),
                        border = if (priority == TaskPriority.URGENT) {
                            BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
                        } else null
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .clip(CircleShape)
                                    .background(UiTaskPriority.CRITICAL.color)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Urgent")
                        }
                    }
                    
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
                    
                    // Due date section with Material 3 Expressive styling
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.medium,
                        tonalElevation = 1.dp
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    "Set due date",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Switch(
                                    checked = hasDueDate,
                                    onCheckedChange = { hasDueDate = it }
                                )
                            }
                            
                            if (hasDueDate) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = dueDate?.format(DateTimeFormatter.ofPattern("MMM d, yyyy")) ?: "Select date",
                                        style = MaterialTheme.typography.bodyLarge,
                                        modifier = Modifier.weight(1f),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    IconButton(onClick = { showDatePicker = true }) {
                                        Icon(
                                            imageVector = Icons.Filled.DateRange,
                                            contentDescription = "Select date",
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                            }
                        }
                    }
                    
                    // Bottom spacer for better scrolling experience
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
    
    // Material 3 Expressive date picker dialog
    if (showDatePicker) {
        MaterialDatePickerDialog(
            onDateSelected = { selectedDate ->
                dueDate = selectedDate
            },
            onDismiss = { showDatePicker = false },
            initialDate = dueDate ?: LocalDate.now()
        )
    }
    
    // Request focus for the title field when the screen appears
    LaunchedEffect(Unit) {
        if (isNewTask) {
            // Add a small delay to ensure the composition is complete before requesting focus
            kotlinx.coroutines.delay(100)
            try {
                titleFocusRequester.requestFocus()
            } catch (e: IllegalStateException) {
                // Handle case where focus requester is not attached yet
                e.printStackTrace()
            }
        }
    }
}
