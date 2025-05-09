package com.example.agilelifemanagement.ui.screens.test

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.agilelifemanagement.data.remote.AuthState

/**
 * Test screen for verifying Supabase integration.
 * This screen allows testing authentication and data synchronization.
 */
@Composable
fun SupabaseTestScreen(
    viewModel: SupabaseTestViewModel = hiltViewModel()
) {
    val authState by viewModel.authState.collectAsState()
    val syncStatus by viewModel.syncStatus.collectAsState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Supabase Integration Test",
            style = MaterialTheme.typography.headlineMedium
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        when (authState) {
            is AuthState.Authenticated -> {
                Text("Logged in as: ${(authState as AuthState.Authenticated).userId}")
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Button(onClick = { viewModel.signOut() }) {
                    Text("Sign Out")
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Text(
                    text = "Data Synchronization",
                    style = MaterialTheme.typography.titleLarge
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Row {
                    Button(onClick = { viewModel.createTestSprint() }) {
                        Text("Create Test Sprint")
                    }
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Button(onClick = { viewModel.createTestGoal() }) {
                        Text("Create Test Goal")
                    }
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Button(onClick = { viewModel.createTestTask() }) {
                        Text("Create Test Task")
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Button(onClick = { viewModel.syncPendingChanges() }) {
                    Text("Force Sync")
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Sync Status: $syncStatus",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            is AuthState.NotAuthenticated -> {
                var email by remember { mutableStateOf("") }
                var password by remember { mutableStateOf("") }
                
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") }
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    visualTransformation = PasswordVisualTransformation()
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Row {
                    Button(onClick = { viewModel.signIn(email, password) }) {
                        Text("Sign In")
                    }
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Button(onClick = { viewModel.signUp(email, password) }) {
                        Text("Sign Up")
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Button(onClick = { viewModel.resetPassword(email) }) {
                    Text("Reset Password")
                }
            }
            is AuthState.Loading -> {
                CircularProgressIndicator()
            }
            is AuthState.Error -> {
                Text(
                    text = "Error: ${(authState as AuthState.Error).message}",
                    color = MaterialTheme.colorScheme.error
                )
                
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
