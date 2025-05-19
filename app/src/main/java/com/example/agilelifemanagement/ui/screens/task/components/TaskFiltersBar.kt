package com.example.agilelifemanagement.ui.screens.task.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.FilterList
import androidx.compose.material.icons.rounded.Sort
import androidx.compose.material3.AssistChip
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.agilelifemanagement.ui.model.TaskFilterChip
import com.example.agilelifemanagement.ui.model.TaskFilterType
import com.example.agilelifemanagement.ui.model.TaskSortCriteria

/**
 * Task filter and sort bar component.
 */
@Composable
fun TaskFiltersBar(
    filterType: TaskFilterType,
    onFilterTypeChange: (TaskFilterType) -> Unit,
    sortCriteria: TaskSortCriteria,
    onSortCriteriaChange: (TaskSortCriteria) -> Unit,
    sortAscending: Boolean,
    onSortDirectionChange: (Boolean) -> Unit,
    selectedFilters: Set<TaskFilterChip>,
    onFilterChipSelected: (TaskFilterChip, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    var showFilterMenu by remember { mutableStateOf(false) }
    var showSortMenu by remember { mutableStateOf(false) }
    
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // Filter button with popup menu
        IconButton(onClick = { showFilterMenu = true }) {
            Icon(
                imageVector = Icons.Rounded.FilterList,
                contentDescription = "Filter tasks",
                tint = MaterialTheme.colorScheme.primary
            )
            
            DropdownMenu(
                expanded = showFilterMenu,
                onDismissRequest = { showFilterMenu = false }
            ) {
                TaskFilterType.entries.forEach { type ->
                    DropdownMenuItem(
                        text = { Text(type.displayName) },
                        onClick = {
                            onFilterTypeChange(type)
                            showFilterMenu = false
                        },
                        trailingIcon = {
                            if (type == filterType) {
                                Icon(
                                    imageVector = Icons.Rounded.FilterList,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    )
                }
            }
        }
        
        // Current filter chip
        AssistChip(
            onClick = { showFilterMenu = true },
            label = { Text(filterType.displayName) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Rounded.FilterList,
                    contentDescription = null,
                    modifier = Modifier.width(16.dp)
                )
            }
        )
        
        Spacer(modifier = Modifier.weight(1f))
        
        // Sort button with popup menu
        IconButton(onClick = { showSortMenu = true }) {
            Icon(
                imageVector = Icons.Rounded.Sort,
                contentDescription = "Sort tasks",
                tint = MaterialTheme.colorScheme.primary
            )
            
            DropdownMenu(
                expanded = showSortMenu,
                onDismissRequest = { showSortMenu = false }
            ) {
                TaskSortCriteria.entries.forEach { sortType ->
                    DropdownMenuItem(
                        text = { Text(sortType.displayName) },
                        onClick = {
                            onSortCriteriaChange(sortType)
                            showSortMenu = false
                        },
                        trailingIcon = {
                            if (sortType == sortCriteria) {
                                Icon(
                                    imageVector = Icons.Rounded.Sort,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    )
                }
                
                // Direction options
                DropdownMenuItem(
                    text = { Text("Ascending") },
                    onClick = {
                        onSortDirectionChange(true)
                        showSortMenu = false
                    },
                    trailingIcon = {
                        if (sortAscending) {
                            Icon(
                                imageVector = Icons.Rounded.Sort,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                )
                DropdownMenuItem(
                    text = { Text("Descending") },
                    onClick = {
                        onSortDirectionChange(false)
                        showSortMenu = false
                    },
                    trailingIcon = {
                        if (!sortAscending) {
                            Icon(
                                imageVector = Icons.Rounded.Sort,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                )
            }
        }
    }
    
    // Filter chips
    if (selectedFilters.isNotEmpty()) {
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp)
        ) {
            items(selectedFilters.toList()) { filter ->
                FilterChip(
                    selected = true,  // These are already selected filters
                    onClick = { 
                        onFilterChipSelected(filter, false) // Deselect the filter
                    },
                    label = { Text(filter.displayName) },
                    modifier = Modifier.padding(end = 8.dp)
                )
            }
        }
    }
}
