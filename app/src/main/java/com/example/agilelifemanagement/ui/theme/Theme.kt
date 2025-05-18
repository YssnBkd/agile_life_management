package com.example.agilelifemanagement.ui.theme

import androidx.compose.ui.graphics.Color

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/**
 * Material 3 Expressive color scheme for light theme
 * Using more vibrant colors with greater contrast between elements
 * for better usability and emotional impact
 */
private val LightColorScheme = lightColorScheme(
    primary = AgileGreen,
    onPrimary = Color.White,
    primaryContainer = AgileGreenLight,
    onPrimaryContainer = AgileGreenDark,
    
    secondary = AgilePurple,
    onSecondary = Color.White,
    secondaryContainer = AgilePurpleLight,
    onSecondaryContainer = AgilePurpleDark,
    
    tertiary = AgileBlue,
    onTertiary = Color.White,
    tertiaryContainer = AgileBlueLight,
    onTertiaryContainer = AgileBlueDark,
    
    error = ErrorRed,
    onError = Color.White,
    
    background = Surface1,
    onBackground = Gray900,
    
    surface = Surface1,
    onSurface = Gray900,
    surfaceVariant = Surface2,
    onSurfaceVariant = Gray700,
    
    // Additional container colors for Material 3 Expressive layering
    surfaceTint = AgileGreen.copy(alpha = 0.1f),
    surfaceContainerLowest = SurfaceContainerLowest,
    surfaceContainerLow = SurfaceContainerLow,
    surfaceContainer = SurfaceContainer,
    surfaceContainerHigh = SurfaceContainerHigh,
    surfaceContainerHighest = SurfaceContainerHighest,
    
    outline = Gray400,
    outlineVariant = Gray300
)

/**
 * Material 3 Expressive color scheme for dark theme
 * Using more vibrant colors with greater contrast between elements
 * for better usability and emotional impact
 */
private val DarkColorScheme = darkColorScheme(
    primary = AgileGreenLight,
    onPrimary = AgileGreenDark,
    primaryContainer = AgileGreen,
    onPrimaryContainer = AgileGreenLight,
    
    secondary = AgilePurpleLight,
    onSecondary = AgilePurpleDark,
    secondaryContainer = AgilePurple,
    onSecondaryContainer = AgilePurpleLight,
    
    tertiary = AgileBlueLight,
    onTertiary = AgileBlueDark,
    tertiaryContainer = AgileBlue,
    onTertiaryContainer = AgileBlueLight,
    
    error = ErrorRed,
    onError = Color.White,
    
    background = Gray900,
    onBackground = Gray100,
    
    surface = Gray900,
    onSurface = Gray100,
    surfaceVariant = Gray800,
    onSurfaceVariant = Gray300,
    
    // Additional container colors for Material 3 Expressive layering
    surfaceTint = AgileGreenLight.copy(alpha = 0.1f),
    surfaceContainerLowest = Gray900,
    surfaceContainerLow = Gray800,
    surfaceContainer = Gray800.copy(alpha = 0.8f),
    surfaceContainerHigh = Gray700,
    surfaceContainerHighest = Gray700.copy(alpha = 0.8f),
    
    outline = Gray600,
    outlineVariant = Gray700
)

/**
 * Extended color palette for Material 3 Expressive
 * Provides additional accent colors beyond the base Material 3 color scheme
 */
data class ExtendedColorPalette(
    val accentMint: Color,
    val accentCoral: Color,
    val accentAqua: Color,
    val accentSunflower: Color,
    val accentLavender: Color,
    val sprintActive: Color,
    val sprintPlanned: Color,
    val sprintCompleted: Color,
    val priorityLow: Color,
    val priorityMedium: Color,
    val priorityHigh: Color,
    val priorityCritical: Color,
    val moodVeryBad: Color,
    val moodBad: Color,
    val moodNeutral: Color,
    val moodGood: Color,
    val moodVeryGood: Color
)

// Light theme extended colors
private val LightExtendedColors = ExtendedColorPalette(
    accentMint = AccentMint,
    accentCoral = AccentCoral,
    accentAqua = Color(0xFF00B4D8), // Light blue/aqua color,
    accentSunflower = AccentSunflower,
    accentLavender = AccentLavender,
    sprintActive = SprintActive,
    sprintPlanned = SprintPlanned,
    sprintCompleted = SprintCompleted,
    priorityLow = PriorityLow,
    priorityMedium = PriorityMedium,
    priorityHigh = PriorityHigh,
    priorityCritical = PriorityCritical,
    moodVeryBad = MoodVeryBad,
    moodBad = MoodBad,
    moodNeutral = MoodNeutral,
    moodGood = MoodGood,
    moodVeryGood = MoodVeryGood
)

