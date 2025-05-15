package com.example.agilelifemanagement.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.agilelifemanagement.ui.navigation.AgileLifeNavGraph
import com.example.agilelifemanagement.ui.navigation.NavDestination

/**
 * MainScreen serves as the container for the entire application.
 * It includes the bottom navigation and hosts the NavHost for all screens.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    
    // Determine whether to show the bottom navigation bar
    val showBottomNav by remember(navBackStackEntry) {
        derivedStateOf {
            when (navBackStackEntry?.destination?.route) {
                // Add routes where bottom nav should be hidden
                // e.g., full-screen detail pages or editors
                else -> true
            }
        }
    }
    
    // Define all main destinations for bottom navigation
    val bottomNavDestinations = listOf(
        NavDestination.Dashboard,
        NavDestination.Sprint,
        NavDestination.Day,
        NavDestination.Task
    )
    
    // Scaffold provides the basic Material Design layout structure
    Scaffold(
        modifier = modifier,
        bottomBar = {
            AnimatedVisibility(
                visible = showBottomNav,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                NavigationBar {
                    val currentDestination = navBackStackEntry?.destination
                    
                    bottomNavDestinations.forEach { destination ->
                        val selected = currentDestination?.hierarchy?.any { 
                            it.route == destination.route 
                        } == true
                        
                        NavigationBarItem(
                            icon = {
                                if (destination.hasUnreadContent) {
                                    BadgedBox(
                                        badge = {
                                            Badge(
                                                containerColor = MaterialTheme.colorScheme.error
                                            )
                                        }
                                    ) {
                                        Icon(
                                            imageVector = destination.icon,
                                            contentDescription = null
                                        )
                                    }
                                } else {
                                    Icon(
                                        imageVector = destination.icon,
                                        contentDescription = null
                                    )
                                }
                            },
                            label = {
                                Text(
                                    text = destination.title,
                                    fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                                    style = MaterialTheme.typography.labelMedium
                                )
                            },
                            selected = selected,
                            onClick = {
                                navigateToTopLevelDestination(
                                    navController = navController,
                                    destination = destination
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        // Navigation host containing all app screens
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .consumeWindowInsets(WindowInsets(0, 0, 0, 0))
        ) {
            AgileLifeNavGraph(
                navController = navController,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

/**
 * Helper function to navigate to a top-level destination in the app.
 * It uses a specific navigation pattern that preserves state when navigating
 * between top-level destinations.
 */
private fun navigateToTopLevelDestination(
    navController: NavController,
    destination: NavDestination
) {
    navController.navigate(destination.route) {
        // Pop up to the start destination of the graph to avoid building up
        // a large stack of destinations
        popUpTo(navController.graph.findStartDestination().id) {
            saveState = true
        }
        // Avoid multiple copies of the same destination when reselecting the same item
        launchSingleTop = true
        // Restore state when reselecting a previously selected item
        restoreState = true
    }
}
