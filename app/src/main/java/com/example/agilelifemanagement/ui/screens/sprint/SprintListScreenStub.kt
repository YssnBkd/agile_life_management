package com.example.agilelifemanagement.ui.screens.sprint

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Simplified version of the SprintListScreen for navigation testing
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SprintListScreen(
    navController: androidx.navigation.NavController,
    onSearchClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    // Extract the parameters we need from the NavController
    val onBackClick: () -> Unit = { navController.navigateUp() }
    val onSprintClick: (String) -> Unit = { sprintId -> navController.navigate("sprints/$sprintId") }
    val onCreateSprintClick: () -> Unit = { navController.navigate("sprints/create") }
    val onTaskClick: (String) -> Unit = { taskId -> navController.navigate("tasks/$taskId") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Sprints") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Rounded.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onCreateSprintClick,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(
                    imageVector = Icons.Rounded.Add,
                    contentDescription = "Create Sprint"
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            Text("Sprint List Screen - Stub Implementation")
        }
    }
}
