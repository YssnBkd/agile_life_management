package com.example.agilelifemanagement.ui.screens.task

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

/**
 * Simplified version of the TaskEditorScreen for navigation testing
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskEditorScreen(
    navController: NavController,
    taskId: String? = null
) {
    // Determine if we're editing an existing task or creating a new one
    val isEditing = taskId != null
    val screenTitle = if (isEditing) "Edit Task" else "Create Task"
    
    // Extract navigation functions from NavController
    val onBackClick: () -> Unit = { navController.navigateUp() }
    val onSaveClick: () -> Unit = { 
        // After saving, navigate back
        navController.navigateUp() 
    }
    val onDeleteClick: () -> Unit = { 
        // After delete, navigate back
        navController.navigateUp() 
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(screenTitle) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Rounded.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    if (isEditing) {
                        IconButton(onClick = onDeleteClick) {
                            Icon(
                                imageVector = Icons.Rounded.Delete,
                                contentDescription = "Delete Task"
                            )
                        }
                    }
                    
                    IconButton(onClick = onSaveClick) {
                        Icon(
                            imageVector = Icons.Rounded.Check,
                            contentDescription = "Save Task"
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
                if (isEditing) {
                    Text(
                        text = "Editing Task ID: $taskId",
                        style = MaterialTheme.typography.headlineSmall
                    )
                } else {
                    Text(
                        text = "Creating New Task",
                        style = MaterialTheme.typography.headlineSmall
                    )
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Text(
                    text = "Task Editor Screen - Stub Implementation",
                    style = MaterialTheme.typography.bodyLarge
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                
                Button(
                    onClick = onSaveClick,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Save Task")
                }
            }
        }
    }
}
