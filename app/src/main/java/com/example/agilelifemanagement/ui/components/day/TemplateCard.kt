package com.example.agilelifemanagement.ui.components.day

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.agilelifemanagement.domain.model.DayTemplate
import com.example.agilelifemanagement.domain.model.TemplateActivity
import com.example.agilelifemanagement.ui.components.cards.ExpressiveCard
import com.example.agilelifemanagement.ui.theme.AgileLifeTheme

/**
 * A card displaying a day template with actions for applying, editing, and deleting.
 *
 * @param template The template to display
 * @param onApplyClick Callback when apply button is clicked
 * @param onEditClick Callback when edit button is clicked
 * @param onDeleteClick Callback when delete button is clicked
 * @param modifier Optional modifier
 */
@Composable
fun TemplateCard(
    template: DayTemplate,
    onApplyClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ExpressiveCard(
        modifier = modifier.fillMaxWidth(),
        onClick = onApplyClick
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header with title and actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = template.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                
                Row {
                    // Apply button
                    IconButton(onClick = onApplyClick) {
                        Icon(
                            imageVector = Icons.Rounded.CalendarMonth,
                            contentDescription = "Apply template",
                            tint = MaterialTheme.colorScheme.primary
                        )
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
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Description
            if (template.description.isNotBlank()) {
                Text(
                    text = template.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(12.dp))
            }
            
            // Activity count
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Rounded.ContentCopy,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.height(16.dp)
                )
                
                Spacer(modifier = Modifier.width(4.dp))
                
                Text(
                    text = "${template.activities.size} activities",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TemplateCardPreview() {
    AgileLifeTheme {
        val sampleTemplate = DayTemplate(
            id = "1",
            name = "Productive Workday",
            description = "Schedule for focused work days with regular breaks",
            activities = listOf(
                com.example.agilelifemanagement.domain.model.TemplateActivity(
                    title = "Morning Exercise",
                    description = "Morning cardio routine",
                    category = com.example.agilelifemanagement.domain.model.ActivityCategory(id = "exercise", name = "Category", color = "#808080"),
                    timeStart = "08:00",
                    timeEnd = "09:00",
                    id = java.util.UUID.randomUUID().toString()
                ),
                com.example.agilelifemanagement.domain.model.TemplateActivity(
                    title = "Team Standup",
                    description = "Daily team coordination",
                    category = com.example.agilelifemanagement.domain.model.ActivityCategory(id = "meeting", name = "Category", color = "#808080"),
                    timeStart = "09:30",
                    timeEnd = "10:00",
                    id = java.util.UUID.randomUUID().toString()
                ),
                com.example.agilelifemanagement.domain.model.TemplateActivity(
                    title = "Focus Time",
                    description = "Uninterrupted work session",
                    category = com.example.agilelifemanagement.domain.model.ActivityCategory(id = "focus", name = "Category", color = "#808080"),
                    timeStart = "10:30",
                    timeEnd = "12:15",
                    id = java.util.UUID.randomUUID().toString()
                )
            )
        )
        
        TemplateCard(
            template = sampleTemplate,
            onApplyClick = {},
            onEditClick = {},
            onDeleteClick = {}
        )
    }
}
