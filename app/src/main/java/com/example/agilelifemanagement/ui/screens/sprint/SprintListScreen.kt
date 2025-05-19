package com.example.agilelifemanagement.ui.screens.sprint

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.FilterList
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.agilelifemanagement.domain.model.Sprint
import com.example.agilelifemanagement.ui.components.cards.SprintCard
import com.example.agilelifemanagement.ui.components.cards.SprintStatus as UISprintStatus
import com.example.agilelifemanagement.domain.model.SprintStatus as DomainSprintStatus
import com.example.agilelifemanagement.ui.viewmodel.SprintViewModel

/**
 * SprintListScreen displays all sprints with filtering options
 * 
 * Features following Material 3 Expressive principles:
 * - Vibrant status indication with contrasting colors
 * - Interactive filter chips with tactile feedback
 * - Clear visual hierarchy with categorized sprint groups
 * - Attention-grabbing Create New Sprint FAB
 * - Integration with SprintViewModel for real data 
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SprintListScreen(
    onSprintClick: (String) -> Unit,
    onCreateSprintClick: () -> Unit,
    onSearchClick: () -> Unit,
    onProfileClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SprintViewModel = hiltViewModel()
) {
    // Get the UI state from the ViewModel
    val uiState by viewModel.uiState.collectAsState()
    
    // SnackbarHostState to display error messages
    val snackbarHostState = remember { SnackbarHostState() }
    
    // Show error message if it exists
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            snackbarHostState.showSnackbar(message = it)
            viewModel.clearError()
        }
    }
    
    // Filter state (convert domain SprintStatus to UI SprintFilterOption)
    var selectedFilters by remember { mutableStateOf(setOf(SprintFilterOption.ACTIVE)) }
    
    // Top app bar scroll behavior
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    
    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Sprints",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    IconButton(onClick = onSearchClick) {
                        Icon(
                            imageVector = Icons.Rounded.Search,
                            contentDescription = "Search Sprints",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    IconButton(onClick = onProfileClick) {
                        Icon(
                            imageVector = Icons.Rounded.AccountCircle,
                            contentDescription = "Profile",
                            modifier = Modifier.size(26.dp)
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
                onClick = onCreateSprintClick,
                icon = {
                    Icon(
                        imageVector = Icons.Rounded.Add,
                        contentDescription = null
                    )
                },
                text = { Text("New Sprint") },
                expanded = true,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        }
    ) { innerPadding ->
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            SprintListContent(
                sprints = uiState.filteredSprints,
                selectedFilters = selectedFilters,
                onFilterChange = { filter, selected ->
                    selectedFilters = if (selected) {
                        selectedFilters + filter
                    } else {
                        // Ensure at least one filter is always selected
                        if (selectedFilters.size > 1) selectedFilters - filter
                        else selectedFilters
                    }
                    
                    // Convert UI filter options to domain SprintStatus
                    val statusFilters = selectedFilters.map { filterOption ->
                        when (filterOption) {
                            SprintFilterOption.ACTIVE -> DomainSprintStatus.ACTIVE
                            SprintFilterOption.PLANNED -> DomainSprintStatus.PLANNED
                            SprintFilterOption.COMPLETED -> DomainSprintStatus.COMPLETED
                        }
                    }.toSet()
                    
                    // Apply filter in the ViewModel
                    viewModel.filterSprints(statusFilters)
                },
                onSprintClick = onSprintClick,
                contentPadding = innerPadding
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SprintListContent(
    sprints: List<Sprint>,
    selectedFilters: Set<SprintFilterOption>,
    onFilterChange: (SprintFilterOption, Boolean) -> Unit,
    onSprintClick: (String) -> Unit,
    contentPadding: PaddingValues
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
    ) {
        // Filter chips row
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 1.dp,
            shadowElevation = 1.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Rounded.FilterList,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                
                SprintFilterOption.values().forEach { option ->
                    FilterChip(
                        selected = selectedFilters.contains(option),
                        onClick = { onFilterChange(option, !selectedFilters.contains(option)) },
                        label = { Text(option.label) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )
                }
            }
        }
        
        // Sprint list
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .animateContentSize(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                ),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            // Map domain status to UI status for grouping
            val sprintsWithUIStatus = sprints.map { sprint ->
                sprint to when(sprint.status) {
                    DomainSprintStatus.ACTIVE -> UISprintStatus.ACTIVE
                    DomainSprintStatus.PLANNED -> UISprintStatus.PLANNED
                    DomainSprintStatus.COMPLETED -> UISprintStatus.COMPLETED
                }
            }
            
            // Group sprints by their UI status
            val groupedSprints = sprintsWithUIStatus.groupBy { it.second }
            
            // Active sprints
            if (selectedFilters.contains(SprintFilterOption.ACTIVE)) {
                val activeSprints = groupedSprints[UISprintStatus.ACTIVE]
                if (activeSprints != null && activeSprints.isNotEmpty()) {
                    item {
                        SprintGroupHeader("Active Sprints")
                    }
                    items(activeSprints) { (sprint, uiStatus) ->
                        SprintCard(
                            title = sprint.name,
                            status = uiStatus,
                            progressPercent = sprint.progress.toFloat(),
                            dateRange = "${sprint.startDate} - ${sprint.endDate}",
                            tasksCompleted = sprint.completedTaskCount,
                            totalTasks = sprint.taskCount,
                            onClick = { onSprintClick(sprint.id) }
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }
            
            // Upcoming/Planned sprints
            if (selectedFilters.contains(SprintFilterOption.PLANNED)) {
                val plannedSprints = groupedSprints[UISprintStatus.PLANNED]
                if (plannedSprints != null && plannedSprints.isNotEmpty()) {
                    item {
                        SprintGroupHeader("Planned Sprints")
                    }
                    items(plannedSprints) { (sprint, uiStatus) ->
                        SprintCard(
                            title = sprint.name,
                            status = uiStatus,
                            progressPercent = sprint.progress.toFloat(),
                            dateRange = "${sprint.startDate} - ${sprint.endDate}",
                            tasksCompleted = sprint.completedTaskCount,
                            totalTasks = sprint.taskCount,
                            onClick = { onSprintClick(sprint.id) }
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }
            
            // Completed sprints
            if (selectedFilters.contains(SprintFilterOption.COMPLETED)) {
                val completedSprints = groupedSprints[UISprintStatus.COMPLETED]
                if (completedSprints != null && completedSprints.isNotEmpty()) {
                    item {
                        SprintGroupHeader("Completed Sprints")
                    }
                    items(completedSprints) { (sprint, uiStatus) ->
                        SprintCard(
                            title = sprint.name,
                            status = uiStatus,
                            progressPercent = sprint.progress.toFloat(),
                            dateRange = "${sprint.startDate} - ${sprint.endDate}",
                            tasksCompleted = sprint.completedTaskCount,
                            totalTasks = sprint.taskCount,
                            onClick = { onSprintClick(sprint.id) }
                        )
                    }
                }
            }
            
            // We don't have a CANCELLED status in the current implementation
            
            // Empty state
            if (sprints.isEmpty()) {
                item {
                    EmptySprintList(selectedFilters)
                }
            }
            
            // Bottom spacing for FAB
            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

@Composable
private fun SprintGroupHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

@Composable
private fun EmptySprintList(filters: Set<SprintFilterOption>) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "No ${filters.joinToString(" or ") { it.label }} Sprints",
                style = MaterialTheme.typography.titleMedium
            )
            
            Text(
                text = "Try changing filters or create a new sprint",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Filter options for sprints
 */
