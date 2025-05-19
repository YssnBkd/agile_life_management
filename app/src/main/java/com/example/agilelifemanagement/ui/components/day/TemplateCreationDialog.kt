package com.example.agilelifemanagement.ui.components.day

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.agilelifemanagement.domain.model.TemplateActivity
import com.example.agilelifemanagement.ui.screens.day.viewmodel.TemplateViewModel

/**
 * Dialog for creating or editing a day template.
 *
 * @param onDismiss Callback when dialog is dismissed
 * @param onSave Callback when template is saved
 * @param viewModel ViewModel for template operations
 * @param modifier Optional modifier
 */
@Composable
fun TemplateCreationDialog(
    onDismiss: () -> Unit,
    onSave: (String, String, List<TemplateActivity>) -> Unit,
    viewModel: TemplateViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    var templateName by remember { mutableStateOf("") }
    var templateDescription by remember { mutableStateOf("") }
    var newActivityName by remember { mutableStateOf("") }
    var newActivityStartTime by remember { mutableStateOf("") }
    var newActivityEndTime by remember { mutableStateOf("") }
    val activities = remember { mutableStateListOf<TemplateActivity>() }
    
    var showAddActivityDialog by remember { mutableStateOf(false) }
    
    // Form validation
    val isValid = templateName.isNotBlank()
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create Template") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                // Template name field
                OutlinedTextField(
                    value = templateName,
                    onValueChange = { templateName = it },
                    label = { Text("Template Name") },
                    placeholder = { Text("e.g. Productive Workday") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Template description field
                OutlinedTextField(
                    value = templateDescription,
                    onValueChange = { templateDescription = it },
                    label = { Text("Description (Optional)") },
                    placeholder = { Text("Describe the purpose of this template") },
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Activities section
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Activities",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.weight(1f)
                    )
                    
                    Button(
                        onClick = { showAddActivityDialog = true }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add activity"
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add")
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // List of activities
                if (activities.isEmpty()) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                    ) {
                        Text(
                            text = "No activities added yet. Add your first activity.",
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    ) {
                        items(
                            items = activities,
                            key = { activity -> activity.id }
                        ) { activity ->
                            ActivityItem(
                                activity = activity,
                                onRemove = {
                                    activities.remove(activity)
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (isValid) {
                        onSave(templateName, templateDescription, activities.toList())
                        onDismiss()
                    }
                },
                enabled = isValid
            ) {
                Icon(
                    imageVector = Icons.Rounded.Save,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Save Template")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        ),
        modifier = modifier
    )
    
    // Dialog to add a new activity
    if (showAddActivityDialog) {
        AlertDialog(
            onDismissRequest = { showAddActivityDialog = false },
            title = { Text("Add Activity") },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    // Activity name field
                    OutlinedTextField(
                        value = newActivityName,
                        onValueChange = { newActivityName = it },
                        label = { Text("Activity Name") },
                        placeholder = { Text("e.g. Morning Exercise") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Time fields
                    Row(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Start time
                        OutlinedTextField(
                            value = newActivityStartTime,
                            onValueChange = { newActivityStartTime = it },
                            label = { Text("Start Time") },
                            placeholder = { Text("09:00") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        // End time
                        OutlinedTextField(
                            value = newActivityEndTime,
                            onValueChange = { newActivityEndTime = it },
                            label = { Text("End Time") },
                            placeholder = { Text("10:00") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newActivityName.isNotBlank() && 
                            newActivityStartTime.isNotBlank() && 
                            newActivityEndTime.isNotBlank()
                        ) {
                            // Add activity to the list
                            activities.add(
                                com.example.agilelifemanagement.domain.model.TemplateActivity(
                                    id = java.util.UUID.randomUUID().toString(),
                                    title = newActivityName,
                                    description = "",
                                    timeStart = newActivityStartTime,
                                    timeEnd = newActivityEndTime,
                                    category = com.example.agilelifemanagement.domain.model.ActivityCategory(id = "other", name = "Other", color = "#808080")
                                )
                            )
                            
                            // Reset fields
                            newActivityName = ""
                            newActivityStartTime = ""
                            newActivityEndTime = ""
                            
                            // Close dialog
                            showAddActivityDialog = false
                        }
                    },
                    enabled = newActivityName.isNotBlank() && 
                        newActivityStartTime.isNotBlank() && 
                        newActivityEndTime.isNotBlank()
                ) {
                    Text("Add")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddActivityDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
