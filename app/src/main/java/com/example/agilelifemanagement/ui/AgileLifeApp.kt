package com.example.agilelifemanagement.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.agilelifemanagement.ui.navigation.AgileLifeBottomNavigation
import com.example.agilelifemanagement.ui.navigation.AgileLifeNavHost
import com.example.agilelifemanagement.ui.navigation.BottomNavDestination
import com.example.agilelifemanagement.ui.theme.AgileLifeTheme

/**
 * Main container composable for the Agile Life Management app.
 * Implements Material 3 Expressive design system with animation patterns and navigation.
 * Features bottom navigation bar and shared element transitions.
 */
@Composable
fun AgileLifeApp(
    navController: NavHostController = rememberNavController()
) {
    AgileLifeTheme {
        // Bottom navigation destinations
        val mainDestinations = listOf(
            BottomNavDestination.Dashboard.route,
            BottomNavDestination.Tasks.route,
            BottomNavDestination.Sprints.route,
            BottomNavDestination.Day.route
        )
        
        // Observe current navigation state
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        
        // Determine whether to show bottom navigation
        val showBottomNav by remember(navBackStackEntry) {
            derivedStateOf {
                navBackStackEntry?.destination?.route?.let { route ->
                    // Show bottom nav for main destinations
                    mainDestinations.any { route == it }
                } ?: true
            }
        }
        
        // For snackbar messages
        val snackbarHostState = remember { SnackbarHostState() }
        val coroutineScope = rememberCoroutineScope()
        
        Scaffold(
            bottomBar = {
                // Only show bottom navigation when on a main destination
                if (showBottomNav) {
                    AgileLifeBottomNavigation(
                        navController = navController
                    )
                }
            },
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState)
            }
        ) { innerPadding ->
            // Main content area with NavHost
            AgileLifeNavHost(
                navController = navController,
                paddingValues = innerPadding
            )
        }
    }
}
