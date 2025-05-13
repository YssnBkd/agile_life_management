package com.example.agilelifemanagement.ui.screens.goals

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.agilelifemanagement.domain.model.Goal
import com.example.agilelifemanagement.domain.model.Tag
import com.example.agilelifemanagement.ui.components.CategoryBadge
import com.example.agilelifemanagement.ui.components.DeadlineBadge
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

private const val TAG = "GoalDetailScreen"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalDetailScreen(
    goalId: String,
    viewModel: GoalsViewModel = hiltViewModel(),
    onBack: () -> Unit = {},
    navigateToTask: (String) -> Unit = {},
    navigateToSprint: (String) -> Unit = {}
) {
    val goalDetailState by viewModel.selectedGoalState.collectAsState()
    val tags by viewModel.tags.collectAsState()
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    val dateFormatter = remember { DateTimeFormatter.ofPattern("MMMM d, yyyy") }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(goalId) {
        viewModel.loadGoalDetail(goalId)
        Log.d(TAG, "Loading goal details for ID: $goalId")
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Goal Details") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = { showEditDialog = true }
                    ) {
                        Icon(Icons.Filled.Edit, contentDescription = "Edit")
                    }
                    IconButton(
                        onClick = { showDeleteDialog = true }
                    ) {
                        Icon(Icons.Filled.Delete, contentDescription = "Delete")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        when (goalDetailState) {
            is GoalsViewModel.GoalDetailUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is GoalsViewModel.GoalDetailUiState.Success -> {
                val goal = (goalDetailState as GoalsViewModel.GoalDetailUiState.Success).goal
                GoalDetailContent(
                    goal = goal,
                    dateFormatter = dateFormatter,
                    navigateToTask = navigateToTask,
                    modifier = Modifier.padding(padding)
                )
            }
            is GoalsViewModel.GoalDetailUiState.NotFound -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Goal not found")
                }
            }
            is GoalsViewModel.GoalDetailUiState.Error -> {
                val errorMsg = (goalDetailState as GoalsViewModel.GoalDetailUiState.Error).message
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Error: $errorMsg",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
            else -> {
                // Idle state, do nothing
            }
        }
    }

    // Delete Confirmation Dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Goal") },
            text = { Text("Are you sure you want to delete this goal? This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteGoal(goalId)
                        showDeleteDialog = false
                        onBack()
                    }
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Edit Dialog
    if (showEditDialog && goalDetailState is GoalsViewModel.GoalDetailUiState.Success) {
        val goal = (goalDetailState as GoalsViewModel.GoalDetailUiState.Success).goal
        GoalEditDialog(
            goal = goal,
            onDismiss = { showEditDialog = false },
            onSave = { title, summary, category, deadline ->
                val updatedGoal = goal.copy(
                    title = title,
                    summary = summary,
                    category = category,
                    deadline = deadline
                )
                viewModel.updateGoal(updatedGoal)
                showEditDialog = false
            }
        )
    }
}

@Composable
fun GoalDetailContent(
    goal: Goal,
    dateFormatter: DateTimeFormatter,
    navigateToTask: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Title and Category
        Text(
            text = goal.title,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Category and Deadline
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CategoryBadge(category = goal.category)
            
            goal.deadline?.let { deadline ->
                val today = LocalDate.now()
                val daysUntil = ChronoUnit.DAYS.between(today, deadline)
                DeadlineBadge(daysUntil = daysUntil, deadline = deadline, dateFormatter = dateFormatter)
            }
            
            CompletionBadge(isCompleted = goal.isCompleted)
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Progress
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Progress: ${(goal.progress * 100).toInt()}%",
                    style = MaterialTheme.typography.titleMedium
                )
                
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
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Summary
        if (goal.summary.isNotBlank()) {
            Text(
                text = "Summary",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = goal.summary,
                style = MaterialTheme.typography.bodyLarge
            )
            
            Spacer(modifier = Modifier.height(16.dp))
        }
        
        // Description (if any)
        if (goal.description.isNotEmpty()) {
            Text(
                text = "Description",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            goal.description.forEach { paragraph ->
                Text(
                    text = paragraph,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }
        
        // Related Tasks section (stub for now)
        Text(
            text = "Related Tasks",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // This would be populated with actual related tasks
        // For now, we'll show a placeholder
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No related tasks yet",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun CompletionBadge(isCompleted: Boolean) {
    val (backgroundColor, textColor, text) = if (isCompleted) {
        Triple(Color(0xFFE8F5E9), Color(0xFF388E3C), "Completed")
    } else {
        Triple(Color(0xFFF3E5F5), Color(0xFF7B1FA2), "In Progress")
    }
    
    Surface(
        shape = CircleShape,
        color = backgroundColor
    ) {
        Text(
            text = text,
            color = textColor,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}
