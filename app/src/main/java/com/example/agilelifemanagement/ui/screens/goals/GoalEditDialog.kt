package com.example.agilelifemanagement.ui.screens.goals

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.agilelifemanagement.domain.model.Goal
import com.example.agilelifemanagement.ui.components.DatePickerField
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalEditDialog(
    goal: Goal? = null,
    onDismiss: () -> Unit,
    onSave: (title: String, summary: String, category: Goal.Category, deadline: LocalDate?) -> Unit
) {
    var title by remember { mutableStateOf(goal?.title ?: "") }
    var summary by remember { mutableStateOf(goal?.summary ?: "") }
    var category by remember { mutableStateOf(goal?.category ?: Goal.Category.PERSONAL) }
    var deadline by remember { mutableStateOf(goal?.deadline) }
    
    val isEdit = goal != null
    val dialogTitle = if (isEdit) "Edit Goal" else "Create Goal"
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(dialogTitle) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Title field
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    placeholder = { Text("Enter goal title") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                // Summary field
                OutlinedTextField(
                    value = summary,
                    onValueChange = { summary = it },
                    label = { Text("Summary") },
                    placeholder = { Text("Brief description of your goal") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    maxLines = 4
                )
                
                // Category selector
                Text("Category", style = MaterialTheme.typography.bodyMedium)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Goal.Category.values().forEach { cat ->
                        FilterChip(
                            selected = category == cat,
                            onClick = { category = cat },
                            label = { 
                                Text(
                                    cat.name.lowercase().replaceFirstChar { it.uppercase() },
                                    style = MaterialTheme.typography.labelMedium
                                ) 
                            }
                        )
                    }
                }
                
                // Deadline picker
                DatePickerField(
                    label = "Deadline (Optional)",
                    date = deadline,
                    onDateSelected = { deadline = it },
                    allowRemoval = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onSave(title, summary, category, deadline)
                    }
                },
                enabled = title.isNotBlank()
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
