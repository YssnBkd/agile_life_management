package com.example.agilelifemanagement.ui.screens.goals

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.agilelifemanagement.domain.model.Goal
import com.example.agilelifemanagement.ui.components.CategoryBadge
import com.example.agilelifemanagement.ui.components.DeadlineBadge
import com.example.agilelifemanagement.ui.components.EmptyState
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalsScreen(
    viewModel: GoalsViewModel = hiltViewModel(),
    navigateToGoalDetail: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val goals by viewModel.goals.collectAsState()
    val filters by viewModel.activeFilters.collectAsState()
    val showCreateDialog by viewModel.showCreateDialog.collectAsState()
    val dateFormatter = remember { DateTimeFormatter.ofPattern("MMM dd, yyyy") }
    var showFiltersDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.loadGoals()
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Goals") },
                actions = {
                    IconButton(onClick = { showFiltersDialog = true }) {
                        Icon(Icons.Filled.FilterList, contentDescription = "Filter")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.setShowCreateDialog(true) },
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add Goal")
            }
        }
    ) { padding ->
        when (uiState) {
            is GoalsViewModel.UiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is GoalsViewModel.UiState.Empty -> {
                EmptyState(
                    icon = Icons.Filled.Star,
                    title = "No Goals Yet",
                    message = "Create your first goal to start tracking your progress",
                    actionLabel = "Create Goal",
                    onActionClick = { viewModel.setShowCreateDialog(true) },
                    modifier = Modifier.padding(padding)
                )
            }
            is GoalsViewModel.UiState.Success -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(goals) { goal ->
                        GoalCard(
                            goal = goal,
                            dateFormatter = dateFormatter,
                            onClick = { navigateToGoalDetail(goal.id) }
                        )
                    }
                }
            }
            is GoalsViewModel.UiState.Error -> {
                val error = (uiState as GoalsViewModel.UiState.Error).message
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = "Error: $error",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
    
    // Goal Create Dialog
    if (showCreateDialog) {
        GoalEditDialog(
            onDismiss = { viewModel.setShowCreateDialog(false) },
            onSave = { title, summary, category, deadline ->
                viewModel.createGoal(title, summary, category, deadline)
            }
        )
    }
    
    // Filters Dialog
    if (showFiltersDialog) {
        AlertDialog(
            onDismissRequest = { showFiltersDialog = false },
            title = { Text("Filter Goals") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Category")
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Goal.Category.values().forEach { category ->
                            FilterChip(
                                selected = filters.category == category,
                                onClick = {
                                    viewModel.updateFilters(
                                        filters.copy(category = if (filters.category == category) null else category)
                                    )
                                },
                                label = { Text(category.name.lowercase().replaceFirstChar { it.uppercase() }) }
                            )
                        }
                    }
                    
                    Text("Completion")
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            selected = filters.showCompleted == true,
                            onClick = {
                                viewModel.updateFilters(
                                    filters.copy(showCompleted = if (filters.showCompleted == true) null else true)
                                )
                            },
                            label = { Text("Completed") }
                        )
                        FilterChip(
                            selected = filters.showCompleted == false,
                            onClick = {
                                viewModel.updateFilters(
                                    filters.copy(showCompleted = if (filters.showCompleted == false) null else false)
                                )
                            },
                            label = { Text("In Progress") }
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.updateFilters(GoalsViewModel.GoalFilters())
                        showFiltersDialog = false
                    }
                ) {
                    Text("Reset")
                }
            },
            dismissButton = {
                TextButton(onClick = { showFiltersDialog = false }) {
                    Text("Close")
                }
            }
        )
    }
}

@Composable
private fun GoalCard(goal: Goal, dateFormatter: DateTimeFormatter, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = if (goal.isCompleted) 
                MaterialTheme.colorScheme.surfaceVariant 
            else 
                MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = goal.title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row {
                CategoryBadge(category = goal.category)
                Spacer(modifier = Modifier.width(8.dp))
                goal.deadline?.let { deadline ->
                    val daysUntil = ChronoUnit.DAYS.between(LocalDate.now(), deadline)
                    DeadlineBadge(daysUntil = daysUntil, deadline = deadline, dateFormatter = dateFormatter)
                }
            }
            
            if (goal.summary.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = goal.summary,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { goal.progress },
                modifier = Modifier.fillMaxWidth(),
                color = when {
                    goal.isCompleted -> MaterialTheme.colorScheme.tertiary
                    goal.progress > 0.75f -> MaterialTheme.colorScheme.primary
                    goal.progress > 0.25f -> MaterialTheme.colorScheme.secondary
                    else -> MaterialTheme.colorScheme.error
                }
            )
        }
    }
}
