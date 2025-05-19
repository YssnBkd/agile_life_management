package com.example.agilelifemanagement.ui.viewmodel

import com.example.agilelifemanagement.domain.model.Sprint

/**
 * UI State for Sprint-related screens, following the single-state pattern.
 * This data class captures all possible states of the Sprint screens.
 */
data class SprintUiState(
    // Sprint collection
    val sprints: List<Sprint> = emptyList(),
    val filteredSprints: List<Sprint> = emptyList(),
    
    // Single sprint detail
    val selectedSprint: Sprint? = null,
    
    // Loading states
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    
    // Error state
    val errorMessage: String? = null,
    
    // Sprint operation states
    val isSprintSaved: Boolean = false,
    val isSprintDeleted: Boolean = false,
    val operationSuccess: Boolean = false
)
