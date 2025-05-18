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
import com.example.agilelifemanagement.ui.navigation.AgileLifeNavHost
import com.example.agilelifemanagement.ui.navigation.AgileLifeBottomNavigation
import com.example.agilelifemanagement.ui.navigation.NavDestination
import com.example.agilelifemanagement.ui.theme.AgileLifeTheme

@dagger.hilt.android.AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AgileLifeTheme {
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
            AgileLifeBottomNavigation(navController = navController)
        },
        floatingActionButton = {
            // Show FAB only on main screens
            val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
            if (currentRoute in listOf(
                    NavDestination.Day.route,
                    NavDestination.Sprint.route,
                    NavDestination.Dashboard.route,
                    NavDestination.Task.route
                )
            ) {
                FloatingActionButton(
                    onClick = {
                        // Action based on current screen
                        when (currentRoute) {
                            NavDestination.Day.route -> { /* Add calendar event */ }
                            NavDestination.Sprint.route -> { /* Create new sprint */ }
                            NavDestination.Dashboard.route -> { /* Create new goal */ }
                            NavDestination.Task.route -> { /* Create new task */ }
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
            AgileLifeNavHost(
                navController = navController,
                paddingValues = innerPadding,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}