// Dark theme extended colors
private val DarkExtendedColors = ExtendedColorPalette(
    accentMint = AccentMint,
    accentCoral = AccentCoral,
    accentAqua = Color(0xFF00B4D8), // Same light blue/aqua color for dark theme
    accentSunflower = AccentSunflower,
    accentLavender = AccentLavender,
    sprintActive = SprintActive,
    sprintPlanned = SprintPlanned,
    sprintCompleted = SprintCompleted,
    priorityLow = PriorityLow,
    priorityMedium = PriorityMedium,
    priorityHigh = PriorityHigh,
    priorityCritical = PriorityCritical,
    moodVeryBad = MoodVeryBad,
    moodBad = MoodBad,
    moodNeutral = MoodNeutral,
    moodGood = MoodGood,
    moodVeryGood = MoodVeryGood
)

// CompositionLocal to provide extended colors throughout the app
val LocalExtendedColors = staticCompositionLocalOf { LightExtendedColors }

/**
 * Material 3 Expressive theme implementation
 * Implements a more visually distinctive design system with:  
 * - More vibrant colors with greater contrast
 * - More generous shapes with larger corner radii
 * - More expressive typography with better visual hierarchy
 * - Extended color palette for richer UI semantics
 * - Sophisticated motion and animation patterns
 */
@Composable
fun AgileLifeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    // Allow high contrast mode for accessibility
    highContrast: Boolean = false,
    content: @Composable () -> Unit
) {
    // Determine the appropriate color scheme based on theme, device settings, and preferences
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            // Start with dynamic color scheme
            val dynamicScheme = if (darkTheme) {
                dynamicDarkColorScheme(context)
            } else {
                dynamicLightColorScheme(context)
            }
            
            // Apply high contrast adjustments if needed
            if (highContrast) {
                applyHighContrastAdjustments(dynamicScheme, darkTheme)
            } else {
                dynamicScheme
            }
        }
        // Use static color schemes as fallback
        darkTheme -> {
            if (highContrast) applyHighContrastAdjustments(DarkColorScheme, true) else DarkColorScheme
        }
        else -> {
            if (highContrast) applyHighContrastAdjustments(LightColorScheme, false) else LightColorScheme
        }
    }
    
    // Extended colors selection based on theme
    val extendedColors = if (darkTheme) DarkExtendedColors else LightExtendedColors
    
    // Apply modern system UI treatments
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            
            // Use surface colors for status bar instead of primary for a more cohesive look
            // This is more aligned with Material 3 Expressive guidelines
            window.statusBarColor = if (darkTheme) {
                colorScheme.surfaceContainerHighest.toArgb()
            } else {
                colorScheme.surfaceContainer.toArgb()
            }
            
            // Configure status bar text color based on background brightness
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
            
            // Enable edge-to-edge design
            WindowCompat.setDecorFitsSystemWindows(window, false)
        }
    }

    // Provide extended colors through CompositionLocal
    CompositionLocalProvider(LocalExtendedColors provides extendedColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            shapes = Shapes,
            content = content
        )
    }
}

/**
 * Applies high contrast adjustments to a color scheme for improved accessibility
 */
private fun applyHighContrastAdjustments(baseScheme: ColorScheme, isDark: Boolean): ColorScheme {
    return baseScheme.copy(
        // Increase contrast between background and content
        surface = if (isDark) Color.Black else Color.White,
        background = if (isDark) Color.Black else Color.White,
        // Make primary more vibrant
        primary = if (isDark) {
            baseScheme.primary.copy(alpha = 1f)
        } else {
            baseScheme.primary.copy(alpha = 1f)
        },
        // Make error color more prominent
        error = if (isDark) {
            Color(0xFFFF6E6E)
        } else {
            Color(0xFFD50000)
        }
    )
}

/**
 * Extend Material Theme with our custom color extensions
 */
object AgileLifeTheme {
    val extendedColors: ExtendedColorPalette
        @Composable
        get() = LocalExtendedColors.current
}
