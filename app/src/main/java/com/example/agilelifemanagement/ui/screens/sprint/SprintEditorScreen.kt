package com.example.agilelifemanagement.ui.screens.sprint

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.DateRange
import androidx.compose.material.icons.rounded.DirectionsRun
import androidx.compose.material.icons.rounded.Flag
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.agilelifemanagement.ui.components.cards.ExpressiveCard
import com.example.agilelifemanagement.ui.components.cards.FeatureCard
import com.example.agilelifemanagement.ui.theme.AgileLifeTheme
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * SprintEditorScreen allows creating new sprints or editing existing ones
 * 
 * Features following Material 3 Expressive principles:
 * - Generous spacing and expressive visual elements
 * - Tactile feedback for all interactive elements
 * - Clear visual hierarchy with section grouping
 * - Visually distinct form fields with immediate validation
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SprintEditorScreen(
    sprintId: String?,
    onBackClick: () -> Unit,
    onSaveClick: (SprintFormData) -> Unit,
    modifier: Modifier = Modifier
) {
    // State for form data - in a real app, would be loaded from ViewModel for existing sprints
    val (formData, goals) = if (sprintId != null) {
        // Edit mode - load existing sprint data
        val existingSprint = SampleSprintEditorData.getSprint(sprintId)
        
        Pair(
            remember {
                mutableStateOf(
                    SprintFormData(
                        id = existingSprint.id,
                        name = existingSprint.name,
                        description = existingSprint.description,
                        startDate = existingSprint.startDate,
                        endDate = existingSprint.endDate
                    )
                )
            },
            remember { mutableStateListOf<String>().apply { addAll(existingSprint.goals) } }
        )
    } else {
        // Create mode - initialize with defaults
        Pair(
            remember {
                mutableStateOf(
                    SprintFormData(
                        id = "",
                        name = "",
                        description = "",
                        startDate = LocalDate.now(),
                        endDate = LocalDate.now().plusWeeks(2)
                    )
                )
            },
            remember { mutableStateListOf<String>() }
        )
    }
    
    // Goal input state
    var newGoal by remember { mutableStateOf("") }
    var showNewGoalInput by remember { mutableStateOf(false) }
    
    // Date picker states
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }
    
    // Form validation
    val isValid = formData.value.name.isNotBlank() && 
                 formData.value.startDate <= formData.value.endDate &&
                 formData.value.description.isNotBlank()
    
    // Top app bar scroll behavior
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val focusManager = LocalFocusManager.current
    
    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (sprintId != null) "Edit Sprint" else "Create New Sprint",
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
            // Sprint header with icon
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.DirectionsRun,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp)
                )
                
                Text(
                    text = if (sprintId != null) "Update Sprint Details" else "Create New Sprint",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
            
            // Form fields
            ExpressiveCard(
                modifier = Modifier.fillMaxWidth(),
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                // Sprint name field
                Text(
                    text = "Sprint Information",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                
                OutlinedTextField(
                    value = formData.value.name,
                    onValueChange = { 
                        formData.value = formData.value.copy(name = it)
                    },
                    label = { Text("Sprint Name") },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = MaterialTheme.typography.bodyLarge,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Words,
                        imeAction = ImeAction.Next
                    ),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Rounded.DirectionsRun,
                            contentDescription = null
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        focusedLabelColor = MaterialTheme.colorScheme.primary
                    ),
                    shape = MaterialTheme.shapes.medium
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Description field
                OutlinedTextField(
                    value = formData.value.description,
                    onValueChange = { 
                        formData.value = formData.value.copy(description = it)
                    },
                    label = { Text("Sprint Description") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    textStyle = MaterialTheme.typography.bodyLarge,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        focusedLabelColor = MaterialTheme.colorScheme.primary
                    ),
                    shape = MaterialTheme.shapes.medium
                )
            }
            
            // Date range selection
            ExpressiveCard(
                modifier = Modifier.fillMaxWidth(),
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                Text(
                    text = "Sprint Timeline",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                
                // Start date selector
                DateSelector(
                    label = "Start Date",
                    date = formData.value.startDate,
                    onClick = { showStartDatePicker = true }
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // End date selector
                DateSelector(
                    label = "End Date",
                    date = formData.value.endDate,
                    onClick = { showEndDatePicker = true }
                )
                
                // Date validation message
                if (formData.value.startDate > formData.value.endDate) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "End date must be after start date",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                }
                
                // Duration info
                val duration = java.time.temporal.ChronoUnit.DAYS.between(
                    formData.value.startDate,
                    formData.value.endDate
                ) + 1
                
                if (formData.value.startDate <= formData.value.endDate) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Sprint Duration: $duration days",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            // Sprint goals
            ExpressiveCard(
                modifier = Modifier.fillMaxWidth(),
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                // Header with add button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Sprint Goals",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    
                    TextButton(
                        onClick = { 
                            showNewGoalInput = !showNewGoalInput
                            newGoal = ""
                        }
                    ) {
                        Icon(
                            imageVector = if (showNewGoalInput) Icons.Rounded.Close else Icons.Rounded.Flag,
                            contentDescription = if (showNewGoalInput) "Cancel" else "Add Goal",
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (showNewGoalInput) "Cancel" else "Add Goal"
                        )
                    }
                }
                
                // New goal input
                AnimatedVisibility(
                    visible = showNewGoalInput,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = newGoal,
                            onValueChange = { newGoal = it },
                            label = { Text("New Goal") },
                            modifier = Modifier.fillMaxWidth(),
                            textStyle = MaterialTheme.typography.bodyLarge,
                            keyboardOptions = KeyboardOptions(
                                capitalization = KeyboardCapitalization.Sentences,
                                imeAction = ImeAction.Done
                            ),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                                focusedLabelColor = MaterialTheme.colorScheme.primary
                            ),
                            shape = MaterialTheme.shapes.medium
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Button(
                            onClick = {
                                if (newGoal.isNotBlank()) {
                                    goals.add(newGoal)
                                    newGoal = ""
                                    showNewGoalInput = false
                                    focusManager.clearFocus()
                                }
                            },
                            enabled = newGoal.isNotBlank(),
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Text("Add Goal")
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Goal list
                if (goals.isEmpty() && !showNewGoalInput) {
                    Text(
                        text = "No goals added yet. Goals help track sprint progress and objectives.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        goals.forEachIndexed { index, goal ->
                            GoalItem(
                                goal = goal,
                                onRemove = { goals.removeAt(index) }
                            )
                        }
                    }
                }
            }
            
            // Save button
            Button(
                onClick = {
                    val finalFormData = formData.value.copy(
                        goals = goals.toList()
                    )
                    onSaveClick(finalFormData)
                },
                enabled = isValid,
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium
            ) {
                Icon(
                    imageVector = Icons.Rounded.CheckCircle,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (sprintId != null) "Update Sprint" else "Create Sprint",
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
            
            // Show validation message if needed
            if (!isValid) {
                Text(
                    text = "Please fill in all required fields correctly",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
            
            // Bottom spacing
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
    
    // Start date picker dialog
    if (showStartDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = formData.value.startDate
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli()
        )
        
        DatePickerDialog(
            onDismissRequest = { showStartDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val localDate = Instant.ofEpochMilli(millis)
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()
                            formData.value = formData.value.copy(startDate = localDate)
                        }
                        showStartDatePicker = false
                    }
                ) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                TextButton(onClick = { showStartDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
    
    // End date picker dialog
    if (showEndDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = formData.value.endDate
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli()
        )
        
        DatePickerDialog(
            onDismissRequest = { showEndDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val localDate = Instant.ofEpochMilli(millis)
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()
                            formData.value = formData.value.copy(endDate = localDate)
                        }
                        showEndDatePicker = false
                    }
                ) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEndDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@Composable
private fun DateSelector(
    label: String,
    date: LocalDate,
    onClick: () -> Unit
) {
    val formattedDate = date.format(DateTimeFormatter.ofPattern("MMM d, yyyy"))
    
    Surface(
        onClick = onClick,
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        contentColor = MaterialTheme.colorScheme.onSurface,
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Rounded.CalendarMonth,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = formattedDate,
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}

@Composable
private fun GoalItem(
    goal: String,
    onRemove: () -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Rounded.Flag,
                contentDescription = null,
                tint = AgileLifeTheme.extendedColors.accentSunflower,
                modifier = Modifier.size(20.dp)
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Text(
                text = goal,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )
            
            IconButton(
                onClick = onRemove,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Close,
                    contentDescription = "Remove Goal",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

/**
 * Form data for creating or editing a sprint
 */
data class SprintFormData(
    val id: String,
    val name: String,
    val description: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val goals: List<String> = emptyList()
)

/**
 * Sample data for the sprint editor
 */
object SampleSprintEditorData {
    fun getSprint(sprintId: String): SprintEditData {
        return when (sprintId) {
            "sprint-1" -> SprintEditData(
                id = "sprint-1",
                name = "Sprint 23: Dashboard Implementation",
                description = "In this sprint, we are focusing on implementing the main dashboard interface and its core components. This includes creating the timeline view, sprint summary cards, and quick-action FAB. We'll also set up the navigation architecture and shared UI components that will be used throughout the app.",
                startDate = LocalDate.now().minusDays(1),
                endDate = LocalDate.now().plusDays(13),
                goals = listOf(
                    "Complete the Material 3 Expressive design system implementation",
                    "Implement dashboard with timeline and quick actions",
                    "Create reusable card components for sprints and tasks",
                    "Establish navigation architecture for the entire app",
                    "Setup automated tests for key components"
                )
            )
            else -> SprintEditData(
                id = sprintId,
                name = "New Sprint",
                description = "Sprint description...",
                startDate = LocalDate.now(),
                endDate = LocalDate.now().plusWeeks(2),
                goals = emptyList()
            )
        }
    }
}

data class SprintEditData(
    val id: String,
    val name: String,
    val description: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val goals: List<String>
)
