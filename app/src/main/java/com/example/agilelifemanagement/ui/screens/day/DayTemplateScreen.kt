package com.example.agilelifemanagement.ui.screens.day

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.agilelifemanagement.domain.model.DayTemplate
import com.example.agilelifemanagement.domain.model.TemplateActivity
import com.example.agilelifemanagement.ui.components.cards.ExpressiveCard
import com.example.agilelifemanagement.ui.screens.day.viewmodel.TemplateViewModel
import com.example.agilelifemanagement.ui.theme.AgileLifeTheme
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * DayTemplateScreen allows users to create, edit, and apply day templates
 * following Material 3 Expressive design principles.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DayTemplateScreen(
    onBackClick: () -> Unit,
    onApplyTemplateClick: (String, LocalDate) -> Unit,
    viewModel: TemplateViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    // Get UI state from ViewModel
    val uiState by viewModel.uiState.collectAsState()
    
    // For error handling
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    
    // Show error messages in snackbar
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { message ->
            coroutineScope.launch {
                snackbarHostState.showSnackbar(message)
            }
        }
    }
    
    // Dialog states
    var showCreateDialog by remember { mutableStateOf(false) }
    var showApplyDialog by remember { mutableStateOf(false) }
    var selectedTemplate by remember { mutableStateOf<DayTemplate?>(null) }
    
    // Top app bar scroll behavior
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    
    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Day Templates",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Rounded.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreateDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Create template"
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        // Show loading state
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (uiState.templates.isEmpty()) {
            // Empty state
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Schedule,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                    modifier = Modifier.size(100.dp)
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Text(
                    text = "No Day Templates",
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "Create templates to quickly plan your day with predefined schedules",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Button(
                    onClick = { showCreateDialog = true }
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null
                    )
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Text("Create Template")
                }
            }
        } else {
            // Template list
            LazyColumn(
                contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                items(uiState.templates) { template ->
                    TemplateCard(
                        template = template,
                        onApplyClick = {
                            selectedTemplate = template
                            showApplyDialog = true
                        },
                        onEditClick = {
                            // Edit template logic would be implemented here
                            // viewModel.selectTemplate(template.id)
                        },
                        onDeleteClick = {
                            viewModel.deleteTemplate(template.id)
                        }
                    )
                }
                
                // Bottom spacing for FAB
                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }
    
    // Create template dialog
    if (showCreateDialog) {
        TemplateCreationDialog(
            onDismiss = { showCreateDialog = false },
            onSave = { name, description, activities ->
                // Create template via ViewModel
                viewModel.createTemplate(name, description, activities)
                showCreateDialog = false
            }
        )
    }
    
    // Apply template dialog
    if (showApplyDialog && selectedTemplate != null) {
        TemplateApplyDialog(
            template = selectedTemplate!!,
            onDismiss = {
                showApplyDialog = false
                selectedTemplate = null
            },
            onApply = { date ->
                // Apply template via ViewModel
                viewModel.applyTemplate(selectedTemplate!!.id, date)
                onApplyTemplateClick(selectedTemplate!!.id, date)
                showApplyDialog = false
                selectedTemplate = null
            }
        )
    }
}

