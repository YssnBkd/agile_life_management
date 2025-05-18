package com.example.agilelifemanagement.ui.screens.day

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.InsertInvitation
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.ChevronLeft
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Today
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Circle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.agilelifemanagement.R
import com.example.agilelifemanagement.domain.model.DayActivity
import com.example.agilelifemanagement.ui.components.cards.ExpressiveCard
import com.example.agilelifemanagement.ui.screens.day.viewmodel.WeekViewModel
import com.example.agilelifemanagement.ui.theme.AgileLifeTheme
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalAdjusters
import java.time.temporal.WeekFields
import java.util.Locale
import kotlinx.coroutines.launch

/**
 * WeekViewScreen displays a weekly calendar view of activities
 * following Material 3 Expressive design principles.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeekViewScreen(
    onBackClick: () -> Unit,
    onDayClick: (String) -> Unit,
    onAddActivityClick: (String) -> Unit,
    viewModel: WeekViewModel = hiltViewModel(),
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
    
    // Calculate current week end based on start date
    val currentWeekEnd = remember(uiState.currentWeekStart) { 
        uiState.currentWeekStart.plusDays(6) 
    }
    
    // Top app bar scroll behavior
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    
    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = {
                    val monthFormatter = DateTimeFormatter.ofPattern("MMMM yyyy")
                    val displayMonth = if (uiState.currentWeekStart.month == currentWeekEnd.month) {
                        uiState.currentWeekStart.format(monthFormatter)
                    } else {
                        "${uiState.currentWeekStart.month.getDisplayName(TextStyle.SHORT, Locale.getDefault())} - " +
                        "${currentWeekEnd.month.getDisplayName(TextStyle.SHORT, Locale.getDefault())} " +
                        uiState.currentWeekStart.year
                    }
                    
                    Text(
                        text = displayMonth,
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
                actions = {
                    // Previous week button
                    IconButton(onClick = { viewModel.navigateToPreviousWeek() }) {
                        Icon(
                            imageVector = Icons.Rounded.ChevronLeft,
                            contentDescription = "Previous week"
                        )
                    }
                    
                    // Today button
                    IconButton(onClick = { viewModel.navigateToToday() }) {
                        Icon(
                            imageVector = Icons.Rounded.Today,
                            contentDescription = "Today"
                        )
                    }
                    
                    // Next week button
                    IconButton(onClick = { viewModel.navigateToNextWeek() }) {
                        Icon(
                            imageVector = Icons.Rounded.ChevronRight,
                            contentDescription = "Next week"
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
                onClick = { onAddActivityClick(uiState.selectedDay.toString()) },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add activity"
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // Week days selector
                val weekDays = (0..6).map { uiState.currentWeekStart.plusDays(it.toLong()) }
                
                WeekDaySelector(
                    days = weekDays,
                    selectedDay = uiState.selectedDay,
                    today = LocalDate.now(),
                    onDaySelected = { viewModel.selectDay(it) }
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Daily schedule
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                ) {
                    val activitiesForSelectedDay = uiState.weekActivities[uiState.selectedDay] ?: emptyList()
                    
                    if (activitiesForSelectedDay.isEmpty()) {
                        EmptyDayView(
                            date = uiState.selectedDay,
                            onAddActivityClick = { onAddActivityClick(uiState.selectedDay.toString()) }
                        )
                    } else {
                        DayScheduleView(
                            date = uiState.selectedDay,
                            activities = activitiesForSelectedDay,
                            onActivityClick = { 
                                // Navigate to activity detail when clicked
                                // This would typically navigate to an activity detail screen
                                onDayClick(uiState.selectedDay.toString())
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun WeekDaySelector(
    days: List<LocalDate>,
    selectedDay: LocalDate,
    today: LocalDate,
    onDaySelected: (LocalDate) -> Unit,
    viewModel: WeekViewModel = hiltViewModel()
) {
    // Get UI state to check for activities on each day
    val uiState by viewModel.uiState.collectAsState()
    
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(days) { date ->
            val dayName = remember(date) {
                date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
            }
            val dayNumber = remember(date) {
                date.dayOfMonth.toString()
            }
            
            val isSelected = date.isEqual(selectedDay)
            val isToday = date.isEqual(today)
            
            // Check if there are activities on this day
            val hasActivities = uiState.weekActivities[date]?.isNotEmpty() == true
            
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onDaySelected(date) }
                    .background(
                        color = when {
                            isSelected -> MaterialTheme.colorScheme.primaryContainer
                            else -> Color.Transparent
                        }
                    )
                    .padding(vertical = 8.dp, horizontal = 12.dp)
            ) {
                // Day name (Mon, Tue, etc)
                Text(
                    text = dayName,
                    style = MaterialTheme.typography.bodySmall,
                    color = when {
                        isSelected -> MaterialTheme.colorScheme.onPrimaryContainer
                        else -> MaterialTheme.colorScheme.onSurface
                    }
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // Day number with circle indicator for today
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(
                            color = when {
                                isSelected && isToday -> MaterialTheme.colorScheme.primary
                                isToday -> MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                                else -> Color.Transparent
                            }
                        )
                        .border(
                            width = if (isToday && !isSelected) 1.dp else 0.dp,
                            color = if (isToday && !isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                            shape = CircleShape
                        )
                ) {
                    Text(
                        text = dayNumber,
                        style = MaterialTheme.typography.titleMedium,
                        color = when {
                            isSelected && isToday -> MaterialTheme.colorScheme.onPrimary
                            isSelected -> MaterialTheme.colorScheme.onPrimaryContainer
                            else -> MaterialTheme.colorScheme.onSurface
                        }
                    )
                }
                
                // Activity indicator dot
                if (hasActivities) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .size(4.dp)
                            .clip(CircleShape)
                            .background(
                                color = when {
                                    isSelected -> MaterialTheme.colorScheme.onPrimaryContainer
                                    else -> MaterialTheme.colorScheme.primary
                                }
                            )
                    )
                } else {
                    Spacer(modifier = Modifier.height(4.dp))
                }
                }
            }
        }
    }


@Composable
private fun EmptyDayView(
    date: LocalDate,
    onAddActivityClick: () -> Unit
) {
    ExpressiveCard(
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 32.dp)
        ) {
            Text(
                text = "No activities for ${date.format(DateTimeFormatter.ofPattern("EEEE, MMMM d"))}",
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Surface(
                onClick = onAddActivityClick,
                color = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                shape = MaterialTheme.shapes.medium
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Text(
                        text = "Add Activity",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    }
}

@Composable
private fun DayScheduleView(
    date: LocalDate,
    activities: List<DayActivity>,
    onActivityClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = date.format(DateTimeFormatter.ofPattern("EEEE, MMMM d")),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        // Group by time blocks
        val activityGroups = activities.groupBy { it.scheduledTime }
        
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            activityGroups.forEach { (timeBlock, activities) ->
                item {
                    TimeBlockHeader(timeBlock)
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    activities.forEach { activity ->
                        DayActivityItem(
                            activity = activity,
                            onClick = onActivityClick
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
            
            // Bottom spacing
            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

@Composable
private fun TimeBlockHeader(
    timeBlock: LocalTime
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = timeBlock.format(DateTimeFormatter.ofPattern("HH:mm")),
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.width(8.dp))
        
        Surface(
            color = MaterialTheme.colorScheme.outlineVariant,
            modifier = Modifier
                .height(1.dp)
                .weight(1f)
        ) { }
    }
}

@Composable
private fun DayActivityItem(
    activity: DayActivity,
    onClick: () -> Unit
) {
    // Default color for activity if category is not available
    val categoryColor = when (activity.categoryId) {
        "WORK" -> Color(0xFF1E88E5) // Blue
        "MEETING" -> Color(0xFF7E57C2) // Purple
        "PERSONAL" -> Color(0xFF26A69A) // Teal
        "BREAK" -> Color(0xFFFFA000) // Amber
        "FITNESS" -> Color(0xFFF44336) // Red
        "LEARNING" -> Color(0xFF66BB6A) // Green
        "SOCIAL" -> Color(0xFFEC407A) // Pink
        else -> MaterialTheme.colorScheme.primary
    }
    
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = categoryColor.copy(alpha = 0.1f),
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Title and time row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = activity.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                
                Text(
                    text = activity.scheduledTime.format(DateTimeFormatter.ofPattern("HH:mm")),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            // Description if available
            if (activity.description.isNotEmpty()) {
                Text(
                    text = activity.description,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .fillMaxWidth(),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            
            // Duration and status row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${activity.duration} min",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Icon(
                    imageVector = if (activity.completed) Icons.Rounded.CheckCircle else Icons.Rounded.Circle,
                    contentDescription = if (activity.completed) "Completed" else "Not completed",
                    tint = if (activity.completed) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}