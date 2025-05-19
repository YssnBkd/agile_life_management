package com.example.agilelifemanagement.ui.screens.day

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.agilelifemanagement.domain.model.DayTemplate
import com.example.agilelifemanagement.ui.components.day.TemplateApplyDialog
import com.example.agilelifemanagement.ui.components.day.TemplateCard
import com.example.agilelifemanagement.ui.screens.day.components.TemplateList
import com.example.agilelifemanagement.ui.components.day.TemplateCreationDialog
import com.example.agilelifemanagement.ui.screens.day.viewmodel.TemplateViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate

/**
 * DayTemplateScreen allows users to create, edit, and apply day templates
 * following Material 3 Expressive design principles.
 * 
 * This implementation uses extracted components for better maintainability.
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
        snackbarHost = { SnackbarHost(snackbarHostState) },
        content = { paddingValues ->
            TemplateList(
                templates = uiState.templates,
                isLoading = uiState.isLoading,
                onTemplateClick = { /* Not used in this screen */ },
                onEditClick = { template ->
                    viewModel.selectTemplateForEdit(template.id)
                    showCreateDialog = true
                },
                onDeleteClick = { template ->
                    viewModel.deleteTemplate(template.id)
                },
                onApplyClick = { template ->
                    selectedTemplate = template
                    showApplyDialog = true
                },
                onCreateClick = { showCreateDialog = true },
                modifier = Modifier.padding(paddingValues)
            )
        }
    )
    
    // Show create/edit template dialog
    if (showCreateDialog) {
        TemplateCreationDialog(
            onDismiss = { showCreateDialog = false },
            onSave = { name, description, activities ->
                viewModel.createTemplate(name, description, activities)
                showCreateDialog = false
            }
        )
    }
    
    // Show apply template dialog
    if (showApplyDialog && selectedTemplate != null) {
        TemplateApplyDialog(
            template = selectedTemplate!!,
            onDismiss = { showApplyDialog = false },
            onApply = { date ->
                onApplyTemplateClick(selectedTemplate!!.id, date)
                showApplyDialog = false
            }
        )
    }
}

/**
 * EmptyTemplatesView displays a message and button when no templates exist.
 *
 * @param onCreateClick Callback when create button is clicked
 * @param modifier Optional modifier
 */
@Composable
private fun EmptyTemplatesView(
    onCreateClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier.padding(16.dp)
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
        
        Button(onClick = onCreateClick) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Text("Create Template")
        }
    }
}
