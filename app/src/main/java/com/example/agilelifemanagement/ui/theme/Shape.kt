package com.example.agilelifemanagement.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

// Material 3 Expressive shapes with more generous roundness
// Expressive uses more dramatic curves and larger radius values
val Shapes = Shapes(
    // Small components like buttons, chips
    small = RoundedCornerShape(16.dp),
    
    // Medium components like cards, dialogs
    medium = RoundedCornerShape(24.dp),
    
    // Large components like bottom sheets, expanded cards
    large = RoundedCornerShape(28.dp),
    
    // Extra large components for feature highlighting
    extraLarge = RoundedCornerShape(36.dp)
)
