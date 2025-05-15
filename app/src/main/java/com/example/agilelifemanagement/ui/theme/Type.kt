package com.example.agilelifemanagement.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Material 3 Expressive Typography
// More expressive with increased contrast between weights and sizes
// This creates better visual hierarchy and draws attention to important elements
val Typography = Typography(
    // Display styles - for very large text, now with more contrast
    displayLarge = TextStyle(
        fontWeight = FontWeight.SemiBold, // Increased weight for better visibility
        fontSize = 60.sp, // Larger for more impact
        lineHeight = 68.sp,
        letterSpacing = (-0.5).sp // More negative tracking for display text
    ),
    displayMedium = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 48.sp, // Increased for better hierarchy
        lineHeight = 56.sp,
        letterSpacing = (-0.25).sp
    ),
    displaySmall = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 38.sp, // Increased for better hierarchy
        lineHeight = 46.sp,
        letterSpacing = 0.sp
    ),
    
    // Headline styles - more expressive with stronger weights
    headlineLarge = TextStyle(
        fontWeight = FontWeight.Bold, // Bolder for stronger hierarchy
        fontSize = 34.sp, // Increased for better distinction
        lineHeight = 42.sp,
        letterSpacing = (-0.1).sp // Slightly tighter
    ),
    headlineMedium = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 30.sp, // Increased for better distinction
        lineHeight = 38.sp,
        letterSpacing = 0.sp
    ),
    headlineSmall = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 26.sp, // Increased for better distinction
        lineHeight = 34.sp,
        letterSpacing = 0.sp
    ),
    
    // Title styles - with more differentiation
    titleLarge = TextStyle(
        fontWeight = FontWeight.Bold, // Bolder for better prominence
        fontSize = 24.sp, // Larger for more impact
        lineHeight = 30.sp,
        letterSpacing = 0.sp
    ),
    titleMedium = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp, // Increased from standard M3
        lineHeight = 26.sp,
        letterSpacing = 0.15.sp
    ),
    titleSmall = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp, // Increased from standard M3
        lineHeight = 22.sp,
        letterSpacing = 0.1.sp
    ),
    
    // Body styles - slightly larger for better readability
    bodyLarge = TextStyle(
        fontWeight = FontWeight.Medium, // Slightly heavier for better readability
        fontSize = 17.sp, // Slightly larger
        lineHeight = 25.sp,
        letterSpacing = 0.4.sp // Slightly tighter
    ),
    bodyMedium = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp, // Slightly larger
        lineHeight = 21.sp,
        letterSpacing = 0.25.sp
    ),
    bodySmall = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 13.sp, // Slightly larger
        lineHeight = 17.sp,
        letterSpacing = 0.4.sp
    ),
    
    // Label styles - bolder for better visibility of interactive elements
    labelLarge = TextStyle(
        fontWeight = FontWeight.SemiBold, // Bolder for better visibility
        fontSize = 15.sp, // Slightly larger
        lineHeight = 21.sp,
        letterSpacing = 0.1.sp
    ),
    labelMedium = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 13.sp, // Slightly larger
        lineHeight = 17.sp,
        letterSpacing = 0.5.sp
    ),
    labelSmall = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp, // Slightly larger
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
)
