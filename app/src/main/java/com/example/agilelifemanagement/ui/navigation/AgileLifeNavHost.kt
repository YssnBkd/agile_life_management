package com.example.agilelifemanagement.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.agilelifemanagement.ui.screens.dashboard.DashboardScreen
import com.example.agilelifemanagement.ui.screens.day.DayDetailScreenSimple
import com.example.agilelifemanagement.ui.screens.day.DayPlannerScreenSimple
// Remove incorrect import
import com.example.agilelifemanagement.ui.screens.sprint.SprintDetailScreen
import com.example.agilelifemanagement.ui.screens.sprint.SprintEditorScreen
import com.example.agilelifemanagement.ui.screens.sprint.SprintListScreen
import com.example.agilelifemanagement.ui.screens.task.TaskBacklogScreen
import com.example.agilelifemanagement.ui.screens.task.TaskDetailScreen
import com.example.agilelifemanagement.ui.screens.task.TaskEditorScreen
import com.example.agilelifemanagement.ui.screens.wellness.WellnessScreen

/**
 * Duration for the animated transitions between destinations
 */
private const val NAVIGATION_ANIMATION_DURATION = 300

/**
 * Navigation host for the AgileLife app
 * Implements Material 3 Expressive motion patterns with shared element transitions
 */
@Composable
fun AgileLifeNavHost(
    navController: NavHostController,
    paddingValues: PaddingValues,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = BottomNavDestination.Dashboard.route,
        modifier = modifier.padding(paddingValues),
        enterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(NAVIGATION_ANIMATION_DURATION)
            ) + fadeIn(animationSpec = tween(NAVIGATION_ANIMATION_DURATION))
        },
        exitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(NAVIGATION_ANIMATION_DURATION)
            ) + fadeOut(animationSpec = tween(NAVIGATION_ANIMATION_DURATION))
        },
        popEnterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(NAVIGATION_ANIMATION_DURATION)
            ) + fadeIn(animationSpec = tween(NAVIGATION_ANIMATION_DURATION))
        },
        popExitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(NAVIGATION_ANIMATION_DURATION)
            ) + fadeOut(animationSpec = tween(NAVIGATION_ANIMATION_DURATION))
        }
    ) {
        // Main bottom navigation destinations
        addBottomNavigationDestinations(navController)
        
        // Task related screens
        taskNavigation(navController)
        
        // Sprint related screens
        sprintNavigation(navController)
        
        // Day and wellness screens
        dayNavigation(navController)
    }
}

/**
 * Add the main bottom navigation destinations to the NavGraphBuilder
 */
private fun NavGraphBuilder.addBottomNavigationDestinations(navController: NavController) {
    composable(BottomNavDestination.Dashboard.route) {
        DashboardScreen(
            onTaskClick = { taskId -> navController.navigate("tasks/$taskId") },
            onSprintClick = { sprintId -> navController.navigate("sprints/$sprintId") },
            onDayClick = { dayId -> navController.navigate("day/timeline?date=$dayId") },
            onCreateTask = { navController.navigate("tasks/create") },
            onCheckIn = { navController.navigate("day/timeline") },
            onCreateNote = { /* Navigate to note creation */ },
            onSearchClick = { /* Open search */ },
            onNotificationsClick = { /* Open notifications */ },
            onProfileClick = { /* Open profile */ }
        )
    }
    
    composable(BottomNavDestination.Tasks.route) {
        TaskBacklogScreen(
            onBackClick = { navController.navigateUp() },
            onTaskClick = { taskId -> navController.navigate("tasks/$taskId") },
            onCreateTaskClick = { navController.navigate("tasks/create") },
            onSprintClick = { sprintId -> navController.navigate("sprints/$sprintId") },
            onCreateSprintClick = { navController.navigate("sprints/create") },
            onSearchClick = { /* TODO: Implement search */ },
            onProfileClick = { /* TODO: Implement profile */ }
        )
    }
    
    composable(BottomNavDestination.Sprints.route) {
        // Using a simplified version that takes a NavController directly
        com.example.agilelifemanagement.ui.screens.sprint.SprintListScreen(
            navController = navController
        )
    }
    
    composable(BottomNavDestination.Day.route) {
        // Using our simplified DayDetailScreenSimple
        DayDetailScreenSimple(
            navController = navController,
            selectedDate = java.time.LocalDate.now()
        )
    }
}

/**
 * Task related navigation destinations
 */
