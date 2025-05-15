package com.example.agilelifemanagement.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import com.example.agilelifemanagement.ui.screens.day.DayTemplateScreen
import com.example.agilelifemanagement.ui.screens.day.WeekViewScreen
import java.time.LocalDate

/**
 * Main navigation graph for the app
 */
@Composable
fun AgileLifeNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    startDestination: String = NavDestination.Dashboard.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        // Dashboard navigation graph
        dashboardGraph(navController)
        
        // Sprint navigation graph
        sprintGraph(navController)
        
        // Day navigation graph
        dayGraph(navController)
        
        // Task navigation graph
        taskGraph(navController)
    }
}

/**
 * Dashboard navigation sub-graph
 */
private fun NavGraphBuilder.dashboardGraph(navController: NavHostController) {
    navigation(
        startDestination = NavRoutes.DASHBOARD_HOME,
        route = NavDestination.Dashboard.route
    ) {
        composable(route = NavRoutes.DASHBOARD_HOME) {
            // TODO: Implement DashboardScreen
        }
    }
}

/**
 * Sprint navigation sub-graph
 */
private fun NavGraphBuilder.sprintGraph(navController: NavHostController) {
    navigation(
        startDestination = NavRoutes.SPRINT_LIST,
        route = NavDestination.Sprint.route
    ) {
        composable(route = NavRoutes.SPRINT_LIST) {
            // TODO: Implement SprintListScreen
        }
        
        composable(
            route = NavRoutes.SPRINT_DETAIL,
            arguments = listOf(navArgument("sprintId") { type = NavType.StringType })
        ) { backStackEntry ->
            val sprintId = backStackEntry.arguments?.getString("sprintId") ?: ""
            // TODO: Implement SprintDetailScreen
        }
        
        composable(
            route = NavRoutes.SPRINT_EDITOR,
            arguments = listOf(navArgument("sprintId") { 
                type = NavType.StringType
                nullable = true
                defaultValue = null
            })
        ) { backStackEntry ->
            val sprintId = backStackEntry.arguments?.getString("sprintId")
            // TODO: Implement SprintEditorScreen
        }
        
        composable(
            route = NavRoutes.SPRINT_REVIEW,
            arguments = listOf(navArgument("sprintId") { type = NavType.StringType })
        ) { backStackEntry ->
            val sprintId = backStackEntry.arguments?.getString("sprintId") ?: ""
            // TODO: Implement SprintReviewScreen
        }
    }
}

/**
 * Day navigation sub-graph
 */
private fun NavGraphBuilder.dayGraph(navController: NavHostController) {
    navigation(
        startDestination = NavRoutes.DAY_TIMELINE,
        route = NavDestination.Day.route
    ) {
        composable(
            route = NavRoutes.DAY_TIMELINE,
            arguments = listOf(navArgument("date") { 
                type = NavType.StringType
                nullable = true
                defaultValue = null
            })
        ) { backStackEntry ->
            val date = backStackEntry.arguments?.getString("date")
            // TODO: Implement DayTimelineScreen
        }
        
        composable(
            route = NavRoutes.DAY_MORNING_CHECKIN,
            arguments = listOf(navArgument("date") { 
                type = NavType.StringType
                nullable = true
                defaultValue = null
            })
        ) { backStackEntry ->
            val date = backStackEntry.arguments?.getString("date")
            // TODO: Implement MorningCheckInScreen
        }
        
        composable(
            route = NavRoutes.DAY_JOURNALING,
            arguments = listOf(navArgument("date") { 
                type = NavType.StringType
                nullable = true
                defaultValue = null
            })
        ) { backStackEntry ->
            val date = backStackEntry.arguments?.getString("date")
            // TODO: Implement JournalingScreen
        }
        
        composable(
            route = NavRoutes.DAY_EVENING_CHECKIN,
            arguments = listOf(navArgument("date") { 
                type = NavType.StringType
                nullable = true
                defaultValue = null
            })
        ) { backStackEntry ->
            val date = backStackEntry.arguments?.getString("date")
            // TODO: Implement EveningCheckInScreen
        }
        
        // Week View Screen
        composable(
            route = NavRoutes.DAY_WEEK_VIEW,
            arguments = listOf(navArgument("date") { 
                type = NavType.StringType
                nullable = true
                defaultValue = null
            })
        ) { backStackEntry ->
            val dateStr = backStackEntry.arguments?.getString("date")
            WeekViewScreen(
                onBackClick = { navController.navigateUp() },
                onDayClick = { selectedDate ->
                    navController.navigate(NavRoutes.dayTimeline(selectedDate))
                },
                onAddActivityClick = { selectedDate ->
                    // This would navigate to an activity creation screen in the future
                    // For now, just navigate to the day timeline
                    navController.navigate(NavRoutes.dayTimeline(selectedDate))
                }
            )
        }
        
        // Day Template Screen
        composable(route = NavRoutes.DAY_TEMPLATE) {
            DayTemplateScreen(
                onBackClick = { navController.navigateUp() },
                onApplyTemplateClick = { templateId, date ->
                    // Apply the template and navigate to the day timeline
                    navController.navigate(NavRoutes.dayTimeline(date.toString()))
                }
            )
        }
    }
}

/**
 * Task navigation sub-graph
 */
private fun NavGraphBuilder.taskGraph(navController: NavHostController) {
    navigation(
        startDestination = NavRoutes.TASK_BACKLOG,
        route = NavDestination.Task.route
    ) {
        composable(route = NavRoutes.TASK_BACKLOG) {
            // TODO: Implement TaskBacklogScreen
        }
        
        composable(
            route = NavRoutes.TASK_DETAIL,
            arguments = listOf(navArgument("taskId") { type = NavType.StringType })
        ) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getString("taskId") ?: ""
            // TODO: Implement TaskDetailScreen
        }
        
        composable(
            route = NavRoutes.TASK_EDITOR,
            arguments = listOf(navArgument("taskId") { 
                type = NavType.StringType
                nullable = true
                defaultValue = null
            })
        ) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getString("taskId")
            // TODO: Implement TaskEditorScreen
        }
    }
}
