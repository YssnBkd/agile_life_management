package com.example.agilelifemanagement.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * Custom shape definitions for the Agile Life Management app.
 */
val Shapes = Shapes(
    // Small components like buttons, chips, and small cards
    small = RoundedCornerShape(4.dp),
    
    // Medium components like medium cards, dialogs
    medium = RoundedCornerShape(8.dp),
    
    // Large components like large cards, bottom sheets
    large = RoundedCornerShape(16.dp),
    
    // Extra large components like full-screen dialogs
    extraLarge = RoundedCornerShape(24.dp)
)
