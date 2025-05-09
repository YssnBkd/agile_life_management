package com.example.agilelifemanagement.ui.screens.test

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agilelifemanagement.data.remote.AuthState
import com.example.agilelifemanagement.data.remote.SupabaseManager
import com.example.agilelifemanagement.data.remote.SyncManager
import com.example.agilelifemanagement.domain.model.Goal
import com.example.agilelifemanagement.domain.model.Sprint
import com.example.agilelifemanagement.domain.model.Task
import com.example.agilelifemanagement.domain.repository.GoalRepository
import com.example.agilelifemanagement.domain.repository.SprintRepository
import com.example.agilelifemanagement.domain.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

/**
 * ViewModel for the Supabase test screen.
 * Handles authentication and data synchronization operations.
 */
@HiltViewModel
class SupabaseTestViewModel @Inject constructor(
    private val supabaseManager: SupabaseManager,
    private val syncManager: SyncManager,
    private val sprintRepository: SprintRepository,
    private val goalRepository: GoalRepository,
    private val taskRepository: TaskRepository
) : ViewModel() {
    
    val authState = supabaseManager.authState
    
    private val _syncStatus = MutableStateFlow("Idle")
    val syncStatus: StateFlow<String> = _syncStatus.asStateFlow()
    
    /**
     * Sign in with email and password.
     */
    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            try {
                supabaseManager.signIn(email, password)
            } catch (e: Exception) {
                // Error handling is done in the SupabaseManager
            }
        }
    }
    
    /**
     * Sign up with email and password.
     */
    fun signUp(email: String, password: String) {
        viewModelScope.launch {
            try {
                supabaseManager.signUp(email, password)
            } catch (e: Exception) {
                // Error handling is done in the SupabaseManager
            }
        }
    }
    
    /**
     * Sign out the current user.
     */
    fun signOut() {
        viewModelScope.launch {
            try {
                supabaseManager.signOut()
            } catch (e: Exception) {
                // Error handling is done in the SupabaseManager
            }
        }
    }
    
    /**
     * Reset password for an email.
     */
    fun resetPassword(email: String) {
        viewModelScope.launch {
            try {
                supabaseManager.resetPassword(email)
            } catch (e: Exception) {
                // Error handling is done in the SupabaseManager
            }
        }
    }
    
    /**
     * Reset the authentication state.
     */
    // fun resetAuthState() {
//     viewModelScope.launch {
//         supabaseManager.resetAuthState()
//     }
// }
    
    /**
     * Create a test sprint for synchronization testing.
     */
    fun createTestSprint() {
        viewModelScope.launch {
            try {
                _syncStatus.value = "Creating test sprint..."
                
                val sprint = Sprint(
                    id = "",
                    name = "Test Sprint ${System.currentTimeMillis()}",
                    summary = "Created for testing Supabase integration",
                    description = listOf("Created for testing Supabase integration"),
                    startDate = LocalDate.now(),
                    endDate = LocalDate.now().plusDays(14),
                    isActive = true,
                    isCompleted = false
                )
                
                sprintRepository.insertSprint(sprint)
                
                _syncStatus.value = "Test sprint created successfully"
            } catch (e: Exception) {
                _syncStatus.value = "Error creating test sprint: ${e.message}"
            }
        }
    }
    
    /**
     * Create a test goal for synchronization testing.
     */
    fun createTestGoal() {
        viewModelScope.launch {
            try {
                _syncStatus.value = "Creating test goal..."
                
                val goal = Goal(
                    id = "",
                    title = "Test Goal ${System.currentTimeMillis()}",
                    summary = "Created for testing Supabase integration",
                    description = listOf("Created for testing Supabase integration"),
                    deadline = LocalDate.now().plusMonths(1),
                    isCompleted = false,
                    category = Goal.Category.PERSONAL
                )
                
                goalRepository.insertGoal(goal)
                
                _syncStatus.value = "Test goal created successfully"
            } catch (e: Exception) {
                _syncStatus.value = "Error creating test goal: ${e.message}"
            }
        }
    }
    
    /**
     * Create a test task for synchronization testing.
     */
    fun createTestTask() {
        viewModelScope.launch {
            try {
                _syncStatus.value = "Creating test task..."
                
                val task = Task(
                    id = "",
                    title = "Test Task ${System.currentTimeMillis()}",
                    summary = "Created for testing Supabase integration",
                    description = listOf("Created for testing Supabase integration"),
                    dueDate = LocalDate.now().plusDays(7),
                    priority = Task.Priority.MEDIUM,
                    status = Task.Status.TODO
                )
                
                taskRepository.insertTask(task)
                
                _syncStatus.value = "Test task created successfully"
            } catch (e: Exception) {
                _syncStatus.value = "Error creating test task: ${e.message}"
            }
        }
    }
    
    /**
     * Force synchronization of pending changes.
     */
    fun syncPendingChanges() {
        viewModelScope.launch {
            try {
                _syncStatus.value = "Syncing pending changes..."
                
                syncManager.syncPendingChanges()
                
                _syncStatus.value = "Sync completed successfully"
            } catch (e: Exception) {
                _syncStatus.value = "Error syncing changes: ${e.message}"
            }
        }
    }
}
