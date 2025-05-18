package com.example.agilelifemanagement.data.remote.model

import com.example.agilelifemanagement.domain.model.Tag
import kotlinx.serialization.Serializable

/**
 * Data Transfer Object for Tag entities used in API communication.
 */
@Serializable
data class TagDto(
    val id: String,
    val name: String,
    val color: String
)

/**
 * Extension function to convert a [TagDto] to a domain [Tag].
 */
fun TagDto.toDomain(): Tag = Tag(
    id = id,
    name = name,
    color = color
)

/**
 * Extension function to convert a domain [Tag] to a [TagDto].
 */
fun Tag.toDto(): TagDto = TagDto(
    id = id,
    name = name,
    color = color
)
