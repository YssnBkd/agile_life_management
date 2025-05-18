package com.example.agilelifemanagement.domain.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import java.time.LocalTime
import java.util.UUID

/**
 * Domain model representing a time block in the application.
 * 
 * This model follows Material 3 Expressive design principles by providing:
 * - Visual representation properties (color, icon)
 * - Consistent categorization
 * - Support for timeline visualization
 */
data class TimeBlock(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String = "",
    val location: String = "",
    val startTime: LocalTime,
    val endTime: LocalTime? = null,
    val timeRange: String = "",  // Legacy field, keeping for compatibility
    val categoryId: String = "",
    val category: TimeBlockCategory = TimeBlockCategory.TASK,
    val color: Color = category.color,
    val icon: ImageVector = Icons.Filled.Star,
    val isCompleted: Boolean = false
)
