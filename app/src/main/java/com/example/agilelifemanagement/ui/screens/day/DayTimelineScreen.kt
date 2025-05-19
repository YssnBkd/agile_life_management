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
import androidx.compose.foundation.layout.size
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
import com.example.agilelifemanagement.ui.model.ActivityCategoryEnum
import com.example.agilelifemanagement.ui.mapper.getColor

/**
 * Screen displaying a timeline of day activities.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DayTimelineScreen(
    navController: NavController,
    selectedDate: LocalDate = LocalDate.now(),
    onAddActivity: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    // Format date for display
    val formattedDate = remember(selectedDate) {
        selectedDate.format(DateTimeFormatter.ofPattern("MMMM d, yyyy"))
    }
    
    val dayOfWeek = remember(selectedDate) {
        selectedDate.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault())
    }
    
    // Scroll behavior for collapsing top app bar
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        rememberTopAppBarState()
    )
    
    // Sample data - would come from ViewModel in real implementation
    val timeBlocks = remember {
        listOf(
            UiTimeBlock(
                id = "1",
                title = "Morning Routine",
                startTime = "07:00",
                endTime = "08:30",
                category = ActivityCategoryEnum.PERSONAL
            ),
            UiTimeBlock(
                id = "2",
                title = "Team Standup",
                startTime = "09:00",
                endTime = "09:30",
                category = ActivityCategoryEnum.WORK
            ),
            UiTimeBlock(
                id = "3",
                title = "Project Planning",
                startTime = "10:00",
                endTime = "12:00",
                category = ActivityCategoryEnum.WORK
            ),
            UiTimeBlock(
                id = "4",
                title = "Lunch Break",
                startTime = "12:00",
                endTime = "13:00",
                category = ActivityCategoryEnum.REST
            ),
            UiTimeBlock(
                id = "5",
                title = "Workout Session",
                startTime = "17:30",
                endTime = "18:30",
                category = ActivityCategoryEnum.EXERCISE
            )
        )
    }
    
    // Track completion state separately since TimeBlock doesn't have this field
    val completedTimeBlocks = remember { mutableSetOf("1", "2") }
    
    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            DayTimelineTopBar(
                date = formattedDate,
                dayOfWeek = dayOfWeek,
                scrollBehavior = scrollBehavior,
                onBackClick = { navController.popBackStack() }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = { Text("Add Activity") },
                icon = { Icon(Icons.Rounded.Add, contentDescription = null) },
                onClick = onAddActivity,
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
                onTimeBlockClick = { /* Handle time block click */ },
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
                    text = dayOfWeek,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = date,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
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
        colors = TopAppBarDefaults.largeTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface
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
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Rounded.CalendarMonth,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
            modifier = Modifier.size(80.dp)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "No activities planned for this day",
            style = MaterialTheme.typography.titleLarge
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Tap the + button to add a new activity",
            style = MaterialTheme.typography.bodyMedium,
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
            .padding(paddingValues)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
        }
        
        items(timeBlocks) { timeBlock ->
            TimelineItem(
                timeBlock = timeBlock,
                isLastItem = timeBlock == timeBlocks.last(),
                onClick = { onTimeBlockClick(timeBlock.id) },
                isCompleted = completedTimeBlocks.contains(timeBlock.id)
            )
        }
        
        item {
            Spacer(modifier = Modifier.height(80.dp)) // Space for FAB
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
        endTime = timeBlock.endTime,
        category = timeBlock.category,
        isCompleted = isCompleted,
        onClick = onClick
    )
}

/**
 * Represents a block of time for an activity
 */
@Composable
private fun TimeBlockCard(
    title: String,
    startTime: String,
    endTime: String,
    category: ActivityCategoryEnum,
    isCompleted: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    androidx.compose.material3.Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        colors = androidx.compose.material3.CardDefaults.cardColors(
            containerColor = category.color.copy(alpha = 0.15f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Activity title
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = if (isCompleted) 
                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                else 
                    MaterialTheme.colorScheme.onSurface,
                textDecoration = if (isCompleted) 
                    androidx.compose.ui.text.style.TextDecoration.LineThrough
                else 
                    androidx.compose.ui.text.style.TextDecoration.None
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            // Activity time
            Text(
                text = "$startTime - $endTime",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Category badge
            androidx.compose.material3.Surface(
                shape = MaterialTheme.shapes.small,
                color = category.color.copy(alpha = 0.15f)
            ) {
                Text(
                    text = category.displayName,
                    style = MaterialTheme.typography.labelMedium,
                    color = category.color,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}
