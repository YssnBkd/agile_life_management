package com.example.agilelifemanagement.data.remote.model

import kotlinx.serialization.Serializable
import java.time.LocalDate

/**
 * Data Transfer Object for ActivityCategory entities in remote API communication.
 */
@Serializable
data class CategoryDto(
    val id: String = "",
    val name: String,
    val color: String,
    val icon: String? = null,
    val description: String = "",
    val createdDate: LocalDate,
    val modifiedDate: LocalDate
)
