package com.example.agilelifemanagement.ui.screens.day.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.agilelifemanagement.domain.model.DayTemplate
import com.example.agilelifemanagement.ui.components.day.TemplateCard
import com.example.agilelifemanagement.ui.screens.day.components.EmptyTemplatesView

/**
 * Displays a list of day templates with appropriate loading and empty states.
 *
 * @param templates List of day templates to display
 * @param isLoading Whether templates are currently being loaded
 * @param onTemplateClick Callback when a template is clicked
 * @param onEditClick Callback when edit button on a template is clicked
 * @param onDeleteClick Callback when delete button on a template is clicked
 * @param onApplyClick Callback when apply button on a template is clicked
 * @param onCreateClick Callback when create new template button is clicked
 * @param modifier Optional modifier
 */
@Composable
fun TemplateList(
    templates: List<DayTemplate>,
    isLoading: Boolean,
    onTemplateClick: (DayTemplate) -> Unit,
    onEditClick: (DayTemplate) -> Unit,
    onDeleteClick: (DayTemplate) -> Unit,
    onApplyClick: (DayTemplate) -> Unit,
    onCreateClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when {
            // Show loading indicator
            isLoading -> {
                CircularProgressIndicator()
            }
            
            // Show empty state
            templates.isEmpty() -> {
                EmptyTemplatesView(
                    onCreateClick = onCreateClick,
                    modifier = Modifier.fillMaxSize()
                )
            }
            
            // Show template list
            else -> {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(
                        items = templates,
                        key = { it.id }
                    ) { template ->
                        TemplateCard(
                            template = template,
                            onApplyClick = { onApplyClick(template) },
                            onEditClick = { onEditClick(template) },
                            onDeleteClick = { onDeleteClick(template) },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }
    }
}
