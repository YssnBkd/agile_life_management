package com.example.agilelifemanagement.domain.model

/**
 * Domain model representing a template for day activities in the AgileLifeManagement app.
 * Templates allow users to create reusable activity definitions that can be scheduled multiple times.
 */
data class DayActivityTemplate(
    val id: String,
    val title: String,
    val description: String,
    val defaultDuration: Int, // Duration in minutes
    val categoryId: String
)
