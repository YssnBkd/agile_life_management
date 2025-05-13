package com.example.agilelifemanagement.ui.screens.sprints

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.agilelifemanagement.domain.model.Sprint
import com.example.agilelifemanagement.domain.model.Tag
import com.example.agilelifemanagement.ui.components.DatePickerField
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SprintEditDialog(
    sprint: Sprint,
    availableTags: List<Tag> = emptyList(),
    onDismiss: () -> Unit,
    onSave: (Sprint) -> Unit
) {
    var name by remember { mutableStateOf(sprint.name) }
    var summary by remember { mutableStateOf(sprint.summary) }
    var description by remember { mutableStateOf(sprint.description.joinToString("\n")) }
    var startDate by remember { mutableStateOf(sprint.startDate) }
    var endDate by remember { mutableStateOf(sprint.endDate) }
    var isActive by remember { mutableStateOf(sprint.isActive) }
    var isCompleted by remember { mutableStateOf(sprint.isCompleted) }
    
    // Validation state
    var nameError by remember { mutableStateOf<String?>(null) }
    var dateError by remember { mutableStateOf<String?>(null) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Edit Sprint",
                    style = MaterialTheme.typography.headlineSmall
                )

                // Name field
                OutlinedTextField(
                    value = name,
                    onValueChange = { 
                        name = it
                        nameError = if (it.isBlank()) "Name is required" else null
                    },
                    label = { Text("Name") },
                    isError = nameError != null,
                    supportingText = { nameError?.let { Text(it) } },
                    modifier = Modifier.fillMaxWidth()
                )

                // Summary field
                OutlinedTextField(
                    value = summary,
                    onValueChange = { summary = it },
                    label = { Text("Summary") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Description field
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description (one line per bullet)") },
                    minLines = 3,
                    maxLines = 5,
                    modifier = Modifier.fillMaxWidth()
                )

                // Date pickers
                DatePickerField(
                    label = "Start Date",
                    date = startDate,
                    onDateSelected = { 
                        startDate = it ?: LocalDate.now()
                        validateDates(startDate, endDate)?.let { dateError = it }
                    },
                    allowRemoval = false,
                    modifier = Modifier.fillMaxWidth()
                )
                
                DatePickerField(
                    label = "End Date",
                    date = endDate,
                    onDateSelected = { 
                        endDate = it ?: startDate.plusWeeks(1)
                        validateDates(startDate, endDate)?.let { dateError = it }
                    },
                    allowRemoval = false,
                    modifier = Modifier.fillMaxWidth()
                )
                
                if (dateError != null) {
                    Text(
                        text = dateError!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }

                // Status switches
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Active", modifier = Modifier.weight(1f))
                    Switch(
                        checked = isActive,
                        onCheckedChange = { 
                            isActive = it
                            // If sprint is completed, it can't be active
                            if (it && isCompleted) isCompleted = false
                        }
                    )
                }
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Completed", modifier = Modifier.weight(1f))
                    Switch(
                        checked = isCompleted,
                        onCheckedChange = { 
                            isCompleted = it
                            // If sprint is completed, it can't be active
                            if (it && isActive) isActive = false
                        }
                    )
                }

                // TODO: Add task and goal selection if available

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (name.isBlank()) {
                                nameError = "Name is required"
                                return@Button
                            }
                            
                            val validationError = validateDates(startDate, endDate)
                            if (validationError != null) {
                                dateError = validationError
                                return@Button
                            }

                            val descriptionLines = description
                                .split("\n")
                                .map { it.trim() }
                                .filter { it.isNotBlank() }

                            val updatedSprint = sprint.copy(
                                name = name,
                                summary = summary,
                                description = descriptionLines,
                                startDate = startDate,
                                endDate = endDate,
                                isActive = isActive,
                                isCompleted = isCompleted
                            )
                            onSave(updatedSprint)
                        }
                    ) {
                        Text("Save")
                    }
                }
            }
        }
    }
}

private fun validateDates(startDate: LocalDate?, endDate: LocalDate?): String? {
    if (startDate == null || endDate == null) {
        return "Both start and end dates are required"
    }
    
    if (endDate.isBefore(startDate)) {
        return "End date must be after start date"
    }
    
    return null
}
