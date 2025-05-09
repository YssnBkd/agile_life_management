package com.example.agilelifemanagement.data.remote.dto

import com.example.agilelifemanagement.data.local.entity.DailyCheckupEntity
import kotlinx.serialization.Serializable

/**
 * Data Transfer Object for DailyCheckup in Supabase.
 */
@Serializable
data class DailyCheckupDto(
    val id: String,
    val date: Long,
    val sprint_id: String,
    val user_id: String,
    val notes: String? = null,
    val created_at: Long,
    val updated_at: Long
) {
    /**
     * Convert DTO to local entity.
     */
    fun toEntity(): DailyCheckupEntity {
        return DailyCheckupEntity(
            id = id,
            date = date,
            sprintId = sprint_id,
            userId = user_id,
            notes = notes,
            createdAt = created_at,
            updatedAt = updated_at
        )
    }
    
    companion object {
        /**
         * Convert local entity to DTO.
         */
        fun fromEntity(entity: DailyCheckupEntity): DailyCheckupDto {
            return DailyCheckupDto(
                id = entity.id,
                date = entity.date,
                sprint_id = entity.sprintId,
                user_id = entity.userId,
                notes = entity.notes,
                created_at = entity.createdAt,
                updated_at = entity.updatedAt
            )
        }
    }
}
