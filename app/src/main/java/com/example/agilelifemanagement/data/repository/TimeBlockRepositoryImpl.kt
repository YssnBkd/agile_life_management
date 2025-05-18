package com.example.agilelifemanagement.data.repository

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Work
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.agilelifemanagement.data.local.dao.TimeBlockDao
import com.example.agilelifemanagement.data.local.entity.TimeBlockEntity
import com.example.agilelifemanagement.domain.model.TimeBlock
import com.example.agilelifemanagement.domain.repository.TimeBlockRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.util.UUID
import javax.inject.Inject

/**
 * Implementation of [TimeBlockRepository] that uses Room database as the data source.
 * 
 * This implementation follows Material 3 Expressive design principles by providing:
 * - Reactive data streams using Flow for real-time UI updates
 * - Proper error handling with Result type
 * - Clean mapping between domain and data layers
 * - Support for Material 3 color system
 */
class TimeBlockRepositoryImpl @Inject constructor(
    private val timeBlockDao: TimeBlockDao
) : TimeBlockRepository {

    /**
     * Maps a TimeBlockEntity to a TimeBlock domain model.
     */
    private fun mapEntityToDomain(entity: TimeBlockEntity): TimeBlock {
        return TimeBlock(
            id = entity.id,
            title = entity.title,
            description = entity.description.orEmpty(),
            location = entity.location.orEmpty(),
            startTime = LocalTime.parse(entity.startTime),
            endTime = entity.endTime?.let { LocalTime.parse(it) },
            timeRange = entity.getFormattedTimeRange(), // Legacy support
            categoryId = entity.categoryId,
            category = entity.mapToCategory(),
            color = parseColorFromHex(entity.colorHex),
            icon = parseIconFromName(entity.iconName),
            isCompleted = entity.isCompleted
        )
    }

    /**
     * Maps a TimeBlock domain model to a TimeBlockEntity.
     */
    private fun mapDomainToEntity(domainModel: TimeBlock): TimeBlockEntity {
        return TimeBlockEntity(
            id = domainModel.id,
            title = domainModel.title,
            description = if (domainModel.description.isBlank()) null else domainModel.description,
            location = if (domainModel.location.isBlank()) null else domainModel.location,
            startTime = formatTimeAsString(domainModel.startTime),
            endTime = domainModel.endTime?.let { formatTimeAsString(it) } ?: formatTimeAsString(domainModel.startTime.plusHours(1)),
            date = formatDateAsString(parseDateFromDomainModel(domainModel)),
            categoryId = domainModel.categoryId.ifEmpty { domainModel.category.name },
            colorHex = colorToHex(domainModel.color),
            iconName = iconToName(domainModel.icon),
            isCompleted = domainModel.isCompleted
        )
    }
    
    /**
     * Converts a Color object to its hex string representation. // weird
     */
    private fun colorToHex(color: Color): String {
        val r = (color.red * 255).toInt().toString(16).padStart(2, '0')
        val g = (color.green * 255).toInt().toString(16).padStart(2, '0')
        val b = (color.blue * 255).toInt().toString(16).padStart(2, '0')
        return "#$r$g$b"
    }
    
    /**
     * Converts an ImageVector to its corresponding name string.
     */
    private fun iconToName(icon: ImageVector): String {
        return when (icon) {
            Icons.Filled.Star -> "star"
            Icons.Filled.Work -> "work"
            Icons.Filled.Home -> "home"
            Icons.Filled.School -> "school"
            Icons.Filled.FitnessCenter -> "fitness"
            Icons.Filled.Restaurant -> "restaurant"
            Icons.Filled.ShoppingCart -> "shopping_cart"
            Icons.Filled.LocalHospital -> "local_hospital"
            Icons.Filled.Favorite -> "favorite"
            Icons.Filled.Spa -> "spa"
            Icons.Filled.DirectionsRun -> "directions_run"
            // Add more icon mappings as needed
            else -> "star" // Default icon
        }
    }
    
    /**
     * Parses a color from its hex representation.
     */
    private fun parseColorFromHex(colorHex: String): Color {
        return try {
            val hex = colorHex.removePrefix("#")
            val r = hex.substring(0, 2).toInt(16) / 255f
            val g = hex.substring(2, 4).toInt(16) / 255f
            val b = hex.substring(4, 6).toInt(16) / 255f
            Color(r, g, b)
        } catch (e: Exception) {
            Color(0xFF6750A4) // Default Material 3 primary color
        }
    }
    
    /**
     * Parses an icon from its name representation.
     */
    private fun parseIconFromName(iconName: String): ImageVector {
        return when (iconName) {
            "work" -> Icons.Filled.Work
            "home" -> Icons.Filled.Home
            "school" -> Icons.Filled.School
            "fitness" -> Icons.Filled.FitnessCenter
            "restaurant" -> Icons.Filled.Restaurant
            "shopping_cart" -> Icons.Filled.ShoppingCart
            "local_hospital" -> Icons.Filled.LocalHospital
            "favorite" -> Icons.Filled.Favorite
            "spa" -> Icons.Filled.Spa
            "directions_run" -> Icons.Filled.DirectionsRun
            // Add more icon mappings as needed
            else -> Icons.Filled.Star // Default icon
        }
    }
    
    /**
     * Parse the start time from a time range string.
     */
    private fun parseStartTimeFromRange(timeRange: String): LocalTime {
        val parts = timeRange.split(" - ")
        return try {
            LocalTime.parse(parts[0], DateTimeFormatter.ofPattern("h:mm a"))
        } catch (e: Exception) {
            LocalTime.now() // Fallback to current time if parsing fails
        }
    }
    
    /**
     * Parse the end time from a time range string.
     */
    private fun parseEndTimeFromRange(timeRange: String): LocalTime {
        val parts = timeRange.split(" - ")
        return try {
            if (parts.size > 1) {
                LocalTime.parse(parts[1], DateTimeFormatter.ofPattern("h:mm a"))
            } else {
                // If no end time in string, assume 1 hour duration
                parseStartTimeFromRange(timeRange).plusHours(1)
            }
        } catch (e: Exception) {
            LocalTime.now().plusHours(1) // Fallback to current time + 1hr if parsing fails
        }
    }
    
    /**
     * Extract date from domain model, falling back to current date if needed.
     * This is used when mapping domain model to entity.
     */
    private fun parseDateFromDomainModel(domainModel: TimeBlock): LocalDate {
        // Assuming date information is not directly in TimeBlock
        // In a real implementation, you'd have a more robust way to get the date
        return LocalDate.now()
    }
    
    /**
     * Format a LocalTime to a standard ISO time string (HH:mm).
     */
    private fun formatTimeAsString(time: LocalTime): String {
        return time.format(DateTimeFormatter.ofPattern("HH:mm"))
    }
    
    /**
     * Format a LocalDate to a standard ISO date string (yyyy-MM-dd).
     */
    private fun formatDateAsString(date: LocalDate): String {
        return date.format(DateTimeFormatter.ISO_LOCAL_DATE)
    }

    override fun getAllTimeBlocks(): Flow<List<TimeBlock>> {
        return timeBlockDao.getAllTimeBlocks().map { entities ->
            entities.map { mapEntityToDomain(it) }
        }
    }

    override fun getTimeBlocksByDate(date: LocalDate): Flow<List<TimeBlock>> {
        val dateString = formatDateAsString(date)
        return timeBlockDao.getTimeBlocksByDate(dateString).map { entities ->
            entities.map { mapEntityToDomain(it) }
        }
    }

    override fun getTimeBlocksForDateRange(startDate: LocalDate, endDate: LocalDate): Flow<List<TimeBlock>> {
        val startDateString = formatDateAsString(startDate)
        val endDateString = formatDateAsString(endDate)
        return timeBlockDao.getTimeBlocksForDateRange(startDateString, endDateString).map { entities ->
            entities.map { mapEntityToDomain(it) }
        }
    }

    override suspend fun getTimeBlockById(timeBlockId: String): Result<TimeBlock> {
        return try {
            val entity = timeBlockDao.getTimeBlockById(timeBlockId) ?: throw Exception("TimeBlock not found")
            Result.success(mapEntityToDomain(entity))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getTimeBlocksByCategory(categoryId: String): Flow<List<TimeBlock>> {
        return timeBlockDao.getTimeBlocksByCategory(categoryId).map { entities ->
            entities.map { mapEntityToDomain(it) }
        }
    }

    override suspend fun addTimeBlock(timeBlock: TimeBlock): Result<TimeBlock> {
        return try {
            val entity = mapDomainToEntity(timeBlock)
            val id = timeBlockDao.insertTimeBlock(entity)
            val updatedTimeBlock = timeBlock.copy(id = entity.id)
            Result.success(updatedTimeBlock)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateTimeBlock(timeBlock: TimeBlock): Result<TimeBlock> {
        return try {
            val entity = mapDomainToEntity(timeBlock)
            timeBlockDao.updateTimeBlock(entity)
            Result.success(timeBlock)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateTimeBlockCompletion(timeBlockId: String, completed: Boolean): Result<TimeBlock> {
        return try {
            timeBlockDao.updateTimeBlockCompletion(timeBlockId, completed)
            getTimeBlockById(timeBlockId) // Return the updated time block
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteTimeBlock(timeBlockId: String): Result<Boolean> {
        return try {
            timeBlockDao.deleteTimeBlock(timeBlockId)
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getTimeBlocksByCategories(): Flow<Map<String, List<TimeBlock>>> {
        return getAllTimeBlocks().map { timeBlocks ->
            timeBlocks.groupBy { it.category.name }
        }
    }

    override fun getTimeBlockCountByCategory(): Flow<Map<String, Int>> {
        return timeBlockDao.getTimeBlockCountByCategory().map { categoryCounts ->
            categoryCounts.associate { it.category_id to it.count }
        }
    }
}
