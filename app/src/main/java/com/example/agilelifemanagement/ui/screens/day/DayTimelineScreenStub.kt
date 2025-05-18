package com.example.agilelifemanagement.ui.screens.day

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

/**
 * Simplified version of the DayTimelineScreen for navigation testing
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DayTimelineScreen(
    navController: NavController,
    selectedDate: java.time.LocalDate = java.time.LocalDate.now()
) {
    // Extract navigation functions from NavController
    val onBackClick: () -> Unit = { navController.navigateUp() }
    val onDayClick: (String) -> Unit = { date -> navController.navigate("day/timeline?date=$date") }
    val onActivityClick: (String) -> Unit = { activityId -> navController.navigate("day/activities/$activityId") }
    val onCreateActivityClick: (String) -> Unit = { date -> navController.navigate("day/activities/create?date=$date") }
    val onWellnessCheckupClick: (String) -> Unit = { date -> navController.navigate("wellness?date=$date") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Day Timeline: ${selectedDate}") },
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
                onClick = { onCreateActivityClick(selectedDate.toString()) },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(
                    imageVector = Icons.Rounded.Add,
                    contentDescription = "Add Activity"
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
            Text("Day Timeline Screen - Stub Implementation")
        }
    }
}
