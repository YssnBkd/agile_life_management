package com.example.agilelifemanagement.ui.components.day

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.agilelifemanagement.ui.components.cards.ExpressiveCard
import com.example.agilelifemanagement.ui.components.timeline.TimeBlock
import com.example.agilelifemanagement.ui.components.timeline.TimeBlockCategory
import com.example.agilelifemanagement.ui.model.ActivityCategoryEnum
import com.example.agilelifemanagement.ui.model.DayData
import com.example.agilelifemanagement.ui.model.SampleDayData
import com.example.agilelifemanagement.ui.theme.AgileLifeTheme

/**
 * Card displaying the day's timeline with activities.
 *
 * @param dayData The day data to display
 * @param onEditDayPlanClick Callback when edit button is clicked
 * @param modifier Optional modifier
 */
@Composable
fun DayTimeline(
    dayData: DayData,
    onEditDayPlanClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ExpressiveCard(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header with title and edit button
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Day Timeline",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                
                IconButton(
                    onClick = onEditDayPlanClick,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Edit,
                        contentDescription = "Edit timeline",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Timeline content
            if (dayData.timeBlocks.isEmpty()) {
                // Empty state
                Text(
                    text = "No activities planned for today",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "Tap the edit button to add activities",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            } else {
                // Timeline with activity blocks
                dayData.timeBlocks.forEachIndexed { index, timeBlock ->
                    TimelineItem(
                        timeBlock = timeBlock,
                        isLastItem = index == dayData.timeBlocks.size - 1
                    )
                }
            }
        }
    }
}

/**
 * Individual timeline item with time and activity details.
 */
@Composable
private fun TimelineItem(
    timeBlock: TimeBlock,
    isLastItem: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        // Activity content
        Row(
            verticalAlignment = Alignment.Top,
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            // Time display
            Column(
                horizontalAlignment = Alignment.End,
                modifier = Modifier.width(64.dp)
            ) {
                Text(
                    text = timeBlock.timeRange.split(" - ").firstOrNull() ?: "",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Bullet with vertical line
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Category-colored bullet
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(timeBlock.category.color)
                )
                
                // Vertical connection line (except for last item)
                if (!isLastItem) {
                    Box(
                        modifier = Modifier
                            .width(2.dp)
                            .height(60.dp)
                            .background(timeBlock.category.color.copy(alpha = 0.3f))
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Activity details
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = timeBlock.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // Activity time range
                Text(
                    text = timeBlock.timeRange,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // Category tag
                CategoryTag(category = timeBlock.category)
            }
        }
        
        // Divider between items
        if (!isLastItem) {
            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
            )
        }
    }
}

/**
 * Activity category tag.
 */
@Composable
private fun CategoryTag(
    category: TimeBlockCategory,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(MaterialTheme.shapes.small)
            .background(category.color.copy(alpha = 0.15f))
    ) {
        Text(
            text = getCategoryDisplayName(category),
            style = MaterialTheme.typography.labelSmall,
            color = category.color,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

/**
 * Get display name for a TimeBlockCategory.
 */
private fun getCategoryDisplayName(category: TimeBlockCategory): String {
    return when (category) {
        TimeBlockCategory.TASK -> "Task"
        TimeBlockCategory.MEETING -> "Meeting"
        TimeBlockCategory.BREAK -> "Break"
        TimeBlockCategory.FOCUS -> "Focus"
        TimeBlockCategory.PERSONAL -> "Personal"
    }
}

@Preview(showBackground = true)
@Composable
private fun DayTimelinePreview() {
    AgileLifeTheme {
        val sampleData = SampleDayData.getDay("1")
        DayTimeline(
            dayData = sampleData,
            onEditDayPlanClick = {}
        )
    }
}
