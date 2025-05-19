package com.example.agilelifemanagement.ui.components.cards

import androidx.compose.ui.graphics.Color

/**
 * UI representation of task priorities with associated colors
 * that follow Material 3 Expressive design principles.
 */
enum class TaskPriority(val color: Color) {
    LOW(Color(0xFF4CAF50)),     // Green
    MEDIUM(Color(0xFFFFC107)),  // Amber
    HIGH(Color(0xFFFF9800)),    // Orange
    CRITICAL(Color(0xFFF44336)) // Red
}
