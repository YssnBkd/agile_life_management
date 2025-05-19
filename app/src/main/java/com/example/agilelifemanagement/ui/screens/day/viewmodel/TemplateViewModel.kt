package com.example.agilelifemanagement.ui.screens.day.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agilelifemanagement.domain.model.GoalCategory
import com.example.agilelifemanagement.domain.model.DayTemplate
import com.example.agilelifemanagement.domain.model.Result
import com.example.agilelifemanagement.domain.model.TemplateActivity
import com.example.agilelifemanagement.domain.usecase.day.category.GetActivityCategoriesUseCase
import com.example.agilelifemanagement.domain.usecase.day.template.CreateDayTemplateUseCase
import com.example.agilelifemanagement.domain.usecase.day.template.DeleteDayTemplateUseCase
import com.example.agilelifemanagement.domain.usecase.day.template.GetDayTemplatesUseCase
import com.example.agilelifemanagement.domain.usecase.day.template.GetTemplateByIdUseCase
import com.example.agilelifemanagement.domain.usecase.day.template.UpdateDayTemplateUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject
import java.util.UUID

/**
 * ViewModel for DayTemplateScreen.
 * Manages day templates and their activities.
 * 
 * Following Clean Architecture principles, this ViewModel interacts with the domain layer
 * through use cases rather than directly with repositories.
 */
@HiltViewModel
class TemplateViewModel @Inject constructor(
    private val getDayTemplatesUseCase: GetDayTemplatesUseCase,
    private val getTemplateByIdUseCase: GetTemplateByIdUseCase,
    private val createDayTemplateUseCase: CreateDayTemplateUseCase,
    private val updateDayTemplateUseCase: UpdateDayTemplateUseCase,
    private val deleteDayTemplateUseCase: DeleteDayTemplateUseCase,
    private val getActivityCategoriesUseCase: GetActivityCategoriesUseCase
) : ViewModel() {

    // UI state for template management
    data class TemplateUiState(
        val isLoading: Boolean = false,
        val templates: List<DayTemplate> = emptyList(),
        val categories: List<GoalCategory> = emptyList(),
        val selectedTemplate: DayTemplate? = null,
        val errorMessage: String? = null
    )

    private val _uiState = MutableStateFlow(TemplateUiState())
    val uiState: StateFlow<TemplateUiState> = _uiState.asStateFlow()

    // Initialize with templates
    init {
        loadTemplates()
        loadCategories()
    }

    // Load all templates using GetDayTemplatesUseCase
    private fun loadTemplates() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null
            )
            
            getDayTemplatesUseCase()
                .catch { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Failed to load templates: ${e.message}"
                    )
                }
                .collectLatest { templates ->
                    _uiState.value = _uiState.value.copy(
                        templates = templates,
                        isLoading = false
                    )
                }
        }
    }

    // Load activity categories using GetActivityCategoriesUseCase
    private fun loadCategories() {
        viewModelScope.launch {
            getActivityCategoriesUseCase()
                .catch { e ->
                    _uiState.value = _uiState.value.copy(
                        errorMessage = "Failed to load categories: ${e.message}"
                    )
                }
                .collectLatest { categories ->
                    _uiState.value = _uiState.value.copy(
                        categories = categories
                    )
                }
        }
    }

    // Select a template for viewing or editing using GetTemplateByIdUseCase
    fun selectTemplate(templateId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true
            )
            
            getTemplateByIdUseCase(templateId)
                .catch { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Failed to load template: ${e.message}"
                    )
                }
                .collectLatest { template ->
                    _uiState.value = _uiState.value.copy(
                        selectedTemplate = template,
                        isLoading = false
                    )
                }
        }
    }

    // Clear template selection
    fun clearTemplateSelection() {
        _uiState.value = _uiState.value.copy(
            selectedTemplate = null
        )
    }

    // Create a new template using CreateDayTemplateUseCase
    fun createTemplate(name: String, description: String, activities: List<TemplateActivity>) {
        viewModelScope.launch {
            val newTemplate = DayTemplate(
                id = UUID.randomUUID().toString(),
                name = name,
                description = description,
                activities = activities,
                createdDate = LocalDate.now()
            )
            
            when (val result = createDayTemplateUseCase(newTemplate)) {
                is Result.Success<*> -> {
                    loadTemplates() // Refresh templates list
                }
                is Result.Error -> {
                    _uiState.value = _uiState.value.copy(
                        errorMessage = "Failed to create template: ${result.message}"
                    )
                }
            }
        }
    }
    
    // Update an existing template using UpdateDayTemplateUseCase
    fun updateTemplate(template: DayTemplate) {
        viewModelScope.launch {
            when (val result = updateDayTemplateUseCase(template)) {
                is Result.Success<*> -> {
                    loadTemplates() // Refresh templates list
                }
                is Result.Error -> {
                    _uiState.value = _uiState.value.copy(
                        errorMessage = "Failed to update template: ${result.message}"
                    )
                }
            }
        }
    }



    // Delete a template using DeleteDayTemplateUseCase
    fun deleteTemplate(templateId: String) {
        viewModelScope.launch {
            when (val result = deleteDayTemplateUseCase(templateId)) {
                is Result.Success<*> -> {
                    loadTemplates() // Refresh templates list
                    
                    // Clear selection if the deleted template was selected
                    if (_uiState.value.selectedTemplate?.id == templateId) {
                        clearTemplateSelection()
                    }
                }
                is Result.Error -> {
                    _uiState.value = _uiState.value.copy(
                        errorMessage = "Failed to delete template: ${result.message}"
                    )
                }
            }
        }
    }

    // Select a template for editing
    fun selectTemplateForEdit(template: DayTemplate) {
        _uiState.value = _uiState.value.copy(
            selectedTemplate = template
        )
    }
    
    /**
     * Select a template for editing by ID.
     * This helps with Clean Architecture by allowing the UI layer to work with IDs rather than domain objects.
     * 
     * @param templateId The ID of the template to select for editing
     */
    fun selectTemplateForEdit(templateId: String) {
        viewModelScope.launch {
            // Find template in the current list
            val template = _uiState.value.templates.find { it.id == templateId }
            if (template != null) {
                _uiState.value = _uiState.value.copy(
                    selectedTemplate = template
                )
            } else {
                // If not found in current list, try to load it from use case
                getTemplateByIdUseCase(templateId)
                    .catch { e ->
                        _uiState.value = _uiState.value.copy(
                            errorMessage = "Failed to load template: ${e.message}"
                        )
                    }
                    .collectLatest { templateResult ->
                        _uiState.value = _uiState.value.copy(
                            selectedTemplate = templateResult
                        )
                    }
            }
        }
    }

    // Apply a template to a specific date
    // Note: This should ideally use a dedicated ApplyTemplateUseCase which we'll need to create
    fun applyTemplate(templateId: String, date: LocalDate) {
        viewModelScope.launch {
            // TODO: Replace this with ApplyTemplateUseCase once implemented
            // For now, we'll keep a comment as a reminder that this needs to be updated
            _uiState.value = _uiState.value.copy(
                errorMessage = "Template application functionality needs to be implemented with proper use case"
            )
        }
    }

    // Add a new activity category
    // Note: This should ideally use a dedicated CreateCategoryUseCase which we'll need to create
    fun addCategory(name: String, colorInt: Int) {
        viewModelScope.launch {
            // TODO: Replace this with CreateCategoryUseCase once implemented
            // For now, we'll keep a comment as a reminder that this needs to be updated
            _uiState.value = _uiState.value.copy(
                errorMessage = "Category creation functionality needs to be implemented with proper use case"
            )
        }
    }
}
