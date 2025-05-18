package com.example.agilelifemanagement.ui.screens.day

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import com.example.agilelifemanagement.ui.mapper.UiTimeBlock
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.EmojiFoodBeverage
import androidx.compose.material.icons.rounded.FitnessCenter
import androidx.compose.material.icons.rounded.MeetingRoom
import androidx.compose.material.icons.rounded.Pets
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.agilelifemanagement.ui.theme.AgileLifeTheme
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale
import com.example.agilelifemanagement.domain.model.TimeBlock

/**
 * Screen displaying a timeline of day activities
 * Implemented with Material 3 Expressive design principles
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DayTimelineScreen(
    navController: NavController,
    selectedDate: LocalDate = LocalDate.now(),
    onAddActivity: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
    
    // Format date for display
    val dateFormatter = remember { DateTimeFormatter.ofPattern("MMMM d, yyyy") }
    val formattedDate = remember(selectedDate) { dateFormatter.format(selectedDate) }
    val dayOfWeek = remember(selectedDate) { 
        selectedDate.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault()) 
    }
    
    // Sample data
    val timeBlocks = remember {
        listOf(
            UiTimeBlock(
                id = "1",
                title = "Morning Routine",
                startTime = "07:00",
                endTime = "08:30",
                category = ActivityCategory.PERSONAL
            ),
            UiTimeBlock(
                id = "2",
                title = "Team Standup",
                startTime = "09:00",
                endTime = "09:30",
                category = ActivityCategory.WORK
            ),
            UiTimeBlock(
                id = "3",
                title = "Project Planning",
                startTime = "10:00",
                endTime = "12:00",
                category = ActivityCategory.WORK
            ),
            UiTimeBlock(
                id = "4",
                title = "Lunch Break",
                startTime = "12:00",
                endTime = "13:00",
                category = ActivityCategory.BREAK
            ),
            UiTimeBlock(
                id = "5",
                title = "Workout Session",
                startTime = "17:30",
                endTime = "18:30",
                category = ActivityCategory.FITNESS
            )
        )
    }
    
    // Track completion state separately since TimeBlock doesn't have this field
    val completedTimeBlocks = remember { mutableSetOf("1", "2") }
    
    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            DayTimelineTopBar(
                date = formattedDate,
                dayOfWeek = dayOfWeek,
                scrollBehavior = scrollBehavior,
                onBackClick = { navController.navigateUp() }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onAddActivity,
                icon = { Icon(Icons.Rounded.Add, contentDescription = "Add activity") },
                text = { Text("Add Activity") },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                expanded = true
            )
        }
    ) { paddingValues ->
        if (timeBlocks.isEmpty()) {
            EmptyTimelineView(paddingValues)
        } else {
            TimelineContent(
                timeBlocks = timeBlocks,
                paddingValues = paddingValues,
                onTimeBlockClick = { blockId ->
                    // Navigate to detailed view or edit
                    navController.navigate("day/activities/$blockId")
                },
                completedTimeBlocks = completedTimeBlocks
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DayTimelineTopBar(
    date: String,
    dayOfWeek: String,
    scrollBehavior: TopAppBarScrollBehavior,
    onBackClick: () -> Unit
) {
    LargeTopAppBar(
        title = {
            Column {
                Text(
                    text = date,
                    style = MaterialTheme.typography.headlineMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = dayOfWeek,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Rounded.ArrowBack,
                    contentDescription = "Navigate back"
                )
            }
        },
        actions = {
            IconButton(onClick = { /* Show calendar picker */ }) {
                Icon(
                    imageVector = Icons.Rounded.CalendarMonth,
                    contentDescription = "Select date"
                )
            }
        },
        scrollBehavior = scrollBehavior,
        colors = TopAppBarDefaults.largeTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        )
    )
}

@Composable
private fun EmptyTimelineView(paddingValues: PaddingValues) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "No activities scheduled",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Add activities to plan your day",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun TimelineContent(
    timeBlocks: List<UiTimeBlock>,
    paddingValues: PaddingValues,
    onTimeBlockClick: (String) -> Unit,
    completedTimeBlocks: Set<String> = emptySet()
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(
            items = timeBlocks,
            key = { it.id }
        ) { timeBlock ->
            TimelineItem(
                timeBlock = timeBlock,
                isLastItem = timeBlock == timeBlocks.last(),
                onClick = { onTimeBlockClick(timeBlock.id) },
                isCompleted = completedTimeBlocks.contains(timeBlock.id)
            )
        }
    }
}

@Composable
private fun TimelineItem(
    timeBlock: UiTimeBlock,
    isLastItem: Boolean,
    onClick: () -> Unit,
    isCompleted: Boolean = false
) {
    TimeBlockCard(
        title = timeBlock.title,
        startTime = timeBlock.startTime,
        endTime = timeBlock.endTime ?: "",
        category = timeBlock.category,
        isCompleted = isCompleted,
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    )
}
/**
 * Represents a block of time for an activity
 */

/**
 * Material 3 Expressive design TimeBlockCard component
 */
@Composable
private fun TimeBlockCard(
    title: String,
    startTime: String,
    endTime: String,
    category: ActivityCategory,
    isCompleted: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    androidx.compose.material3.Card(
        onClick = onClick,
        modifier = modifier,
        colors = androidx.compose.material3.CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Time info
            Text(
                text = "$startTime - $endTime",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            // Title with completion status
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = if (isCompleted) {
                    MaterialTheme.colorScheme.onSurfaceVariant
                } else {
                    MaterialTheme.colorScheme.onSurface
                }
            )
            
            // Category indicator
            androidx.compose.material3.Surface(
                modifier = Modifier,
                shape = MaterialTheme.shapes.small,
                color = category.getCategoryColor().copy(alpha = 0.15f)
            ) {
                Text(
                    text = category.name.lowercase().replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.labelMedium,
                    color = category.getCategoryColor(),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}

// Use the ActivityCategory enum and its getCategoryColor method from DayDetailScreen.kt
