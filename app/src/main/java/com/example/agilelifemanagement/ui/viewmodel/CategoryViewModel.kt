package com.example.agilelifemanagement.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agilelifemanagement.domain.model.GoalCategory
import com.example.agilelifemanagement.domain.usecase.day.category.GetActivityCategoriesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * UI State for Category screens, following the Unidirectional Data Flow pattern.
 */
data class CategoryUiState(
    val categories: List<GoalCategory> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val errorMessage: String? = null
)

/**
 * ViewModel for Category-related screens, implementing Unidirectional Data Flow.
 * It handles loading categories for activities.
 */
@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val getActivityCategoriesUseCase: GetActivityCategoriesUseCase
) : ViewModel() {

    // Private mutable state flow
    private val _uiState = MutableStateFlow(CategoryUiState())
    
    // Public immutable state flow for UI consumption
    val uiState: StateFlow<CategoryUiState> = _uiState.asStateFlow()

    init {
        loadCategories()
    }

    /**
     * Loads all activity categories
     */
    fun loadCategories() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            
            getActivityCategoriesUseCase()
                .catch { e ->
                    Timber.e(e, "Error loading categories")
                    _uiState.update { 
                        it.copy(
                            isLoading = false, 
                            errorMessage = e.message ?: "Unknown error occurred while loading categories"
                        )
                    }
                }
                .collectLatest { categories ->
                    _uiState.update { 
                        it.copy(
                            categories = categories,
                            isLoading = false,
                            isRefreshing = false
                        )
                    }
                }
        }
    }
    
    /**
     * Refresh the categories data
     */
    fun refresh() {
        _uiState.update { it.copy(isRefreshing = true) }
        loadCategories()
    }
    
    /**
     * Clears the error message
     */
    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
    
    /**
     * Gets a category by its ID
     */
    fun getCategoryById(categoryId: String): GoalCategory? {
        return _uiState.value.categories.find { it.id == categoryId }
    }
    
    /**
     * Gets a category by name
     */
    fun getCategoryByName(name: String): GoalCategory? {
        return _uiState.value.categories.find { 
            it.name.equals(name, ignoreCase = true) 
        }
    }
}