@Composable
private fun TemplateCard(
    template: DayTemplate,
    onApplyClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    ExpressiveCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Template header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = template.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    
                    if (template.description.isNotBlank()) {
                        Text(
                            text = template.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    
                    // Show last used date if available
                    template.lastUsedDate?.let { lastUsed ->
                        Text(
                            text = "Last used: ${lastUsed.format(DateTimeFormatter.ofPattern("MMM d, yyyy"))}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                
                // Edit button
                IconButton(onClick = onEditClick) {
                    Icon(
                        imageVector = Icons.Rounded.Edit,
                        contentDescription = "Edit template",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                
                // Delete button
                IconButton(onClick = onDeleteClick) {
                    Icon(
                        imageVector = Icons.Rounded.Delete,
                        contentDescription = "Delete template",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Activity summary
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${template.activities.size} activities",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                if (template.useCount > 0) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "• Used ${template.useCount} ${if (template.useCount == 1) "time" else "times"}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Sample activities (show first 3)
            val displayActivities = template.activities.take(3)
            displayActivities.forEach { activity ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    Text(
                        text = "${activity.startTime}",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    Text(
                        text = activity.title,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    
                    Spacer(modifier = Modifier.width(4.dp))
                    
                    Text(
                        text = "(${activity.durationMinutes} mins)",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            // Show "more" indicator if there are more activities
            if (template.activities.size > 3) {
                Text(
                    text = "+ ${template.activities.size - 3} more activities",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Apply button
            Button(
                onClick = onApplyClick,
                modifier = Modifier.align(Alignment.End)
            ) {
                Icon(
                    imageVector = Icons.Rounded.ContentCopy,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                
                Spacer(modifier = Modifier.width(4.dp))
                
                Text("Apply")
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    
                    Spacer(modifier = Modifier.width(4.dp))
                    
                    Text("Apply")
                }
            }
        }
    }
}

@Composable
private fun TemplateCreationDialog(
    onDismiss: () -> Unit,
    onSave: (String, String, List<TemplateActivity>) -> Unit,
    viewModel: TemplateViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    // Get categories from ViewModel
    val uiState by viewModel.uiState.collectAsState()
    
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    val activities = remember { mutableStateListOf<TemplateActivity>() }
    
    // Form validation
    val isValid = name.isNotBlank() && activities.isNotEmpty()
    
    // Activity temp states
    var showAddActivityDialog by remember { mutableStateOf(false) }
    var tempTitle by remember { mutableStateOf("") }
    var tempStartTime by remember { mutableStateOf("") }
    var tempDurationMinutes by remember { mutableStateOf("") }
    var selectedCategoryId by remember { mutableStateOf<String?>(null) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create Day Template") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                // Template name
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Template Name") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Words
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Template description
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description (Optional)") },
                    singleLine = false,
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Activities section header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Activities",
                        style = MaterialTheme.typography.titleMedium
                    )
                    
                    TextButton(onClick = { showAddActivityDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        
                        Spacer(modifier = Modifier.width(4.dp))
                        
                        Text("Add")
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Activities list
                if (activities.isEmpty()) {
                    Text(
                        text = "No activities added yet",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                } else {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        activities.forEach { activity ->
                            ActivityItem(
                                activity = activity,
                                onRemove = { activities.remove(activity) }
                            )
                        }
                    }
                }
                
                // Activity dialog
                if (showAddActivityDialog) {
                    AlertDialog(
                        onDismissRequest = { 
                            showAddActivityDialog = false 
                            tempTitle = ""
                            tempStartTime = ""
                            tempDurationMinutes = ""
                            selectedCategoryId = null
                        },
                        title = { Text("Add Activity") },
                        text = {
                            Column {
                                OutlinedTextField(
                                    value = tempTitle,
                                    onValueChange = { tempTitle = it },
                                    label = { Text("Activity Title") },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                
                                Spacer(modifier = Modifier.height(8.dp))
                                
                                OutlinedTextField(
                                    value = tempStartTime,
                                    onValueChange = { tempStartTime = it },
                                    label = { Text("Start Time (HH:MM)") },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth(),
                                    placeholder = { Text("e.g., 09:30") }
                                )
                                
                                Spacer(modifier = Modifier.height(8.dp))
                                
                                OutlinedTextField(
                                    value = tempDurationMinutes,
                                    onValueChange = { tempDurationMinutes = it },
                                    label = { Text("Duration (minutes)") },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth(),
                                    placeholder = { Text("e.g., 30") }
                                )
                                
                                if (uiState.categories.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    
                                    Text(
                                        text = "Activity Category",
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = Modifier.padding(bottom = 4.dp)
                                    )
                                    
                                    // Display categories as chips that can be selected
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        uiState.categories.forEach { category ->
                                            val isSelected = selectedCategoryId == category.id
                                            Surface(
                                                shape = MaterialTheme.shapes.small,
                                                color = if (isSelected) category.color.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant,
                                                border = if (isSelected) androidx.compose.foundation.BorderStroke(
                                                    width = 1.dp,
                                                    color = category.color
                                                ) else null,
                                                modifier = Modifier.clip(MaterialTheme.shapes.small)
                                                    .clickable {
                                                        selectedCategoryId = if (isSelected) null else category.id
                                                    }
                                            ) {
                                                Text(
                                                    text = category.name,
                                                    style = MaterialTheme.typography.bodySmall,
                                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    if (tempTitle.isNotBlank() && tempStartTime.isNotBlank() && tempDurationMinutes.isNotBlank()) {
                                        val durationInt = tempDurationMinutes.toIntOrNull() ?: 30
                                        
                                        // Find the category if one was selected
                                        val category = selectedCategoryId?.let { categoryId ->
                                            uiState.categories.find { it.id == categoryId }
                                        }
                                        
                                        activities.add(
                                            TemplateActivity(
                                                id = "new_activity_${System.currentTimeMillis()}",
                                                title = tempTitle,
                                                startTime = tempStartTime,
                                                durationMinutes = durationInt,
                                                categoryId = selectedCategoryId,
                                                color = category?.color ?: Color.Unspecified
                                            )
                                        )
                                        tempTitle = ""
                                        tempStartTime = ""
                                        tempDurationMinutes = ""
                                        selectedCategoryId = null
                                        showAddActivityDialog = false
                                    }
                                }
                            ) {
                                Text("Add")
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { 
                                showAddActivityDialog = false 
                                tempTitle = ""
                                tempStartTime = ""
                                tempDurationMinutes = ""
                                selectedCategoryId = null
                            }) {
                                Text("Cancel")
                            }
                        }
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onSave(name, description, activities) },
                enabled = isValid
            ) {
                Text("Save Template")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun ActivityItem(
    activity: TemplateActivity,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.small)
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = activity.title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
            
            Text(
                text = "${activity.startTime} (${activity.durationMinutes} mins)",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        IconButton(
            onClick = onRemove,
            modifier = Modifier.size(28.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.Delete,
                contentDescription = "Remove activity",
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
private fun TemplateApplyDialog(
    template: DayTemplate,
    onDismiss: () -> Unit,
    onApply: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Apply Template") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Apply '${template.name}' template to:",
                    style = MaterialTheme.typography.bodyLarge
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Date picker would go here in a real implementation
                // This is just a placeholder row for demonstration
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(MaterialTheme.shapes.small)
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                        .padding(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Today,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    Column {
                        Text(
                            text = selectedDate.format(DateTimeFormatter.ofPattern("EEEE", Locale.getDefault())),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        
                        Text(
                            text = selectedDate.format(DateTimeFormatter.ofPattern("MMMM d, yyyy", Locale.getDefault())),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onApply(selectedDate) }
            ) {
                Icon(
                    imageVector = Icons.Rounded.ContentCopy,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                
                Spacer(modifier = Modifier.width(4.dp))
                
                Text("Apply")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

//@Composable
//private fun TemplateCard(
//    template: DayTemplate,
//    onApplyClick: () -> Unit,
//    onEditClick: () -> Unit,
//    onDeleteClick: () -> Unit
//) {