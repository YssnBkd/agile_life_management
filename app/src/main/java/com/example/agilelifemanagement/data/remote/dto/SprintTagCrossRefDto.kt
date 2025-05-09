package com.example.agilelifemanagement.data.remote.dto

import com.example.agilelifemanagement.data.local.entity.SprintTagCrossRefEntity
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneOffset

/**
 * Data Transfer Object for SprintTagCrossRef in Supabase.
 * Represents the many-to-many relationship between sprints and tags.
 */
@Serializable
data class SprintTagCrossRefDto(
    @SerialName("id") val id: String,
    @SerialName("sprint_id") val sprint_id: String,
    @SerialName("tag_id") val tag_id: String,
    @SerialName("created_at") val created_at: Long
) {
    /**
     * Convert DTO to local entity.
     */
    fun toEntity(): SprintTagCrossRefEntity {
        return SprintTagCrossRefEntity(
            id = id,
            sprintId = sprint_id,
            tagId = tag_id,
            createdAt = OffsetDateTime.ofInstant(Instant.ofEpochSecond(created_at), ZoneOffset.UTC)
        )
    }
    
    companion object {
        /**
         * Convert local entity to DTO.
         */
        fun fromEntity(entity: SprintTagCrossRefEntity): SprintTagCrossRefDto {
            return SprintTagCrossRefDto(
                id = entity.id,
                sprint_id = entity.sprintId,
                tag_id = entity.tagId,
                created_at = entity.createdAt.toEpochSecond()
            )
        }
    }
}
