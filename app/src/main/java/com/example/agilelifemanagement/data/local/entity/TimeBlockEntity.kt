package com.example.agilelifemanagement.data.local.entity


import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.agilelifemanagement.domain.model.TimeBlock
import com.example.agilelifemanagement.domain.model.TimeBlockCategory
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.UUID

/**
 * Room database entity representing a TimeBlock.
 * 
 * This entity follows Material 3 Expressive design principles by storing all data needed
 * to create visually consistent UI components with proper theming support.
 * 
 * Relationships:
 * - May be associated with a CategoryEntity through categoryId
 */
@Entity(
    tableName = "time_blocks",
    indices = [
        Index("category_id")
    ],
    foreignKeys = [
        ForeignKey(
            entity = ActivityCategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["category_id"],
            onDelete = ForeignKey.SET_NULL
        )
    ]
)
data class TimeBlockEntity(
    @PrimaryKey
    val id: String,
    
    val title: String,
    
    @ColumnInfo(defaultValue = "NULL")
    val description: String? = null,
    
    @ColumnInfo(defaultValue = "NULL")
    val location: String? = null,
    
    @ColumnInfo(name = "start_time")
    val startTime: String, // Store as formatted string "HH:mm"
    
    @ColumnInfo(name = "end_time")
    val endTime: String, // Store as formatted string "HH:mm"
    
    @ColumnInfo(name = "date")
    val date: String, // Store as formatted string "yyyy-MM-dd"
    
    @ColumnInfo(name = "time_range", defaultValue = "")
    val timeRange: String = "", // Combined time range for querying
    
    @ColumnInfo(name = "category_id", defaultValue = "TASK")
    val categoryId: String,
    
    @ColumnInfo(name = "color_hex")
    val colorHex: String = "#6750A4", // Default Material 3 primary color
    
    @ColumnInfo(name = "icon_name")
    val iconName: String = "star",
    
    @ColumnInfo(name = "is_completed", defaultValue = "0")
    val isCompleted: Boolean = false
) {
    /**
     * Returns the formatted time range string.
     * Used when mapping entity to domain model.
     * 
     * @return A formatted time range string (e.g., "9:00 AM - 10:00 AM")
     */
    fun getFormattedTimeRange(): String {
        return timeRange
    }
    
    /**
     * Parses the start time string to LocalTime.
     * Used when mapping entity to domain model.
     * 
     * @return The LocalTime representation of the start time
     */
    fun parseStartTime(): LocalTime {
        return try {
            LocalTime.parse(startTime)
        } catch (e: Exception) {
            LocalTime.now() // Fallback
        }
    }
    
    /**
     * Parses the end time string to LocalTime.
     * Used when mapping entity to domain model.
     * 
     * @return The LocalTime representation of the end time
     */
    fun parseEndTime(): LocalTime {
        return try {
            LocalTime.parse(endTime)
        } catch (e: Exception) {
            LocalTime.now().plusHours(1) // Fallback
        }
    }
    
    /**
     * Parses the date string to LocalDate.
     * Used when mapping entity to domain model.
     * 
     * @return The LocalDate representation of the date
     */
    fun parseDate(): LocalDate {
        return try {
            LocalDate.parse(date)
        } catch (e: Exception) {
            LocalDate.now() // Fallback
        }
    }
    
    /**
     * Maps the categoryId to the corresponding TimeBlockCategory.
     * Used when mapping entity to domain model.
     * 
     * @return The TimeBlockCategory enum value
     */
    fun mapToCategory(): TimeBlockCategory {
        return try {
            TimeBlockCategory.valueOf(categoryId)
        } catch (e: Exception) {
            // Default to TASK if the category is not recognized
            TimeBlockCategory.TASK
        }
    }
}
