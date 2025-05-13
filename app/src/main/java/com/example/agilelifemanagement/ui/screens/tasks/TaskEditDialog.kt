package com.example.agilelifemanagement.ui.screens.tasks

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
import com.example.agilelifemanagement.domain.model.Task
import com.example.agilelifemanagement.domain.model.Tag
import com.example.agilelifemanagement.ui.components.DatePickerField
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskEditDialog(
    task: Task,
    availableTags: List<Tag> = emptyList(),
    onDismiss: () -> Unit,
    onSave: (Task) -> Unit
) {
    var title by remember { mutableStateOf(task.title) }
    var summary by remember { mutableStateOf(task.summary) }
    var description by remember { mutableStateOf(task.description.joinToString("\n")) }
    var dueDate by remember { mutableStateOf(task.dueDate) }
    var priority by remember { mutableStateOf(task.priority) }
    var status by remember { mutableStateOf(task.status) }
    var estimatedEffort by remember { mutableStateOf(task.estimatedEffort.toString()) }
    var selectedTagIds by remember { mutableStateOf(task.tags) }

    // Validation state
    var titleError by remember { mutableStateOf<String?>(null) }
    var effortError by remember { mutableStateOf<String?>(null) }

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
                    text = "Edit Task",
                    style = MaterialTheme.typography.headlineSmall
                )

                // Title field
                OutlinedTextField(
                    value = title,
                    onValueChange = { 
                        title = it
                        titleError = if (it.isBlank()) "Title is required" else null
                    },
                    label = { Text("Title") },
                    isError = titleError != null,
                    supportingText = { titleError?.let { Text(it) } },
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

                // Due date picker
                DatePickerField(
                    label = "Due Date",
                    date = dueDate,
                    onDateSelected = { dueDate = it },
                    allowRemoval = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // Priority dropdown
                ExposedDropdownMenuBox(
                    expanded = false,
                    onExpandedChange = { /* Handle dropdown */ }
                ) {
                    val priorities = Task.Priority.values()
                    var expanded by remember { mutableStateOf(false) }

                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        readOnly = true,
                        value = priority.name,
                        onValueChange = { },
                        label = { Text("Priority") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) }
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        priorities.forEach { priorityOption ->
                            DropdownMenuItem(
                                text = { Text(priorityOption.name) },
                                onClick = {
                                    priority = priorityOption
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                // Status dropdown
                ExposedDropdownMenuBox(
                    expanded = false,
                    onExpandedChange = { /* Handle dropdown */ }
                ) {
                    val statuses = Task.Status.values()
                    var expanded by remember { mutableStateOf(false) }

                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        readOnly = true,
                        value = status.name,
                        onValueChange = { },
                        label = { Text("Status") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) }
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        statuses.forEach { statusOption ->
                            DropdownMenuItem(
                                text = { Text(statusOption.name) },
                                onClick = {
                                    status = statusOption
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                // Effort estimate field
                OutlinedTextField(
                    value = estimatedEffort,
                    onValueChange = {
                        estimatedEffort = it
                        effortError = try {
                            if (it.isNotBlank()) it.toInt()
                            null
                        } catch (e: NumberFormatException) {
                            "Must be a valid number"
                        }
                    },
                    label = { Text("Estimated Effort (points)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = effortError != null,
                    supportingText = { effortError?.let { Text(it) } },
                    modifier = Modifier.fillMaxWidth()
                )

                // TODO: Add tag selection UI when tag component is available

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
                            if (title.isBlank()) {
                                titleError = "Title is required"
                                return@Button
                            }

                            val effortValue = try {
                                estimatedEffort.toIntOrNull() ?: 0
                            } catch (e: NumberFormatException) {
                                0
                            }

                            val descriptionLines = description
                                .split("\n")
                                .map { it.trim() }
                                .filter { it.isNotBlank() }

                            val updatedTask = task.copy(
                                title = title,
                                summary = summary,
                                description = descriptionLines,
                                dueDate = dueDate,
                                priority = priority,
                                status = status,
                                estimatedEffort = effortValue,
                                tags = selectedTagIds
                            )
                            onSave(updatedTask)
                        }
                    ) {
                        Text("Save")
                    }
                }
            }
        }
    }
}
