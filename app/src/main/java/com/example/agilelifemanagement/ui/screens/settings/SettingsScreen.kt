package com.example.agilelifemanagement.ui.screens.settings

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.material3.Button
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.agilelifemanagement.auth.presentation.AuthViewModel
import com.example.agilelifemanagement.auth.presentation.AuthUiState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SettingsScreen(
    viewModel: AuthViewModel = hiltViewModel(),
    onLogout: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Settings Screen",
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.padding(24.dp))
            Button(onClick = { viewModel.logout() }) {
                Text("Logout")
            }
            if (uiState is AuthUiState.LoggedOut) {
                // Trigger navigation to login
                LaunchedEffect(Unit) { onLogout() }
            }
        }
    }
}
