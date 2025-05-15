package com.example.agilelifemanagement.domain.model

/**
 * Domain model for Tag.
 * Represents a tag that can be associated with tasks, goals, and sprints.
 */
data class Tag(
    val id: String = "",
    val name: String,
    val color: String
)
