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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
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
import com.example.agilelifemanagement.ui.components.cards.SprintCard
import com.example.agilelifemanagement.ui.components.cards.SprintStatus
import com.example.agilelifemanagement.ui.screens.dashboard.SprintInfo

/**
 * SprintListScreen displays all sprints with filtering options
 * 
 * Features following Material 3 Expressive principles:
 * - Vibrant status indication with contrasting colors
 * - Interactive filter chips with tactile feedback
 * - Clear visual hierarchy with categorized sprint groups
 * - Attention-grabbing Create New Sprint FAB 
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SprintListScreen(
    onSprintClick: (String) -> Unit,
    onCreateSprintClick: () -> Unit,
    onSearchClick: () -> Unit,
    onProfileClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Sample data - would come from ViewModel in real implementation
    val sprints = remember { SampleSprintData.allSprints }
    
    // Filter state
    var selectedFilters by remember { mutableStateOf(setOf(SprintFilterOption.ACTIVE)) }
    
    // Top app bar scroll behavior
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    
    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
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
        SprintListContent(
            sprints = sprints,
            selectedFilters = selectedFilters,
            onFilterChange = { filter, selected ->
                selectedFilters = if (selected) {
                    selectedFilters + filter
                } else {
                    // Ensure at least one filter is always selected
                    if (selectedFilters.size > 1) selectedFilters - filter
                    else selectedFilters
                }
            },
            onSprintClick = onSprintClick,
            contentPadding = innerPadding
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SprintListContent(
    sprints: List<SprintInfo>,
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
            // Filter and group sprints
            val filteredSprints = sprints.filter { sprint ->
                when (sprint.status) {
                    SprintStatus.ACTIVE -> selectedFilters.contains(SprintFilterOption.ACTIVE)
                    SprintStatus.PLANNED -> selectedFilters.contains(SprintFilterOption.PLANNED)
                    SprintStatus.COMPLETED -> selectedFilters.contains(SprintFilterOption.COMPLETED)
                }
            }
            
            // Active sprints
            if (selectedFilters.contains(SprintFilterOption.ACTIVE)) {
                val activeSprints = filteredSprints.filter { it.status == SprintStatus.ACTIVE }
                if (activeSprints.isNotEmpty()) {
                    item {
                        SprintGroupHeader("Active Sprints")
                    }
                    items(activeSprints) { sprint ->
                        SprintCard(
                            title = sprint.name,
                            status = sprint.status,
                            progressPercent = sprint.progressPercent,
                            dateRange = sprint.dateRange,
                            tasksCompleted = sprint.tasksCompleted,
                            totalTasks = sprint.totalTasks,
                            onClick = { onSprintClick(sprint.id) }
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }
            
            // Planned sprints
            if (selectedFilters.contains(SprintFilterOption.PLANNED)) {
                val plannedSprints = filteredSprints.filter { it.status == SprintStatus.PLANNED }
                if (plannedSprints.isNotEmpty()) {
                    item {
                        SprintGroupHeader("Planned Sprints")
                    }
                    items(plannedSprints) { sprint ->
                        SprintCard(
                            title = sprint.name,
                            status = sprint.status,
                            progressPercent = sprint.progressPercent,
                            dateRange = sprint.dateRange,
                            tasksCompleted = sprint.tasksCompleted,
                            totalTasks = sprint.totalTasks,
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
                val completedSprints = filteredSprints.filter { it.status == SprintStatus.COMPLETED }
                if (completedSprints.isNotEmpty()) {
                    item {
                        SprintGroupHeader("Completed Sprints")
                    }
                    items(completedSprints) { sprint ->
                        SprintCard(
                            title = sprint.name,
                            status = sprint.status,
                            progressPercent = sprint.progressPercent,
                            dateRange = sprint.dateRange,
                            tasksCompleted = sprint.tasksCompleted,
                            totalTasks = sprint.totalTasks,
                            onClick = { onSprintClick(sprint.id) }
                        )
                    }
                }
            }
            
            // Empty state
            if (filteredSprints.isEmpty()) {
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
 * Sample data for preview and testing
 */
object SampleSprintData {
    val allSprints = listOf(
        SprintInfo(
            id = "sprint-1",
            name = "Sprint 23: Dashboard Implementation",
            status = SprintStatus.ACTIVE,
            dateRange = "May 14 - May 28",
            progressPercent = 0.35f,
            tasksCompleted = 7,
            totalTasks = 20
        ),
        SprintInfo(
            id = "sprint-2",
            name = "Sprint 24: Sprint Module",
            status = SprintStatus.PLANNED,
            dateRange = "May 29 - Jun 12",
            progressPercent = 0f,
            tasksCompleted = 0,
            totalTasks = 18
        ),
        SprintInfo(
            id = "sprint-3",
            name = "Sprint 25: Day Module",
            status = SprintStatus.PLANNED,
            dateRange = "Jun 13 - Jun 27",
            progressPercent = 0f,
            tasksCompleted = 0,
            totalTasks = 15
        ),
        SprintInfo(
            id = "sprint-4",
            name = "Sprint 22: Task Management",
            status = SprintStatus.COMPLETED,
            dateRange = "Apr 29 - May 13",
            progressPercent = 1f,
            tasksCompleted = 22,
            totalTasks = 22
        ),
        SprintInfo(
            id = "sprint-5",
            name = "Sprint 21: Authentication",
            status = SprintStatus.COMPLETED,
            dateRange = "Apr 15 - Apr 28",
            progressPercent = 0.9f,
            tasksCompleted = 18,
            totalTasks = 20
        )
    )
}
