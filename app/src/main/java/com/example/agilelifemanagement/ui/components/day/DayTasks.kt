package com.example.agilelifemanagement.ui.components.day

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.agilelifemanagement.ui.components.cards.ExpressiveCard
import com.example.agilelifemanagement.ui.components.cards.TaskCard
import com.example.agilelifemanagement.ui.components.cards.TaskPriority
import com.example.agilelifemanagement.ui.screens.dashboard.TaskInfo
import com.example.agilelifemanagement.ui.theme.AgileLifeTheme

/**
 * Card displaying tasks for a day.
 *
 * @param tasks List of tasks to display
 * @param onTaskClick Callback when a task is clicked
 * @param modifier Optional modifier
 */
@Composable
fun DayTasks(
    tasks: List<TaskInfo>,
    onTaskClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    ExpressiveCard(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Card title
            Text(
                text = "Tasks",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            if (tasks.isEmpty()) {
                // Empty state
                Text(
                    text = "No tasks scheduled for today",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp)
                )
            } else {
                // Task list
                Column {
                    tasks.forEach { task ->
                        TaskCard(
                            title = task.title,
                            description = task.description,
                            priority = task.priority,
                            dueDate = task.dueDate,
                            isCompleted = task.isCompleted,
                            onClick = { onTaskClick(task.id) },
                            estimatedMinutes = task.estimatedMinutes,
                            onCompletedChange = { /* Handled through task details */ },
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Task stats
                val completedCount = tasks.count { it.isCompleted }
                Text(
                    text = "$completedCount of ${tasks.size} tasks completed",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DayTasksPreview() {
    AgileLifeTheme {
        val sampleTasks = listOf(
            TaskInfo(
                id = "1",
                title = "Complete project proposal",
                description = "Draft and send the project proposal to the client",
                estimatedMinutes = 120,
                priority = TaskPriority.HIGH,
                dueDate = "Today, 17:00",
                isCompleted = false
            ),
            TaskInfo(
                id = "2",
                title = "Daily team standup",
                description = "Discuss progress and blockers with the team",
                estimatedMinutes = 30,
                priority = TaskPriority.MEDIUM,
                dueDate = "Today, 09:00",
                isCompleted = true
            ),
            TaskInfo(
                id = "3",
                title = "Review pull request #42",
                description = "Code review for the authentication feature",
                estimatedMinutes = 45,
                priority = TaskPriority.MEDIUM,
                dueDate = "Today, 16:00",
                isCompleted = false
            )
        )
        
        DayTasks(
            tasks = sampleTasks,
            onTaskClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun EmptyDayTasksPreview() {
    AgileLifeTheme {
        DayTasks(
            tasks = emptyList(),
            onTaskClick = {}
        )
    }
}
