package com.example.agilelifemanagement.ui.screens.task

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.rounded.AccessTime
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.CalendarToday
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.PriorityHigh
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.agilelifemanagement.ui.components.cards.ExpressiveCard
import com.example.agilelifemanagement.ui.components.cards.TaskPriority
import com.example.agilelifemanagement.ui.theme.AgileLifeTheme

/**
 * TaskEditorScreen allows users to create or edit tasks
 * following Material 3 Expressive design principles.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskEditorScreen(
    taskId: String?,
    onBackClick: () -> Unit,
    onSaveClick: (String?) -> Unit,
    onDeleteClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // State variables
    val isNewTask = taskId == null
    val titleFocusRequester = remember { FocusRequester() }
    
    // Task data - would come from ViewModel in real implementation
    val existingTask = if (!isNewTask) SampleTaskDataDetail.getTask(taskId!!) else null
    
    // Form state
    var title by remember { mutableStateOf(existingTask?.title ?: "") }
    var description by remember { mutableStateOf(existingTask?.description ?: "") }
    var priority by remember { mutableStateOf(existingTask?.priority ?: TaskPriority.MEDIUM) }
    var hasDueDate by remember { mutableStateOf(existingTask?.dueDate != null) }
    var dueDate by remember { mutableStateOf(existingTask?.dueDate ?: "") }
    var estimatedHours by remember { mutableStateOf(existingTask?.estimatedMinutes?.div(60)?.toString() ?: "") }
    var estimatedMinutes by remember { mutableStateOf(existingTask?.estimatedMinutes?.rem(60)?.toString() ?: "") }
    
    // Checklist items
    val checklistItems = remember { 
        mutableStateListOf<ChecklistItemState>().apply {
            existingTask?.checklistItems?.forEach { item ->
                add(ChecklistItemState(item.id, item.text, item.isCompleted))
            }
        }
    }
    
    // Dialog states
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var showSprintSelector by remember { mutableStateOf(false) }
    
    // Sprint selection
    var selectedSprintName by remember { mutableStateOf(existingTask?.sprintName) }
    var isSprintDropdownExpanded by remember { mutableStateOf(false) }
    
    // Validation state
    val isTitleValid = title.isNotBlank()
    val isFormValid = isTitleValid
    
    // Top app bar scroll behavior
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    
    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isNewTask) "Create Task" else "Edit Task",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Rounded.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { onSaveClick(taskId) },
                        enabled = isFormValid
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Save,
                            contentDescription = "Save task"
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Title and basic details section
            ExpressiveCard(
                containerColor = MaterialTheme.colorScheme.surface,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Title field
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Task Title") },
                    placeholder = { Text("Enter task title") },
                    isError = title.isBlank(),
                    supportingText = {
                        if (title.isBlank()) {
                            Text("Title is required")
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(titleFocusRequester),
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences
                    ),
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Priority selector
                PrioritySelector(
                    selectedPriority = priority,
                    onPrioritySelected = { priority = it }
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Description field
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description (optional)") },
                    placeholder = { Text("Enter task description") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences
                    )
                )
            }
            
            // Due date and time section
            ExpressiveCard(
                containerColor = MaterialTheme.colorScheme.surface,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Time & Planning",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                // Due date toggle and selector
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.CalendarToday,
                            contentDescription = null,
                            tint = AgileLifeTheme.extendedColors.accentCoral,
                            modifier = Modifier.size(20.dp)
                        )
                        
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        Text(
                            text = "Due Date",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                    
                    androidx.compose.material3.Switch(
                        checked = hasDueDate,
                        onCheckedChange = { hasDueDate = it }
                    )
                }
                
                AnimatedVisibility(
                    visible = hasDueDate,
                    enter = expandVertically(),
                    exit = shrinkVertically()
                ) {
                    Surface(
                        onClick = { showDatePicker = true },
                        color = MaterialTheme.colorScheme.surfaceContainerLow,
                        contentColor = MaterialTheme.colorScheme.onSurface,
                        shape = MaterialTheme.shapes.medium,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text(
                                text = dueDate.ifEmpty { "Select due date and time" },
                                style = MaterialTheme.typography.bodyLarge
                            )
                            
                            Icon(
                                imageVector = Icons.Rounded.CalendarToday,
                                contentDescription = "Select date",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Estimated time
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Rounded.AccessTime,
                        contentDescription = null,
                        tint = AgileLifeTheme.extendedColors.accentLavender,
                        modifier = Modifier.size(20.dp)
                    )
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Text(
                        text = "Estimated Time",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Time input fields
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Hours field
                    OutlinedTextField(
                        value = estimatedHours,
                        onValueChange = { 
                            if (it.isEmpty() || it.toIntOrNull() != null) {
                                estimatedHours = it
                            }
                        },
                        label = { Text("Hours") },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number
                        ),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    // Minutes field
                    OutlinedTextField(
                        value = estimatedMinutes,
                        onValueChange = { 
                            if (it.isEmpty() || (it.toIntOrNull() != null && it.toInt() < 60)) {
                                estimatedMinutes = it
                            }
                        },
                        label = { Text("Minutes") },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number
                        ),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Sprint selection
                Text(
                    text = "Sprint Assignment",
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                // Sprint dropdown
                ExposedDropdownMenuBox(
                    expanded = isSprintDropdownExpanded,
                    onExpandedChange = { isSprintDropdownExpanded = it },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = selectedSprintName ?: "Not assigned to a sprint",
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = isSprintDropdownExpanded)
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    
                    ExposedDropdownMenu(
                        expanded = isSprintDropdownExpanded,
                        onDismissRequest = { isSprintDropdownExpanded = false }
                    ) {
                        // Option to remove sprint assignment
                        DropdownMenuItem(
                            text = { Text("Not assigned to a sprint") },
                            onClick = {
                                selectedSprintName = null
                                isSprintDropdownExpanded = false
                            }
                        )
                        
                        // Sample sprint options
                        listOf("Sprint 22", "Sprint 23 (Next)", "Sprint 21 (Previous)").forEach { sprint ->
                            DropdownMenuItem(
                                text = { Text(sprint) },
                                onClick = {
                                    selectedSprintName = sprint
                                    isSprintDropdownExpanded = false
                                }
                            )
                        }
                    }
                }
            }
            
            // Checklist section
            ExpressiveCard(
                containerColor = MaterialTheme.colorScheme.surface,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    Text(
                        text = "Checklist",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    
                    FilledTonalButton(
                        onClick = {
                            // Add new empty checklist item
                            checklistItems.add(
                                ChecklistItemState(
                                    id = "new-${System.currentTimeMillis()}",
                                    text = "",
                                    isCompleted = false
                                )
                            )
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add item",
                            modifier = Modifier.size(16.dp)
                        )
                        
                        Spacer(modifier = Modifier.width(4.dp))
                        
                        Text("Add Item")
                    }
                }
                
                // Checklist items
                if (checklistItems.isEmpty()) {
                    // Empty state
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceContainerLow,
                        shape = MaterialTheme.shapes.medium,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "No checklist items yet. Add some steps to track progress.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                } else {
                    // Display checklist items
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        checklistItems.forEachIndexed { index, item ->
                            ChecklistItemEditor(
                                item = item,
                                onTextChange = { text ->
                                    checklistItems[index] = item.copy(text = text)
                                },
                                onCompletionChange = { isCompleted ->
                                    checklistItems[index] = item.copy(isCompleted = isCompleted)
                                },
                                onDeleteClick = {
                                    checklistItems.removeAt(index)
                                }
                            )
                        }
                    }
                }
            }
            
            // Action buttons for existing tasks
            if (!isNewTask) {
                Button(
                    onClick = { onSaveClick(taskId) },
                    enabled = isFormValid,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Save,
                        contentDescription = null
                    )
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Text("Save Changes")
                }
                
                FilledTonalButton(
                    onClick = { onDeleteClick(taskId!!) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Delete,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error
                    )
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Text("Delete Task")
                }
            } else {
                // Create button for new tasks
                Button(
                    onClick = { onSaveClick(null) },
                    enabled = isFormValid,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null
                    )
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Text("Create Task")
                }
            }
            
            // Bottom spacing
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
    
    // Date picker dialog
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            selectableDates = object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                    // Allow selection of current or future dates
                    return utcTimeMillis >= System.currentTimeMillis() - (24 * 60 * 60 * 1000)
                }
            }
        )
        
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let {
                            // Format date
                            val date = java.time.Instant.ofEpochMilli(it)
                                .atZone(java.time.ZoneId.systemDefault())
                                .toLocalDate()
                            
                            // Check if selected date is today
                            val today = java.time.LocalDate.now()
                            val formattedDate = if (date.equals(today)) {
                                "Today"
                            } else {
                                date.format(java.time.format.DateTimeFormatter.ofPattern("MMM d, yyyy"))
                            }
                            
                            dueDate = formattedDate
                            showDatePicker = false
                            showTimePicker = true // Show time picker after date is selected
                        }
                    }
                ) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDatePicker = false }
                ) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
    
    // Time picker dialog (simplified, would be implemented with actual TimePicker in a real app)
    if (showTimePicker) {
        val timePickerState = rememberTimePickerState(
            initialHour = 17, // 5 PM default
            initialMinute = 0
        )
        
        Dialog(
            onDismissRequest = { showTimePicker = false }
        ) {
            Surface(
                shape = MaterialTheme.shapes.large,
                color = MaterialTheme.colorScheme.surface
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(24.dp)
                ) {
                    Text(
                        text = "Select Time",
                        style = MaterialTheme.typography.headlineSmall
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    TimePicker(state = timePickerState)
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row(
                        horizontalArrangement = Arrangement.End,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        TextButton(
                            onClick = { showTimePicker = false }
                        ) {
                            Text("Cancel")
                        }
                        
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        TextButton(
                            onClick = {
                                // Format time
                                val hour = timePickerState.hour
                                val minute = timePickerState.minute
                                val amPm = if (hour < 12) "AM" else "PM"
                                val hour12 = if (hour == 0) 12 else if (hour > 12) hour - 12 else hour
                                val minuteStr = if (minute < 10) "0$minute" else "$minute"
                                
                                // Update due date with time
                                dueDate = "$dueDate, $hour12:$minuteStr $amPm"
                                showTimePicker = false
                            }
                        ) {
                            Text("OK")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PrioritySelector(
    selectedPriority: TaskPriority,
    onPrioritySelected: (TaskPriority) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Rounded.PriorityHigh,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Text(
                text = "Priority",
                style = MaterialTheme.typography.titleSmall
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            TaskPriority.values().forEach { priority ->
                PriorityOption(
                    priority = priority,
                    selected = selectedPriority == priority,
                    onClick = { onPrioritySelected(priority) }
                )
            }
        }
    }
}

@Composable
private fun PriorityOption(
    priority: TaskPriority,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        color = if (selected) priority.color.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceContainerLow,
        contentColor = priority.color,
        shape = MaterialTheme.shapes.medium,
        border = if (selected) androidx.compose.foundation.BorderStroke(1.dp, priority.color) else null,
        modifier = Modifier.weight(1f)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 8.dp)
        ) {
            // Priority indicator
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .clip(CircleShape)
                    .background(priority.color)
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = priority.name.lowercase().replaceFirstChar { it.uppercase() },
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}

@Composable
private fun ChecklistItemEditor(
    item: ChecklistItemState,
    onTextChange: (String) -> Unit,
    onCompletionChange: (Boolean) -> Unit,
    onDeleteClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        // Checkbox
        Surface(
            onClick = { onCompletionChange(!item.isCompleted) },
            color = Color.Transparent,
            contentColor = if (item.isCompleted) 
                AgileLifeTheme.extendedColors.accentMint 
            else 
                MaterialTheme.colorScheme.onSurfaceVariant
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .padding(4.dp)
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(
                        if (item.isCompleted) 
                            AgileLifeTheme.extendedColors.accentMint 
                        else 
                            MaterialTheme.colorScheme.surfaceContainerHigh
                    )
            ) {
                if (item.isCompleted) {
                    Icon(
                        imageVector = Icons.Rounded.CheckCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.surface,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.width(8.dp))
        
        // Text field
        OutlinedTextField(
            value = item.text,
            onValueChange = onTextChange,
            placeholder = { Text("Enter checklist item") },
            modifier = Modifier.weight(1f),
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Sentences
            ),
            singleLine = true
        )
        
        // Delete button
        IconButton(
            onClick = onDeleteClick,
            modifier = Modifier.padding(start = 4.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Remove item",
                tint = MaterialTheme.colorScheme.error
            )
        }
    }
}

/**
 * State for checklist items in editor
 */
data class ChecklistItemState(
    val id: String,
    val text: String,
    val isCompleted: Boolean
)
