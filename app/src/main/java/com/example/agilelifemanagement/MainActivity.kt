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
import com.example.agilelifemanagement.ui.navigation.NavDestinations
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
                    NavDestinations.Day.route,
                    NavDestinations.Sprints.route,
                    NavDestinations.Goals.route,
                    NavDestinations.Tasks.route
                )
            ) {
                FloatingActionButton(
                    onClick = {
                        // Action based on current screen
                        when (currentRoute) {
                            NavDestinations.Day.route -> { /* Add calendar event */ }
                            NavDestinations.Sprints.route -> { /* Create new sprint */ }
                            NavDestinations.Goals.route -> { /* Create new goal */ }
                            NavDestinations.Tasks.route -> { /* Create new task */ }
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