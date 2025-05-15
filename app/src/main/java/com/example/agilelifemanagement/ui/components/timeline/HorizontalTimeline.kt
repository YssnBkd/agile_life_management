package com.example.agilelifemanagement.ui.components.timeline

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.agilelifemanagement.ui.theme.AgileLifeTheme

/**
 * Horizontal timeline component for Material 3 Expressive design
 * 
 * Features:
 * - Generous spacing and visual emphasis
 * - Bold indicators for current day
 * - Status indicators for day completion
 * - Smooth animations for selection changes
 * - Custom styling for different day states
 */
@Composable
fun HorizontalTimeline(
    days: List<TimelineDay>,
    selectedDayId: String,
    onDaySelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()
    
    // Auto-scroll to selected day
    val selectedIndex = days.indexOfFirst { it.id == selectedDayId }.takeIf { it >= 0 } ?: 0
    LaunchedEffect(selectedDayId) {
        listState.animateScrollToItem(
            index = selectedIndex.coerceAtMost(days.lastIndex),
            scrollOffset = -100 // Center item
        )
    }
    
    Surface(
        color = MaterialTheme.colorScheme.surfaceContainer,
        tonalElevation = 1.dp,
        shape = MaterialTheme.shapes.medium,
        modifier = modifier
    ) {
        LazyRow(
            state = listState,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            items(days) { day ->
                DayItem(
                    day = day,
                    isSelected = day.id == selectedDayId,
                    onClick = { onDaySelected(day.id) }
                )
            }
        }
    }
}

/**
 * Single day item in the timeline
 */
@Composable
private fun DayItem(
    day: TimelineDay,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    // Animated properties for selected state
    val containerColor by animateColorAsState(
        targetValue = when {
            isSelected -> MaterialTheme.colorScheme.primaryContainer
            day.isToday -> MaterialTheme.colorScheme.surfaceContainerHigh
            else -> MaterialTheme.colorScheme.surfaceContainer
        },
        label = "containerColor"
    )
    
    val textColor by animateColorAsState(
        targetValue = when {
            isSelected -> MaterialTheme.colorScheme.onPrimaryContainer
            day.isToday -> MaterialTheme.colorScheme.onSurface
            else -> MaterialTheme.colorScheme.onSurfaceVariant
        },
        label = "textColor"
    )
    
    val borderWidth by animateDpAsState(
        targetValue = if (day.isToday && !isSelected) 1.dp else 0.dp,
        label = "borderWidth"
    )
    
    val dayOfMonthStyle = if (day.isToday || isSelected) {
        MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
    } else {
        MaterialTheme.typography.titleMedium
    }
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(MaterialTheme.shapes.medium)
            .border(
                width = borderWidth,
                color = MaterialTheme.colorScheme.primary,
                shape = MaterialTheme.shapes.medium
            )
            .background(containerColor)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .width(48.dp)
    ) {
        // Day of week (Mon, Tue, etc)
        Text(
            text = day.dayOfWeek,
            style = MaterialTheme.typography.bodySmall,
            color = textColor,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        // Day of month (1, 2, etc)
        Text(
            text = day.dayOfMonth,
            style = dayOfMonthStyle,
            color = textColor,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        // Status indicator
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(getDayStatusColor(day))
                .align(Alignment.CenterHorizontally)
        )
    }
}

/**
 * Get the appropriate color for day status indicators
 */
@Composable
private fun getDayStatusColor(day: TimelineDay): Color {
    return when {
        day.isPast && day.completionPercentage >= 0.9f -> AgileLifeTheme.extendedColors.accentMint
        day.isPast && day.completionPercentage >= 0.5f -> AgileLifeTheme.extendedColors.accentSunflower
        day.isPast && day.completionPercentage < 0.5f -> AgileLifeTheme.extendedColors.accentCoral
        day.isToday -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.surfaceContainerHighest
    }
}

/**
 * Data class representing a day in the timeline
 */
data class TimelineDay(
    val id: String,
    val dayOfWeek: String,
    val dayOfMonth: String,
    val isToday: Boolean = false,
    val isPast: Boolean = false,
    val completionPercentage: Float = 0f
)
