package com.example.agilelifemanagement.data.local.converter

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.room.TypeConverter

/**
 * Type converters for Room database to handle Compose UI Color type.
 * These converters allow Room to store Color objects by converting
 * them to/from Integer color values.
 * 
 * Follows Material 3 Expressive design principles for color representation.
 */
class ColorConverters {
    @TypeConverter
    fun fromColor(color: Color?): Int? {
        return color?.toArgb()
    }

    @TypeConverter
    fun toColor(colorInt: Int?): Color? {
        return colorInt?.let { Color(it) }
    }
    
    // Helper function to convert color to hex string (not used by Room directly)
    fun colorToHex(color: Color): String {
        return String.format("#%08X", color.toArgb())
    }
    
    // Helper function to convert hex string to color (not used by Room directly)
    fun hexToColor(hex: String): Color {
        return try {
            Color(android.graphics.Color.parseColor(hex))
        } catch (e: Exception) {
            // Default to primary color if parsing fails
            Color(0xFF6750A4) // Material 3 default primary
        }
    }
}
