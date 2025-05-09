package com.example.agilelifemanagement

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.agilelifemanagement.ui.navigation.AppNavHost
import com.example.agilelifemanagement.ui.navigation.BottomNavBar
import com.example.agilelifemanagement.ui.navigation.NavRoutes
import com.example.agilelifemanagement.ui.theme.AgileLifeManagementTheme

@dagger.hilt.android.AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AgileLifeManagementTheme {
                AgileLifeManagementApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgileLifeManagementApp() {
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }
    
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            BottomNavBar(navController = navController)
        },
        floatingActionButton = {
            // Show FAB only on main screens
            val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
            if (currentRoute in listOf(
                    NavRoutes.CALENDAR,
                    NavRoutes.SPRINTS,
                    NavRoutes.GOALS,
                    NavRoutes.TASKS
                )
            ) {
                FloatingActionButton(
                    onClick = {
                        // Action based on current screen
                        when (currentRoute) {
                            NavRoutes.CALENDAR -> { /* Add calendar event */ }
                            NavRoutes.SPRINTS -> { /* Create new sprint */ }
                            NavRoutes.GOALS -> { /* Create new goal */ }
                            NavRoutes.TASKS -> { /* Create new task */ }
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add"
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AppNavHost(
                navController = navController,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}