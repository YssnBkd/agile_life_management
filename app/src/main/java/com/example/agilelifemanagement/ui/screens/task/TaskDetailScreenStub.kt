package com.example.agilelifemanagement.ui.screens.task

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

/**
 * Simplified version of the TaskDetailScreen for navigation testing
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDetailScreen(
    navController: NavController,
    taskId: String
) {
    // Extract navigation functions from NavController
    val onBackClick: () -> Unit = { navController.navigateUp() }
    val onEditClick: (String) -> Unit = { id -> navController.navigate("tasks/edit/$id") }
    val onDeleteTask: (String) -> Unit = { 
        // After delete, navigate back
        navController.navigateUp() 
    }
    val onAssignToSprint: (String, String) -> Unit = { taskId, sprintId ->
        // Implementation would assign the task to the sprint and navigate
        navController.navigateUp()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Task Details") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Rounded.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { onEditClick(taskId) }) {
                        Icon(
                            imageVector = Icons.Rounded.Edit,
                            contentDescription = "Edit Task"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Task ID: $taskId",
                    style = MaterialTheme.typography.headlineMedium
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Text(
                    text = "Task Detail Screen - Stub Implementation",
                    style = MaterialTheme.typography.bodyLarge
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                
                Button(
                    onClick = { onDeleteTask(taskId) },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete Task")
                }
            }
        }
    }
}
