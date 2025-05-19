package com.example.agilelifemanagement.ui.components.day

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.rounded.EmojiFoodBeverage
import androidx.compose.material.icons.rounded.FitnessCenter
import androidx.compose.material.icons.rounded.SelfImprovement
import androidx.compose.material.icons.rounded.StarRate
import androidx.compose.material.icons.rounded.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.agilelifemanagement.ui.components.cards.ExpressiveCard
import com.example.agilelifemanagement.ui.model.DayData
import com.example.agilelifemanagement.ui.model.SampleDayData
import com.example.agilelifemanagement.ui.theme.AgileLifeTheme

/**
 * Card showing mood and wellness metrics for a day.
 *
 * @param dayData The day data to display
 * @param onEditMoodClick Callback when edit button is clicked
 * @param modifier Optional modifier
 */
@Composable
fun DayMoodCard(
    dayData: DayData,
    onEditMoodClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ExpressiveCard(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Card title with edit button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Wellness",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                IconButton(
                    onClick = onEditMoodClick,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Edit,
                        contentDescription = "Edit mood",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Mood indicator
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Rounded.StarRate,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Text(
                    text = "Mood: ${dayData.mood}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Wellness metrics row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                WellnessMetric(
                    icon = Icons.Rounded.SelfImprovement,
                    iconTint = Color(0xFF6D4C41),
                    label = "Sleep",
                    value = "${dayData.sleepHours}h",
                    backgroundColor = Color(0xFFEFEBE9)
                )
                
                WellnessMetric(
                    icon = Icons.Rounded.WbSunny,
                    iconTint = Color(0xFFFBC02D),
                    label = "Energy",
                    value = "${dayData.energyLevel}/10",
                    backgroundColor = Color(0xFFFFF9C4)
                )
                
                WellnessMetric(
                    icon = Icons.Rounded.FitnessCenter,
                    iconTint = Color(0xFF1976D2),
                    label = "Exercise",
                    value = "${dayData.exerciseMinutes}m",
                    backgroundColor = Color(0xFFBBDEFB)
                )
                
                WellnessMetric(
                    icon = Icons.Rounded.EmojiFoodBeverage,
                    iconTint = Color(0xFF388E3C),
                    label = "Water",
                    value = "${dayData.waterGlasses}",
                    backgroundColor = Color(0xFFDCEDC8)
                )
            }
        }
    }
}

/**
 * Individual wellness metric display.
 *
 * @param icon Icon to display
 * @param iconTint Tint color for icon
 * @param label Label text
 * @param value Value text
 * @param backgroundColor Background color
 * @param modifier Optional modifier
 */
@Composable
fun WellnessMetric(
    icon: ImageVector,
    iconTint: Color,
    label: String,
    value: String,
    backgroundColor: Color,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .padding(horizontal = 4.dp)
    ) {
        // Icon with background
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(backgroundColor)
                .padding(8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = iconTint
            )
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        // Label
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        // Value
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun Box(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    androidx.compose.foundation.layout.Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
    ) {
        content()
    }
}

@Preview(showBackground = true)
@Composable
private fun DayMoodCardPreview() {
    AgileLifeTheme {
        val sampleDayData = DayData(
            id = "1",
            date = java.time.LocalDate.now(),
            mood = 5, // Excellent mood (5/5)
            sleepHours = 8f, // 8 hours as float
            energyLevel = 5, // High energy (5/5)
            exerciseMinutes = 45,
            waterGlasses = 8,
            timeBlocks = emptyList(),
            tasks = emptyList()
        )
        
        DayMoodCard(
            dayData = sampleDayData,
            onEditMoodClick = {}
        )
    }
}
