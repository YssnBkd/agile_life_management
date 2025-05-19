package com.example.agilelifemanagement.ui.screens.task.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.agilelifemanagement.domain.model.Task
import com.example.agilelifemanagement.domain.model.TaskStatus
import com.example.agilelifemanagement.ui.components.cards.TaskCard
import com.example.agilelifemanagement.ui.components.cards.TaskPriority
import java.time.format.DateTimeFormatter

/**
 * A reusable component that displays a list of tasks with loading and empty states.
 * This follows the architectural principle of component abstraction.
 *
 * @param tasks List of tasks to display
 * @param isLoading Whether the tasks are currently loading
 * @param onTaskClick Callback when a task is clicked
 * @param onCompletedChange Callback when a task's completion status changes
 * @param emptyContent Content to display when there are no tasks
 * @param modifier Optional modifier
 */
@Composable
fun TaskList(
    tasks: List<Task>,
    isLoading: Boolean,
    onTaskClick: (String) -> Unit,
    onCompletedChange: (String, Boolean) -> Unit,
    emptyContent: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when {
            // Show loading indicator
            isLoading -> {
                CircularProgressIndicator()
            }
            
            // Show empty state
            tasks.isEmpty() -> {
                emptyContent()
            }
            
            // Show task list
            else -> {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(
                        items = tasks,
                        key = { it.id }
                    ) { task ->
                        TaskCard(
                            title = task.title,
                            description = task.description,
                            priority = mapDomainToUiPriority(task.priority),
                            dueDate = task.dueDate?.format(DateTimeFormatter.ofPattern("MMM d, yyyy")),
                            isCompleted = task.status == TaskStatus.COMPLETED,
                            estimatedMinutes = 0, // Defaulting to zero since domain Task model doesn't have this field
                            onClick = { onTaskClick(task.id) },
                            onCompletedChange = { newStatus ->
                                onCompletedChange(
                                    task.id, 
                                    newStatus
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}

/**
 * Helper function to map domain priority to UI priority
 */
private fun mapDomainToUiPriority(domainPriority: com.example.agilelifemanagement.domain.model.TaskPriority): TaskPriority {
    return when (domainPriority) {
        com.example.agilelifemanagement.domain.model.TaskPriority.LOW -> TaskPriority.LOW
        com.example.agilelifemanagement.domain.model.TaskPriority.MEDIUM -> TaskPriority.MEDIUM
        com.example.agilelifemanagement.domain.model.TaskPriority.HIGH -> TaskPriority.HIGH
        com.example.agilelifemanagement.domain.model.TaskPriority.URGENT -> TaskPriority.CRITICAL
    }
}
