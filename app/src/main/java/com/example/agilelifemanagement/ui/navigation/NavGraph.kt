package com.example.agilelifemanagement.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.agilelifemanagement.ui.screens.dashboard.DashboardScreen
import com.example.agilelifemanagement.ui.screens.day.DayTemplateScreen
import com.example.agilelifemanagement.ui.screens.day.DayTimelineScreen
import com.example.agilelifemanagement.ui.screens.day.WeekViewScreen
import com.example.agilelifemanagement.ui.screens.sprint.SprintDetailScreen
import com.example.agilelifemanagement.ui.screens.sprint.SprintEditorScreen
import com.example.agilelifemanagement.ui.screens.sprint.SprintListScreen
import com.example.agilelifemanagement.ui.screens.sprint.SprintReviewScreen
import com.example.agilelifemanagement.ui.screens.task.TaskBacklogScreen
import com.example.agilelifemanagement.ui.screens.task.TaskDetailScreen
import com.example.agilelifemanagement.ui.screens.task.TaskEditorScreen
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
            DashboardScreen(
                onTaskClick = { taskId -> navController.navigate(NavRoutes.taskDetail(taskId)) },
                onSprintClick = { sprintId -> navController.navigate(NavRoutes.sprintDetail(sprintId)) },
                onGoalClick = { goalId -> /* Goal navigation will be implemented later */ },
                onDayActivityClick = { dateStr -> navController.navigate(NavRoutes.dayTimeline(dateStr)) },
                onWellnessClick = { navController.navigate(NavRoutes.DAY_MORNING_CHECKIN) },
                onAllTasksClick = { navController.navigate(NavRoutes.TASK_BACKLOG) },
                onAllSprintsClick = { navController.navigate(NavRoutes.SPRINT_LIST) },
                onAllGoalsClick = { /* Goals feature will be implemented later */ }
            )
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
            SprintListScreen(
                onSprintClick = { sprintId -> 
                    navController.navigate("${NavRoutes.SPRINT_DETAIL}/$sprintId") 
                },
                onCreateSprintClick = { 
                    navController.navigate(NavRoutes.SPRINT_EDITOR) 
                },
                onSearchClick = { /* TODO: Implement search */ },
                onProfileClick = { /* TODO: Implement profile navigation */ }
            )
        }
        
        composable(
            route = NavRoutes.SPRINT_DETAIL,
            arguments = listOf(navArgument("sprintId") { type = NavType.StringType })
        ) { backStackEntry ->
            val sprintId = backStackEntry.arguments?.getString("sprintId") ?: ""
            SprintDetailScreen(
                navController = navController,
                sprintId = sprintId
            )
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
            SprintEditorScreen(
                navController = navController,
                sprintId = sprintId
            )
        }
        
        composable(
            route = NavRoutes.SPRINT_REVIEW,
            arguments = listOf(navArgument("sprintId") { type = NavType.StringType })
        ) { backStackEntry ->
            val sprintId = backStackEntry.arguments?.getString("sprintId") ?: ""
            SprintReviewScreen(
                sprintId = sprintId,
                onBackClick = { navController.popBackStack() },
                onFinishReviewClick = { navController.popBackStack(NavRoutes.SPRINT_LIST, false) },
                onTaskClick = { taskId -> navController.navigate("${NavRoutes.TASK_DETAIL}/$taskId") },
                onPlanNextSprintClick = { navController.navigate(NavRoutes.SPRINT_EDITOR) }
            )
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
            val selectedDate = if (date != null) LocalDate.parse(date) else LocalDate.now()
            DayTimelineScreen(
                navController = navController,
                selectedDate = selectedDate,
                onAddActivity = {
                    navController.navigate(NavRoutes.DAY_ACTIVITY_EDITOR)
                }
            )
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
            // TODO: MorningCheckInScreen needs to be implemented
            // For now, navigate back and show a temporary message
            LaunchedEffect(Unit) {
                navController.popBackStack()
            }
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
            // TODO: JournalingScreen needs to be implemented
            // For now, navigate back and show a temporary message
            LaunchedEffect(Unit) {
                navController.popBackStack()
            }
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
            // TODO: EveningCheckInScreen needs to be implemented
            // For now, navigate back and show a temporary message
            LaunchedEffect(Unit) {
                navController.popBackStack()
            }
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
        
        // Day Activity Editor Screen
        composable(
            route = NavRoutes.DAY_ACTIVITY_EDITOR,
            arguments = listOf(navArgument("activityId") { 
                type = NavType.StringType
                nullable = true
                defaultValue = null
            })
        ) { backStackEntry ->
            val activityId = backStackEntry.arguments?.getString("activityId")
            // TODO: Implement DayActivityEditorScreen when available
            // For now, navigate back to timeline
            LaunchedEffect(Unit) {
                navController.popBackStack()
            }
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
            TaskBacklogScreen(navController = navController)
        }
        
        composable(
            route = NavRoutes.TASK_DETAIL,
            arguments = listOf(navArgument("taskId") { type = NavType.StringType })
        ) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getString("taskId") ?: ""
            TaskDetailScreen(
                navController = navController,
                taskId = taskId
            )
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
            TaskEditorScreen(
                navController = navController,
                taskId = taskId
            )
        }
    }
}