enum class SprintFilterOption(val label: String) {
    ACTIVE("Active"),
    PLANNED("Planned"),
    COMPLETED("Completed")
}

/**
 * Preview helper for testing the UI without a real ViewModel
 */
object SprintPreviewData {
    val previewSprints = listOf(
        Sprint(
            id = "sprint1",
            name = "Sprint 1",
            goal = "Complete authentication features",
            description = "Implement login, registration and password reset",
            status = DomainSprintStatus.COMPLETED,
            startDate = java.time.LocalDate.now().minusDays(28),
            endDate = java.time.LocalDate.now().minusDays(14),
            progress = 100,
            taskCount = 8,
            completedTaskCount = 8,
            createdDate = java.time.LocalDate.now().minusDays(35)
        ),
        Sprint(
            id = "sprint2",
            name = "Sprint 2",
            goal = "Task management features",
            description = "Implement task backlog, editing, and filtering",
            status = DomainSprintStatus.ACTIVE,
            startDate = java.time.LocalDate.now().minusDays(13),
            endDate = java.time.LocalDate.now().plusDays(1),
            progress = 75,
            taskCount = 12,
            completedTaskCount = 9,
            createdDate = java.time.LocalDate.now().minusDays(14)
        ),
        Sprint(
            id = "sprint3",
            name = "Sprint 3",
            goal = "Sprint management and review features",
            description = "Implement sprint creation, tracking, and retrospectives",
            status = DomainSprintStatus.PLANNED,
            startDate = java.time.LocalDate.now().plusDays(2),
            endDate = java.time.LocalDate.now().plusDays(16),
            progress = 0,
            taskCount = 10,
            completedTaskCount = 0,
            createdDate = java.time.LocalDate.now().minusDays(7)
        )
    )
}
