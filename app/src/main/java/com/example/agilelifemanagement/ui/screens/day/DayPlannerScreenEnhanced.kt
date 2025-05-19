package com.example.agilelifemanagement.ui.screens.day

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.agilelifemanagement.ui.components.common.LoadingIndicator
import com.example.agilelifemanagement.ui.components.timeline.DayTimeline
import com.example.agilelifemanagement.ui.components.timeline.TimeBlock
import com.example.agilelifemanagement.ui.components.timeline.TimeBlockCategory
import com.example.agilelifemanagement.ui.viewmodel.DayActivityUiModel
import com.example.agilelifemanagement.ui.viewmodel.DayPlannerEvent
import com.example.agilelifemanagement.ui.viewmodel.DayPlannerViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.*

/**
 * Enhanced Day Planner Screen with interactive timeline and advanced features.
 * This screen allows users to plan their day with a visual timeline, drag-and-drop
 * to reschedule activities, and track completion of daily tasks.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DayPlannerScreenEnhanced(
    navController: NavController,
    selectedDate: LocalDate = LocalDate.now(),
    viewModel: DayPlannerViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    
    // Show snackbar for action messages
    LaunchedEffect(uiState.actionMessage) {
        uiState.actionMessage?.let {
            if (uiState.actionSuccess) {
                snackbarHostState.showSnackbar(it)
            }
        }
    }
    
    // Initialize with the selected date
    LaunchedEffect(selectedDate) {
        viewModel.handleEvent(DayPlannerEvent.DateSelected(selectedDate))
    }
    
    // State for add activity dialog
    var showAddActivityDialog by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            DayPlannerTopBar(
                date = uiState.selectedDate,
                onDateChange = { viewModel.handleEvent(DayPlannerEvent.DateSelected(it)) },
                onBackClick = { navController.navigateUp() }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showAddActivityDialog = true },
                icon = { Icon(Icons.Filled.Add, contentDescription = "Add Activity") },
                text = { Text("Add Activity") },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Progress summary section
                DayProgressSummary(
                    completionRate = uiState.completionRate,
                    totalActivities = uiState.timeBlocks.size
                )
                
                // Main timeline content
                when {
                    uiState.isLoading -> {
                        LoadingIndicator(
                            message = "Loading your day...",
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    uiState.timeBlocks.isEmpty() -> {
                        EmptyDayView(
                            date = uiState.selectedDate,
                            onAddActivity = { showAddActivityDialog = true }
                        )
                    }
                    else -> {
                        DayTimeline(
                            timeBlocks = uiState.timeBlocks,
                            onTimeBlockClick = { timeBlock ->
                                // Navigate to activity detail
                                navController.navigate("day_activity_detail/${timeBlock.id}")
                            },
                            onTimeBlockComplete = { timeBlock ->
                                viewModel.handleEvent(DayPlannerEvent.ActivityCompletionToggled(timeBlock.id))
                            },
                            onTimeBlockReschedule = { id, newStartTime, duration ->
                                viewModel.handleEvent(
                                    DayPlannerEvent.ActivityRescheduled(
                                        activityId = id,
                                        newStartTime = newStartTime,
                                        duration = duration
                                    )
                                )
                                
                                scope.launch {
                                    snackbarHostState.showSnackbar("Activity rescheduled")
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .padding(top = 8.dp)
                        )
                    }
                }
            }
            
            // Error message
            AnimatedVisibility(
                visible = uiState.error != null,
                enter = fadeIn() + slideInVertically { it },
                exit = fadeOut() + slideOutVertically { it },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
            ) {
                uiState.error?.let { error ->
                    ErrorCard(
                        errorMessage = error,
                        onDismiss = {
                            // Clear error by refreshing
                            viewModel.handleEvent(DayPlannerEvent.RefreshRequested)
                        }
                    )
                }
            }
            
            // Add Activity Dialog
            if (showAddActivityDialog) {
                AddActivityDialog(
                    onDismiss = { showAddActivityDialog = false },
                    onActivityAdded = { activityUiModel ->
                        viewModel.handleEvent(DayPlannerEvent.ActivityAdded(activityUiModel))
                        showAddActivityDialog = false
                    },
                    selectedDate = uiState.selectedDate
                )
            }
        }
    }
}

/**
 * Top bar for the Day Planner with date selection
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DayPlannerTopBar(
    date: LocalDate,
    onDateChange: (LocalDate) -> Unit,
    onBackClick: () -> Unit
) {
    var showDatePicker by remember { mutableStateOf(false) }
    
    TopAppBar(
        title = {
            Column {
                Text(
                    text = "Day Planner",
                    style = MaterialTheme.typography.headlineSmall
                )
                Text(
                    text = formatDate(date),
                    style = MaterialTheme.typography.labelMedium
                )
            }
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back"
                )
            }
        },
        actions = {
            IconButton(onClick = { showDatePicker = true }) {
                Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = "Select Date"
                )
            }
        }
    )
    
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = { showDatePicker = false }
                ) {
                    Text("OK")
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
            DatePicker(
                state = rememberDatePickerState(
                    initialSelectedDateMillis = date.toEpochDay() * 24 * 60 * 60 * 1000
                ),
                title = { Text("Select Date") },
                headline = { Text("Choose a day to plan") },
                showModeToggle = true
            )
        }
    }
}

/**
 * Shows a summary of the day's progress
 */
