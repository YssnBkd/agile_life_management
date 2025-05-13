package com.example.agilelifemanagement.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.agilelifemanagement.domain.model.Goal
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

/**
 * Displays a category badge with appropriate colors for a goal category
 */
@Composable
fun CategoryBadge(category: Goal.Category) {
    val (backgroundColor, textColor) = when (category) {
        Goal.Category.PERSONAL -> Pair(Color(0xFFE1F5FE), Color(0xFF0288D1))
        Goal.Category.PROFESSIONAL -> Pair(Color(0xFFE8F5E9), Color(0xFF388E3C))
        Goal.Category.HEALTH -> Pair(Color(0xFFF3E5F5), Color(0xFF7B1FA2))
        Goal.Category.FINANCIAL -> Pair(Color(0xFFFFF8E1), Color(0xFFFFA000))
        Goal.Category.LEARNING -> Pair(Color(0xFFE0F2F1), Color(0xFF00796B))
        Goal.Category.OTHER -> Pair(Color(0xFFEFEBE9), Color(0xFF5D4037))
    }
    
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = backgroundColor
    ) {
        Text(
            text = category.name.lowercase().replaceFirstChar { it.uppercase() },
            color = textColor,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

/**
 * Displays a deadline badge with appropriate colors based on days remaining
 */
@Composable
fun DeadlineBadge(daysUntil: Long, deadline: LocalDate, dateFormatter: DateTimeFormatter) {
    val (backgroundColor, textColor) = when {
        daysUntil < 0 -> Pair(Color(0xFFFFEBEE), Color(0xFFD32F2F)) // Overdue
        daysUntil < 3 -> Pair(Color(0xFFFFF3E0), Color(0xFFEF6C00)) // Soon
        daysUntil < 7 -> Pair(Color(0xFFFFFDE7), Color(0xFFFBC02D)) // This week
        else -> Pair(Color(0xFFF1F8E9), Color(0xFF689F38)) // Later
    }
    
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = backgroundColor
    ) {
        Text(
            text = when {
                daysUntil < 0 -> "Overdue"
                daysUntil == 0L -> "Today"
                daysUntil == 1L -> "Tomorrow"
                daysUntil < 7 -> "$daysUntil days"
                else -> deadline.format(dateFormatter)
            },
            color = textColor,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}