private fun NavGraphBuilder.taskNavigation(navController: NavController) {
    composable(
        route = "tasks/{taskId}",
        arguments = listOf(navArgument("taskId") { type = NavType.StringType })
    ) { backStackEntry ->
        val taskId = backStackEntry.arguments?.getString("taskId") ?: ""
        // Use our simplified TaskDetailScreen that takes NavController and taskId
        com.example.agilelifemanagement.ui.screens.task.TaskDetailScreen(
            navController = navController,
            taskId = taskId
        )
    }
    
    composable("tasks/create") {
        // Use our simplified TaskEditorScreen that takes NavController
        com.example.agilelifemanagement.ui.screens.task.TaskEditorScreen(
            navController = navController,
            taskId = null
        )
    }
    
    composable(
        route = "tasks/edit/{taskId}",
        arguments = listOf(navArgument("taskId") { type = NavType.StringType })
    ) { backStackEntry ->
        val taskId = backStackEntry.arguments?.getString("taskId") ?: ""
        // Use our simplified TaskEditorScreen that takes NavController
        com.example.agilelifemanagement.ui.screens.task.TaskEditorScreen(
            navController = navController,
            taskId = taskId
        )
    }
}

/**
 * Sprint related navigation destinations
 */
private fun NavGraphBuilder.sprintNavigation(navController: NavController) {
    composable(
        route = "sprints/{sprintId}",
        arguments = listOf(navArgument("sprintId") { type = NavType.StringType })
    ) { backStackEntry ->
        val sprintId = backStackEntry.arguments?.getString("sprintId") ?: ""
        SprintDetailScreen(
            sprintId = sprintId,
            onBackClick = { navController.navigateUp() },
            onEditSprintClick = { navController.navigate("sprints/edit/$sprintId") },
            onSprintReviewClick = { navController.navigate("sprints/$sprintId/review") },
            onTaskClick = { taskId -> navController.navigate("tasks/$taskId") },
            onDayClick = { date -> navController.navigate("day/timeline?date=$date") },
            onCreateTaskClick = { navController.navigate("tasks/create") }
        )
    }
    
    composable("sprints/create") {
        SprintEditorScreen(
            sprintId = null,
            onBackClick = { navController.navigateUp() },
            onSaveClick = { /* Sprint creation handled in ViewModel */ 
                navController.navigateUp()
            }
        )
    }
    
    composable(
        route = "sprints/edit/{sprintId}",
        arguments = listOf(navArgument("sprintId") { type = NavType.StringType })
    ) { backStackEntry ->
        val sprintId = backStackEntry.arguments?.getString("sprintId") ?: ""
        SprintEditorScreen(
            sprintId = sprintId,
            onBackClick = { navController.navigateUp() },
            onSaveClick = { /* Sprint update handled in ViewModel */ 
                navController.navigateUp()
            }
        )
    }
}

/**
 * Day and wellness related navigation destinations
 */
private fun NavGraphBuilder.dayNavigation(navController: NavController) {
    // Day timeline screen with optional date parameter
    composable(
        route = "day/timeline?date={date}",
        arguments = listOf(
            navArgument("date") {
                type = NavType.StringType
                nullable = true
                defaultValue = null
            }
        )
    ) { backStackEntry ->
        val dateString = backStackEntry.arguments?.getString("date")
        val selectedDate = dateString?.let {
            java.time.LocalDate.parse(it)
        } ?: java.time.LocalDate.now()
        
        // Using our simplified implementation
        DayDetailScreenSimple(
            navController = navController,
            selectedDate = selectedDate
        )
    }
    
    // Day activity detail - to be implemented
    composable(
        route = "day/activities/{activityId}",
        arguments = listOf(
            navArgument("activityId") {
                type = NavType.StringType
            }
        )
    ) { backStackEntry ->
        val activityId = backStackEntry.arguments?.getString("activityId") ?: ""
        // Placeholder for activity detail screen
        // Will be implemented in a future update
    }
    
    // Day planner screen
    composable(
        route = "day/planner?date={date}",
        arguments = listOf(
            navArgument("date") {
                type = NavType.StringType
                nullable = true
                defaultValue = null
            }
        )
    ) { backStackEntry ->
        val dateString = backStackEntry.arguments?.getString("date")
        val selectedDate = dateString?.let {
            java.time.LocalDate.parse(it)
        } ?: java.time.LocalDate.now()
        
        DayPlannerScreenSimple(
            navController = navController,
            selectedDate = selectedDate
        )
    }
    
    // Wellness screen
    composable("wellness") {
        WellnessScreen(navController = navController)
    }
}