@Composable
private fun DayProgressSummary(
    completionRate: Float,
    totalActivities: Int
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Today's Progress",
                style = MaterialTheme.typography.titleMedium
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            LinearProgressIndicator(
                progress = { completionRate },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${(completionRate * 100).toInt()}% Complete",
                    style = MaterialTheme.typography.bodyMedium
                )
                
                Text(
                    text = "${(totalActivities * completionRate).toInt()}/$totalActivities Activities",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

/**
 * Displayed when there are no activities for the day
 */
@Composable
private fun EmptyDayView(
    date: LocalDate,
    onAddActivity: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.DateRange,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.outline
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "No Activities Planned",
            style = MaterialTheme.typography.headlineSmall
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Your day on ${formatDate(date)} is open.\nStart planning your day by adding activities.",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Button(
            onClick = onAddActivity,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Add Your First Activity")
        }
    }
}

/**
 * Dialog for adding a new activity
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddActivityDialog(
    onDismiss: () -> Unit,
    onActivityAdded: (DayActivityUiModel) -> Unit,
    selectedDate: LocalDate
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var startHour by remember { mutableStateOf(9) }
    var startMinute by remember { mutableStateOf(0) }
    var durationHours by remember { mutableStateOf(1) }
    var durationMinutes by remember { mutableStateOf(0) }
    var categoryId by remember { mutableStateOf("work") } // Default category
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Activity") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description (Optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Start Time",
                    style = MaterialTheme.typography.titleSmall
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Hour picker
                    OutlinedTextField(
                        value = startHour.toString(),
                        onValueChange = { 
                            it.toIntOrNull()?.let { hour ->
                                if (hour in 0..23) startHour = hour
                            }
                        },
                        label = { Text("Hour") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    
                    // Minute picker
                    OutlinedTextField(
                        value = startMinute.toString(),
                        onValueChange = { 
                            it.toIntOrNull()?.let { minute ->
                                if (minute in 0..59) startMinute = minute
                            }
                        },
                        label = { Text("Minute") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Duration",
                    style = MaterialTheme.typography.titleSmall
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Hour picker
                    OutlinedTextField(
                        value = durationHours.toString(),
                        onValueChange = { 
                            it.toIntOrNull()?.let { hours ->
                                if (hours >= 0) durationHours = hours
                            }
                        },
                        label = { Text("Hours") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    
                    // Minute picker
                    OutlinedTextField(
                        value = durationMinutes.toString(),
                        onValueChange = { 
                            it.toIntOrNull()?.let { minutes ->
                                if (minutes in 0..59) durationMinutes = minutes
                            }
                        },
                        label = { Text("Minutes") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Category",
                    style = MaterialTheme.typography.titleSmall
                )
                
                // Category selector
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    CategoryChip(
                        label = "Work",
                        selected = categoryId == "work",
                        color = MaterialTheme.colorScheme.primary,
                        onClick = { categoryId = "work" }
                    )
                    
                    CategoryChip(
                        label = "Meeting",
                        selected = categoryId == "meeting",
                        color = MaterialTheme.colorScheme.tertiary,
                        onClick = { categoryId = "meeting" }
                    )
                    
                    CategoryChip(
                        label = "Personal",
                        selected = categoryId == "personal",
                        color = MaterialTheme.colorScheme.secondary,
                        onClick = { categoryId = "personal" }
                    )
                }
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    CategoryChip(
                        label = "Focus",
                        selected = categoryId == "focus",
                        color = Color(0xFF6200EA),
                        onClick = { categoryId = "focus" }
                    )
                    
                    CategoryChip(
                        label = "Break",
                        selected = categoryId == "break",
                        color = Color(0xFFFF9800),
                        onClick = { categoryId = "break" }
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    // Validate inputs
                    if (title.isNotBlank()) {
                        val startTime = LocalTime.of(startHour, startMinute)
                        val durationInMinutes = durationHours * 60 + durationMinutes
                        
                        val newActivity = DayActivityUiModel(
                            id = java.util.UUID.randomUUID().toString(),
                            title = title,
                            description = description,
                            startTime = startTime,
                            duration = durationInMinutes,
                            isCompleted = false,
                            categoryId = categoryId
                        )
                        
                        onActivityAdded(newActivity)
                    }
                }
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

/**
 * Selectable category chip
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CategoryChip(
    label: String,
    selected: Boolean,
    color: Color,
    onClick: () -> Unit
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { 
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium
            )
        },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = color,
            selectedLabelColor = Color.White
        )
    )
}

/**
 * Error message card
 */
@Composable
private fun ErrorCard(
    errorMessage: String,
    onDismiss: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.Info,
                contentDescription = "Error",
                tint = MaterialTheme.colorScheme.error
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Text(
                text = errorMessage,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onErrorContainer,
                modifier = Modifier.weight(1f)
            )
            
            TextButton(
                onClick = onDismiss
            ) {
                Text("Dismiss")
            }
        }
    }
}

/**
 * Formats a date for display
 */
private fun formatDate(date: LocalDate): String {
    val dayOfWeek = date.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault())
    val formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy")
    return "$dayOfWeek, ${date.format(formatter)}"
}
