package com.example.agilelifemanagement.ui.components.sprint

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.agilelifemanagement.domain.model.Sprint

/**
 * A reusable sprint selector component that allows a task to be assigned to a sprint.
 * 
 * @param sprints List of available sprints to choose from
 * @param selectedSprintId The currently selected sprint ID or null if none selected
 * @param onSprintSelected Callback when a sprint is selected, null means unassigned
 * @param modifier Modifier for styling
 * @param label Label for the selector
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SprintSelector(
    sprints: List<Sprint>,
    selectedSprintId: String?,
    onSprintSelected: (String?) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "Assign to Sprint"
) {
    var expanded by remember { mutableStateOf(false) }
    
    // Find the selected sprint name or use "None" if null
    val selectedSprint = sprints.find { it.id == selectedSprintId }
    val displayName = selectedSprint?.name ?: "None"
    
    Column(modifier = modifier) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = displayName,
                onValueChange = {},
                readOnly = true,
                label = { Text(label) },
                trailingIcon = { 
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) 
                },
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
            )
            
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                // Option to unassign from sprint
                DropdownMenuItem(
                    text = { Text("None") },
                    onClick = { 
                        onSprintSelected(null) 
                        expanded = false
                    }
                )
                
                // List all sprints
                sprints.forEach { sprint ->
                    DropdownMenuItem(
                        text = { 
                            Text(
                                sprint.name,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            ) 
                        },
                        onClick = { 
                            onSprintSelected(sprint.id) 
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}
