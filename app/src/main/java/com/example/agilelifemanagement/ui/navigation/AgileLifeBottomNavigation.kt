package com.example.agilelifemanagement.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CalendarToday
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.RunCircle
import androidx.compose.material.icons.rounded.Task
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState

/**
 * Bottom navigation destinations for the AgileLife app
 */
sealed class BottomNavDestination(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Dashboard : BottomNavDestination("dashboard", "Home", Icons.Rounded.Home)
    object Tasks : BottomNavDestination("tasks", "Tasks", Icons.Rounded.Task)
    object Sprints : BottomNavDestination("sprints", "Sprints", Icons.Rounded.RunCircle)
    object Day : BottomNavDestination("day", "Day", Icons.Rounded.CalendarToday)
}

/**
 * All possible bottom navigation destinations
 */
val bottomNavDestinations = listOf(
    BottomNavDestination.Dashboard,
    BottomNavDestination.Tasks, 
    BottomNavDestination.Sprints,
    BottomNavDestination.Day
)

/**
 * Bottom navigation bar for the AgileLife app
 * Features Material 3 Expressive styling with elevated bar and custom transitions
 */
@Composable
fun AgileLifeBottomNavigation(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    NavigationBar(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        tonalElevation = 8.dp,
        contentColor = MaterialTheme.colorScheme.onSurface
    ) {
        bottomNavDestinations.forEach { destination ->
            val selected = currentDestination?.hierarchy?.any { 
                it.route == destination.route 
            } == true
            
            // Custom colors for selected/unselected states for more expressive UI
            val iconColor = if (selected) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            }
            
            val labelColor = if (selected) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            }

            NavigationBarItem(
                icon = { 
                    Icon(
                        imageVector = destination.icon,
                        contentDescription = destination.title,
                        tint = iconColor
                    )
                },
                label = { 
                    Text(
                        text = destination.title,
                        style = MaterialTheme.typography.labelSmall,
                        color = labelColor,
                        maxLines = 1
                    )
                },
                selected = selected,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f),
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                onClick = {
                    if (!selected) {
                        navController.navigate(destination.route) {
                            // Pop up to the start destination of the graph to
                            // avoid building up a large stack of destinations
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            // Avoid multiple copies of the same destination
                            launchSingleTop = true
                            // Restore state when reselecting a previously selected item
                            restoreState = true
                        }
                    }
                }
            )
        }
    }
}
