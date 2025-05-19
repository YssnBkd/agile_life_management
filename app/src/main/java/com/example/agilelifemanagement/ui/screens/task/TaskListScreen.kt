package com.example.agilelifemanagement.ui.screens.task

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.agilelifemanagement.domain.model.TaskStatus
import com.example.agilelifemanagement.ui.components.cards.TaskPriority
import com.example.agilelifemanagement.ui.model.TaskFilterType
import com.example.agilelifemanagement.ui.screens.task.components.EmptyTasksView
import com.example.agilelifemanagement.ui.screens.task.components.TaskCountSummary
import com.example.agilelifemanagement.ui.screens.task.components.TaskFiltersBar
import com.example.agilelifemanagement.ui.screens.task.components.TaskList
import com.example.agilelifemanagement.ui.screens.task.components.TaskSearchBar
import com.example.agilelifemanagement.ui.viewmodel.TaskViewModel

/**
 * TaskListScreen displays all tasks with filtering, sorting and search functionality
 * following Material 3 Expressive design principles and properly using ViewModel with StateFlow.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListScreen(
    onTaskClick: (String) -> Unit,
    onAddTaskClick: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TaskViewModel = hiltViewModel()
) {
    // Get UI state from ViewModel
    val uiState by viewModel.uiState.collectAsState()
    
    // State for search functionality
    var isSearchActive by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    
    // Create SnackbarHostState for showing error messages
    val snackbarHostState = remember { SnackbarHostState() }
    
    // Show error messages as snackbars
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
        }
    }
    
    // Top app bar scroll behavior
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    
    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            if (isSearchActive) {
                // Search bar when active
                TaskSearchBar(
                    query = searchQuery,
                    onQueryChange = { 
                        searchQuery = it
                        viewModel.searchTasks(it)
                    },
                    onSearch = { viewModel.searchTasks(searchQuery) },
                    active = true,
                    onActiveChange = { isSearchActive = it },
                    onBackClick = { isSearchActive = false },
                    placeholder = "Search tasks"
                )
            } else {
                // Standard top app bar
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
            }
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = { Text("Add Task") },
                icon = { 
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add task"
                    )
                },
                onClick = onAddTaskClick,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
        ) {
            Column {
                // Task filters bar
                TaskFiltersBar(
                    filterType = uiState.filterType,
                    onFilterTypeChange = { filterType -> viewModel.setFilterType(filterType) },
                    sortCriteria = uiState.sortCriteria,
                    onSortCriteriaChange = { criteria -> viewModel.setSortCriteria(criteria) },
                    sortAscending = uiState.sortAscending,
                    onSortDirectionChange = { ascending -> viewModel.setSortDirection(ascending) },
                    selectedFilters = uiState.selectedFilters,
                    onFilterChipSelected = { chip, selected -> viewModel.updateSelectedFilters(chip, selected) }
                )
                
                // Task count summary
                TaskCountSummary(
                    displayedCount = uiState.filteredTasks.size,
                    totalCount = uiState.tasks.size,
                    filterType = uiState.filterType
                )
                
                // Task list with empty state handling
                TaskList(
                    tasks = uiState.filteredTasks,
                    isLoading = uiState.isLoading,
                    onTaskClick = onTaskClick,
                    onCompletedChange = { taskId, isCompleted ->
                        viewModel.updateTaskStatus(
                            taskId, 
                            if (isCompleted) TaskStatus.COMPLETED else TaskStatus.IN_PROGRESS
                        )
                    },
                    emptyContent = {
                        EmptyTasksView(
                            filterType = uiState.filterType,
                            onAddClick = onAddTaskClick,
                            modifier = Modifier.fillMaxSize()
                        )
                    },
                    modifier = Modifier.weight(1f)
                )
            }
            
            // Pull to refresh indicator, if implemented
            AnimatedVisibility(
                visible = uiState.isRefreshing,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically(),
                modifier = Modifier.align(Alignment.TopCenter)
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}

// Helper function to map domain priority to UI priority
private fun mapDomainToUiPriority(domainPriority: com.example.agilelifemanagement.domain.model.TaskPriority): TaskPriority {
    return when (domainPriority) {
        com.example.agilelifemanagement.domain.model.TaskPriority.LOW -> TaskPriority.LOW
        com.example.agilelifemanagement.domain.model.TaskPriority.MEDIUM -> TaskPriority.MEDIUM
        com.example.agilelifemanagement.domain.model.TaskPriority.HIGH -> TaskPriority.HIGH
        com.example.agilelifemanagement.domain.model.TaskPriority.URGENT -> TaskPriority.CRITICAL
    }
}
