package com.example.agilelifemanagement.ui.screens.task

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.rounded.ArrowDownward
import androidx.compose.material.icons.rounded.ArrowUpward
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.FilterList
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Sort
import androidx.compose.material.icons.rounded.TaskAlt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.agilelifemanagement.ui.components.cards.ExpressiveCard
import com.example.agilelifemanagement.ui.components.cards.TaskCard
import com.example.agilelifemanagement.ui.components.cards.TaskPriority
import com.example.agilelifemanagement.ui.screens.dashboard.TaskInfo
import com.example.agilelifemanagement.ui.theme.AgileLifeTheme

/**
 * TaskListScreen displays all tasks with filtering, sorting and search functionality
 * following Material 3 Expressive design principles.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListScreen(
    onTaskClick: (String) -> Unit,
    onAddTaskClick: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Sample data - would come from ViewModel in real implementation
    val allTasks = remember { SampleTaskData.getAllTasks() }
    
    // State for filtering and sorting
    var filterType by remember { mutableStateOf(TaskFilterType.ALL) }
    var sortCriteria by remember { mutableStateOf(TaskSortCriteria.DUE_DATE) }
    var sortAscending by remember { mutableStateOf(true) }
    
    // Search state
    var searchQuery by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }
    
    // Filter chips state
    val selectedFilters = remember { mutableStateListOf<TaskFilterChip>() }
    
    // Apply filters and sorting to get displayed tasks
    val displayedTasks = remember(allTasks, filterType, sortCriteria, sortAscending, searchQuery, selectedFilters) {
        // Filter by completion status
        var filteredTasks = when (filterType) {
            TaskFilterType.ALL -> allTasks
            TaskFilterType.ACTIVE -> allTasks.filter { !it.isCompleted }
            TaskFilterType.COMPLETED -> allTasks.filter { it.isCompleted }
        }
        
        // Apply search query if any
        if (searchQuery.isNotEmpty()) {
            filteredTasks = filteredTasks.filter { 
                it.title.contains(searchQuery, ignoreCase = true)
            }
        }
        
        // Apply priority filter if selected
        if (selectedFilters.contains(TaskFilterChip.HIGH_PRIORITY)) {
            filteredTasks = filteredTasks.filter { it.priority == TaskPriority.HIGH }
        } else if (selectedFilters.contains(TaskFilterChip.MEDIUM_PRIORITY)) {
            filteredTasks = filteredTasks.filter { it.priority == TaskPriority.MEDIUM }
        } else if (selectedFilters.contains(TaskFilterChip.LOW_PRIORITY)) {
            filteredTasks = filteredTasks.filter { it.priority == TaskPriority.LOW }
        }
        
        // Apply due today filter if selected
        if (selectedFilters.contains(TaskFilterChip.DUE_TODAY)) {
            filteredTasks = filteredTasks.filter { 
                it.dueDate?.contains("Today") == true 
            }
        }
        
        // Sort tasks
        filteredTasks.sortedWith(
            when (sortCriteria) {
                TaskSortCriteria.DUE_DATE -> compareBy { 
                    it.dueDate ?: "ZZZ" // Tasks without due date go to the end
                }
                TaskSortCriteria.PRIORITY -> compareBy { 
                    it.priority.ordinal 
                }
                TaskSortCriteria.ESTIMATED_TIME -> compareBy { 
                    it.estimatedMinutes ?: Int.MAX_VALUE
                }
            }
        ).let {
            if (!sortAscending) it.reversed() else it
        }
    }
    
    // Top app bar scroll behavior
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    
    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Tasks",
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
                    IconButton(onClick = { isSearchActive = true }) {
                        Icon(
                            imageVector = Icons.Rounded.Search,
                            contentDescription = "Search tasks"
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onAddTaskClick,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                expanded = true
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add task"
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Add Task")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Search bar
            AnimatedVisibility(
                visible = isSearchActive,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                SearchBar(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    onSearch = { /* Handle search submission */ },
                    active = isSearchActive,
                    onActiveChange = { isSearchActive = it },
                    placeholder = { Text("Search tasks...") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Rounded.Search,
                            contentDescription = "Search icon"
                        )
                    },
                    trailingIcon = {
                        if (isSearchActive) {
                            IconButton(onClick = { 
                                if (searchQuery.isNotEmpty()) {
                                    searchQuery = ""
                                } else {
                                    isSearchActive = false
                                }
                            }) {
                                Icon(
                                    imageVector = Icons.Rounded.ArrowBack,
                                    contentDescription = "Exit search"
                                )
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    // Search suggestions would go here
                }
            }
            
            // Filter and Sort controls
            TaskFiltersBar(
                filterType = filterType,
                onFilterTypeChange = { filterType = it },
                sortCriteria = sortCriteria,
                onSortCriteriaChange = { sortCriteria = it },
                sortAscending = sortAscending,
                onSortDirectionChange = { sortAscending = it },
                selectedFilters = selectedFilters,
                onFilterChipSelected = { chip, selected ->
                    if (selected) {
                        // Ensure only one priority filter is active
                        if (chip.isPriorityFilter) {
                            selectedFilters.removeAll { it.isPriorityFilter }
                        }
                        selectedFilters.add(chip)
                    } else {
                        selectedFilters.remove(chip)
                    }
                }
            )
            
            // Task list
            if (displayedTasks.isEmpty()) {
                EmptyTasksView(filterType)
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    item {
                        TaskCountSummary(
                            displayedCount = displayedTasks.size,
                            totalCount = allTasks.size,
                            filterType = filterType
                        )
                    }
                    
                    items(displayedTasks) { task ->
                        TaskCard(
                            title = task.title,
                            priority = task.priority,
                            isCompleted = task.isCompleted,
                            dueTime = task.dueDate,
                            estimatedMinutes = task.estimatedMinutes,
                            onClick = { onTaskClick(task.id) }
                        )
                    }
                    
                    // Bottom spacing for FAB
                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TaskFiltersBar(
    filterType: TaskFilterType,
    onFilterTypeChange: (TaskFilterType) -> Unit,
    sortCriteria: TaskSortCriteria,
    onSortCriteriaChange: (TaskSortCriteria) -> Unit,
    sortAscending: Boolean,
    onSortDirectionChange: (Boolean) -> Unit,
    selectedFilters: List<TaskFilterChip>,
    onFilterChipSelected: (TaskFilterChip, Boolean) -> Unit
) {
    ExpressiveCard(
        containerColor = MaterialTheme.colorScheme.surface,
        elevationShadowEnabled = false,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // Status filter (Segmented control)
        SingleChoiceSegmentedButtonRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
        ) {
            TaskFilterType.values().forEachIndexed { index, type ->
                SegmentedButton(
                    selected = filterType == type,
                    onClick = { onFilterTypeChange(type) },
                    shape = SegmentedButtonDefaults.itemShape(
                        index = index,
                        count = TaskFilterType.values().size
                    ),
                    colors = SegmentedButtonDefaults.colors(
                        activeContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        activeContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        inactiveContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                        inactiveContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = type.label,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
        
        // Sort controls
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
        ) {
            // Sort by label
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Rounded.Sort,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Text(
                    text = "Sort by:",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            // Sort criteria buttons
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TaskSortCriteria.values().forEach { criteria ->
                    val isSelected = sortCriteria == criteria
                    
                    Surface(
                        onClick = { onSortCriteriaChange(criteria) },
                        color = if (isSelected) 
                            MaterialTheme.colorScheme.secondaryContainer
                        else 
                            MaterialTheme.colorScheme.surfaceContainerLow,
                        contentColor = if (isSelected) 
                            MaterialTheme.colorScheme.onSecondaryContainer
                        else 
                            MaterialTheme.colorScheme.onSurfaceVariant,
                        shape = MaterialTheme.shapes.small
                    ) {
                        Text(
                            text = criteria.label,
                            style = MaterialTheme.typography.labelMedium,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
                
                // Direction button
                IconButton(
                    onClick = { onSortDirectionChange(!sortAscending) },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = if (sortAscending) 
                            Icons.Rounded.ArrowUpward 
                        else 
                            Icons.Rounded.ArrowDownward,
                        contentDescription = if (sortAscending) 
                            "Sort ascending" 
                        else 
                            "Sort descending",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
        
        Divider(
            color = MaterialTheme.colorScheme.outlineVariant,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        
        // Filter chips
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Rounded.FilterList,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Text(
                text = "Filters:",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            // Scrollable filter chips
            TaskFilterChip.values().forEach { chip ->
                val isSelected = selectedFilters.contains(chip)
                
                FilterChip(
                    selected = isSelected,
                    onClick = { onFilterChipSelected(chip, !isSelected) },
                    label = { Text(chip.label) },
                    leadingIcon = if (isSelected) {
                        {
                            Icon(
                                imageVector = Icons.Rounded.CheckCircle,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    } else null,
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = when (chip) {
                            TaskFilterChip.HIGH_PRIORITY -> AgileLifeTheme.extendedColors.accentCoral.copy(alpha = 0.2f)
                            TaskFilterChip.MEDIUM_PRIORITY -> AgileLifeTheme.extendedColors.accentSunflower.copy(alpha = 0.2f)
                            TaskFilterChip.LOW_PRIORITY -> AgileLifeTheme.extendedColors.accentMint.copy(alpha = 0.2f)
                            else -> MaterialTheme.colorScheme.secondaryContainer
                        },
                        selectedLabelColor = when (chip) {
                            TaskFilterChip.HIGH_PRIORITY -> AgileLifeTheme.extendedColors.accentCoral
                            TaskFilterChip.MEDIUM_PRIORITY -> AgileLifeTheme.extendedColors.accentSunflower
                            TaskFilterChip.LOW_PRIORITY -> AgileLifeTheme.extendedColors.accentMint
                            else -> MaterialTheme.colorScheme.onSecondaryContainer
                        },
                        selectedLeadingIconColor = when (chip) {
                            TaskFilterChip.HIGH_PRIORITY -> AgileLifeTheme.extendedColors.accentCoral
                            TaskFilterChip.MEDIUM_PRIORITY -> AgileLifeTheme.extendedColors.accentSunflower
                            TaskFilterChip.LOW_PRIORITY -> AgileLifeTheme.extendedColors.accentMint
                            else -> MaterialTheme.colorScheme.onSecondaryContainer
                        }
                    ),
                    modifier = Modifier.padding(end = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun TaskCountSummary(
    displayedCount: Int,
    totalCount: Int,
    filterType: TaskFilterType
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // Task count info
            Text(
                text = when (filterType) {
                    TaskFilterType.ALL -> "Showing $displayedCount of $totalCount tasks"
                    TaskFilterType.ACTIVE -> "Showing $displayedCount active tasks"
                    TaskFilterType.COMPLETED -> "Showing $displayedCount completed tasks"
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            // Status indicator
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(
                        color = when {
                            displayedCount == 0 -> MaterialTheme.colorScheme.outline
                            displayedCount < 5 -> AgileLifeTheme.extendedColors.accentMint
                            displayedCount < 10 -> AgileLifeTheme.extendedColors.accentSunflower
                            else -> AgileLifeTheme.extendedColors.accentCoral
                        }
                    )
            ) {
                Text(
                    text = displayedCount.toString(),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun EmptyTasksView(filterType: TaskFilterType) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Rounded.TaskAlt,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                modifier = Modifier.size(100.dp)
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = when (filterType) {
                    TaskFilterType.ALL -> "No tasks found"
                    TaskFilterType.ACTIVE -> "No active tasks"
                    TaskFilterType.COMPLETED -> "No completed tasks"
                },
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = when (filterType) {
                    TaskFilterType.ALL -> "Create a new task to get started"
                    TaskFilterType.ACTIVE -> "Create a new task or check your filters"
                    TaskFilterType.COMPLETED -> "Complete some tasks to see them here"
                },
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Button(
                onClick = { /* Add task action */ },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Add New Task")
            }
        }
    }
}

// Enums for task filtering and sorting
enum class TaskFilterType(val label: String) {
    ALL("All"),
    ACTIVE("Active"),
    COMPLETED("Completed")
}

enum class TaskSortCriteria(val label: String) {
    DUE_DATE("Due Date"),
    PRIORITY("Priority"),
    ESTIMATED_TIME("Est. Time")
}

enum class TaskFilterChip(
    val label: String,
    val isPriorityFilter: Boolean
) {
    HIGH_PRIORITY("High Priority", true),
    MEDIUM_PRIORITY("Medium Priority", true),
    LOW_PRIORITY("Low Priority", true),
    DUE_TODAY("Due Today", false)
}

/**
 * Sample data for the task list screen
 */
object SampleTaskData {
    fun getAllTasks(): List<TaskInfo> {
        return listOf(
            TaskInfo(
                id = "task1",
                title = "Create UI mockups for dashboard",
                priority = TaskPriority.HIGH,
                dueDate = "Today, 5:00 PM",
                estimatedMinutes = 120,
                isCompleted = false
            ),
            TaskInfo(
                id = "task2",
                title = "Review API documentation",
                priority = TaskPriority.MEDIUM,
                dueDate = "Tomorrow, 12:00 PM",
                estimatedMinutes = 60,
                isCompleted = false
            ),
            TaskInfo(
                id = "task3",
                title = "Set up CI/CD pipeline",
                priority = TaskPriority.HIGH,
                dueDate = "May 16, 3:00 PM",
                estimatedMinutes = 180,
                isCompleted = false
            ),
            TaskInfo(
                id = "task4",
                title = "Fix login screen animation bug",
                priority = TaskPriority.MEDIUM,
                dueDate = "Today, 2:00 PM",
                estimatedMinutes = 45,
                isCompleted = true
            ),
            TaskInfo(
                id = "task5",
                title = "Update project documentation",
                priority = TaskPriority.LOW,
                dueDate = "May 18",
                estimatedMinutes = 90,
                isCompleted = false
            ),
            TaskInfo(
                id = "task6",
                title = "Refactor utility functions",
                priority = TaskPriority.LOW,
                dueDate = null,
                estimatedMinutes = 120,
                isCompleted = false
            ),
            TaskInfo(
                id = "task7",
                title = "Implement dark mode theme",
                priority = TaskPriority.MEDIUM,
                dueDate = "May 20",
                estimatedMinutes = 150,
                isCompleted = false
            ),
            TaskInfo(
                id = "task8",
                title = "Write unit tests for authentication",
                priority = TaskPriority.HIGH,
                dueDate = "May 15",
                estimatedMinutes = 180,
                isCompleted = true
            ),
            TaskInfo(
                id = "task9",
                title = "Configure analytics tracking",
                priority = TaskPriority.MEDIUM,
                dueDate = "Today, 4:00 PM",
                estimatedMinutes = 60,
                isCompleted = false
            ),
            TaskInfo(
                id = "task10",
                title = "Conduct user testing session",
                priority = TaskPriority.HIGH,
                dueDate = "May 19, 10:00 AM",
                estimatedMinutes = 120,
                isCompleted = false
            )
        )
    }
}
