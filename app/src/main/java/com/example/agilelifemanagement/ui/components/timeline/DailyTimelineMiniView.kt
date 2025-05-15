package com.example.agilelifemanagement.ui.components.timeline

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.agilelifemanagement.ui.theme.AgileLifeTheme

/**
 * Condensed timeline view for the day's schedule to be shown on the Dashboard
 * 
 * Features:
 * - Compact representation of day's schedule
 * - Color-coded time blocks for different activities
 * - Material 3 Expressive styling with generous spacing and bold colors
 */
@Composable
fun DailyTimelineMiniView(
    dateTitle: String,
    timeBlocks: List<TimeBlock>,
    onViewFullSchedule: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceContainer,
        tonalElevation = 1.dp,
        shape = MaterialTheme.shapes.large,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header with date and view all button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = dateTitle,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                TextButton(onClick = onViewFullSchedule) {
                    Text(
                        text = "View Full Schedule",
                        style = MaterialTheme.typography.labelLarge
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Rounded.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Display time blocks in a condensed format
            timeBlocks.forEach { timeBlock ->
                TimeBlockItem(timeBlock = timeBlock)
                Spacer(modifier = Modifier.height(8.dp))
            }
            
            // Show empty state if no time blocks
            if (timeBlocks.isEmpty()) {
                EmptySchedule()
            }
        }
    }
}

/**
 * Single time block item showing a scheduled activity
 */
@Composable
private fun TimeBlockItem(
    timeBlock: TimeBlock,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Time indicator
        Text(
            text = timeBlock.timeRange,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(72.dp)
        )
        
        // Activity block with category color
        Box(
            modifier = Modifier
                .weight(1f)
                .height(40.dp)
                .clip(MaterialTheme.shapes.medium)
                .background(getCategoryColor(timeBlock.category).copy(alpha = 0.15f))
                .border(
                    width = 1.dp,
                    color = getCategoryColor(timeBlock.category).copy(alpha = 0.5f),
                    shape = MaterialTheme.shapes.medium
                )
                .padding(horizontal = 12.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = timeBlock.title,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/**
 * Empty state when no schedule is available
 */
@Composable
private fun EmptySchedule() {
    Surface(
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
    ) {
        Text(
            text = "No activities scheduled for today",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(16.dp)
        )
    }
}

/**
 * Get color for time block category
 */
@Composable
private fun getCategoryColor(category: TimeBlockCategory): Color {
    return when (category) {
        TimeBlockCategory.TASK -> AgileLifeTheme.extendedColors.accentCoral
        TimeBlockCategory.MEETING -> AgileLifeTheme.extendedColors.accentLavender
        TimeBlockCategory.FOCUS -> AgileLifeTheme.extendedColors.accentMint
        TimeBlockCategory.BREAK -> AgileLifeTheme.extendedColors.accentSunflower
        TimeBlockCategory.PERSONAL -> MaterialTheme.colorScheme.tertiary
    }
}

/**
 * Data class representing a block of time in the schedule
 */
data class TimeBlock(
    val id: String,
    val title: String,
    val timeRange: String,
    val category: TimeBlockCategory
)

/**
 * Categories for time blocks
 */
enum class TimeBlockCategory {
    TASK,
    MEETING,
    FOCUS,
    BREAK,
    PERSONAL
}
