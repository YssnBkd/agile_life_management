package com.example.agilelifemanagement.ui.screens.task

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.animateItemPlacement
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.ArrowDownward
import androidx.compose.material.icons.rounded.ArrowUpward
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.FilterList
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Sort
import androidx.compose.material.icons.rounded.TaskAlt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.agilelifemanagement.domain.model.Task
import com.example.agilelifemanagement.domain.model.TaskStatus
import com.example.agilelifemanagement.ui.viewmodel.TaskViewModel
import java.time.format.DateTimeFormatter
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListScreenWithViewModel(
    onTaskClick: (String) -> Unit,
    onAddTaskClick: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TaskViewModel = hiltViewModel()
) {
    // Collect UI state from ViewModel using StateFlow
    val uiState by viewModel.uiState.collectAsState()
    
    // Search state
    var searchQuery by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }
    
    // Effect to clear any errors after a delay
    LaunchedEffect(uiState.errorMessage) {
        if (uiState.errorMessage != null) {
            // Clear error after 5 seconds
            kotlinx.coroutines.delay(5000)
            viewModel.clearError()
        }
    }
    
    // Set up scroll behavior for collapsing top app bar
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
    
    // Track scroll state for FAB expansion/collapse
    val scrollState = rememberLazyListState()
    
    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = { 
                    Text(
                        text = "Tasks",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Rounded.ArrowBack,
                            contentDescription = "Navigate Back",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                ),
                scrollBehavior = topAppBarScrollBehavior
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onAddTaskClick,
                icon = { 
                    Icon(
                        imageVector = Icons.Filled.Add, 
                        contentDescription = "Add Task",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    ) 
                },
                text = { 
                    Text(
                        text = "Add Task",
                        style = MaterialTheme.typography.labelLarge
                    ) 
                },
                expanded = !scrollState.isScrollInProgress,
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Task Filter Bar
            TaskFiltersBar(
                filterType = uiState.filterType,
                onFilterTypeChange = { viewModel.setFilterType(it) },
                sortCriteria = uiState.sortCriteria,
                onSortCriteriaChange = { viewModel.setSortCriteria(it) },
                sortAscending = uiState.sortAscending,
                onSortDirectionChange = { viewModel.setSortDirection(it) },
                selectedFilters = uiState.selectedFilters,
                onFilterChipSelected = { filter, selected -> viewModel.updateSelectedFilters(filter, selected) },
                modifier = Modifier.fillMaxWidth()
            )
            
            // Task Count Summary
            TaskCountSummary(
                displayedCount = uiState.filteredTasks.size,
                totalCount = uiState.tasks.size,
                filterType = uiState.filterType
            )
            
            // Loading Indicator
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            
            // Error Message
            uiState.errorMessage?.let { error ->
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .background(
                            color = MaterialTheme.colorScheme.errorContainer,
                            shape = MaterialTheme.shapes.medium
                        )
                        .padding(16.dp)
                )
            }
            
            // Task List or Empty State
            if (uiState.filteredTasks.isEmpty()) {
                EmptyTasksView(filterType = uiState.filterType)
            } else {
                LazyColumn(
                    state = scrollState,
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .nestedScroll(scrollBehavior.nestedScrollConnection)
                ) {
                    items(
                        items = uiState.filteredTasks,
                        key = { task -> task.id }
                    ) { task ->
                        AnimatedVisibility(
                            visible = true,
                            enter = fadeIn(animationSpec = tween(300)) + expandVertically(animationSpec = tween(300)),
                            exit = fadeOut(animationSpec = tween(300)) + shrinkVertically(animationSpec = tween(300))
                        ) {
                            TaskItem(
                                task = task,
                                onClick = { onTaskClick(task.id) },
                                onStatusChange = { viewModel.updateTaskStatus(task.id, it) },
                                modifier = Modifier.animateItemPlacement(tween(durationMillis = 250))
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TaskItem(
    task: Task,
    onClick: () -> Unit,
    onStatusChange: (TaskStatus) -> Unit,
    modifier: Modifier = Modifier
) {
    val dateFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy")
    
    // Add subtle animation for hover-like effect
    var elevated by remember { mutableStateOf(false) }
    
    // Create task completion visual indicator
    val titleAlpha by animateFloatAsState(
        targetValue = if (task.status == TaskStatus.COMPLETED) 0.7f else 1f,
        animationSpec = tween(durationMillis = 300)
    )
    
    // Add entrance animation for the task card
    val cardElevation by animateDpAsState(
        targetValue = if (elevated) 4.dp else 2.dp,
        animationSpec = tween(durationMillis = 150)
    )
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectTapGestures(onPress = { 
                    elevated = true 
                    awaitRelease()
                    elevated = false
                }, onTap = { onClick() })
            },
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = cardElevation,
            pressedElevation = 6.dp
        )
    ) {
        Column {
            // Task header with extra visual interest
            if (task.priority == "HIGH") {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .background(
                            MaterialTheme.colorScheme.error
                        )
                )
            }
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Status Icon with ripple effect
                val statusTransition = updateTransition(
                    targetState = task.status,
                    label = "Status Transition"
                )
                
                val backgroundColor by statusTransition.animateColor(
                    label = "Background Color",
                    transitionSpec = { tween(300) }
                ) { status ->
                    when (status) {
                        TaskStatus.COMPLETED -> MaterialTheme.colorScheme.tertiaryContainer
                        TaskStatus.IN_PROGRESS -> MaterialTheme.colorScheme.secondaryContainer
                        TaskStatus.NOT_STARTED -> MaterialTheme.colorScheme.surfaceVariant
                    }
                }
                
                val iconColor by statusTransition.animateColor(
                    label = "Icon Color",
                    transitionSpec = { tween(300) }
                ) { status ->
                    when (status) {
                        TaskStatus.COMPLETED -> MaterialTheme.colorScheme.onTertiaryContainer
                        TaskStatus.IN_PROGRESS -> MaterialTheme.colorScheme.onSecondaryContainer
                        TaskStatus.NOT_STARTED -> MaterialTheme.colorScheme.onSurfaceVariant
                    }
                }
                
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(backgroundColor)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = rememberRipple(
                                bounded = false,
                                radius = 24.dp,
                                color = iconColor
                            ),
                            onClick = { 
                                // Cycle through statuses
                                val newStatus = when (task.status) {
                                    TaskStatus.NOT_STARTED -> TaskStatus.IN_PROGRESS
                                    TaskStatus.IN_PROGRESS -> TaskStatus.COMPLETED
                                    TaskStatus.COMPLETED -> TaskStatus.NOT_STARTED
                                }
                                onStatusChange(newStatus)
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = when (task.status) {
                            TaskStatus.COMPLETED -> Icons.Rounded.CheckCircle
                            TaskStatus.IN_PROGRESS -> Icons.Rounded.TaskAlt
                            TaskStatus.NOT_STARTED -> Icons.Rounded.TaskAlt
                        },
                        contentDescription = "Task Status: ${task.status}",
                        tint = iconColor,
                        modifier = Modifier.size(24.dp)
                    )
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                // Task Details with more expressive typography
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = task.title,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = titleAlpha),
                        textDecoration = if (task.status == TaskStatus.COMPLETED) TextDecoration.LineThrough else TextDecoration.None
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = task.description ?: "",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Priority & Due Date with more visual distinction
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (task.priority.isNotBlank()) {
                            val priorityColor = when (task.priority) {
                                "HIGH" -> MaterialTheme.colorScheme.error
                                "MEDIUM" -> MaterialTheme.colorScheme.tertiary
                                else -> MaterialTheme.colorScheme.outline
                            }
                            
                            Surface(
                                shape = MaterialTheme.shapes.extraSmall,
                                color = priorityColor.copy(alpha = 0.9f),
                                tonalElevation = 1.dp
                            ) {
                                Text(
                                    text = task.priority,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.surface,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                            
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                        
                        if (task.dueDate != null) {
                            // Define if the date is overdue
                            val isOverdue = task.dueDate.isBefore(LocalDate.now()) && 
                                           task.status != TaskStatus.COMPLETED
                            
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (isOverdue) {
                                    Icon(
                                        imageVector = Icons.Rounded.Flag,
                                        contentDescription = "Overdue",
                                        tint = MaterialTheme.colorScheme.error,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                }
                                
                                Text(
                                    text = "Due: ${task.dueDate.format(dateFormatter)}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (isOverdue) MaterialTheme.colorScheme.error 
                                           else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
