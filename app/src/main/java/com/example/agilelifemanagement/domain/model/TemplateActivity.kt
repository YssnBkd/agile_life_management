package com.example.agilelifemanagement.domain.model

/**
 * Represents an activity within a day template.
 */
data class TemplateActivity(
    val id: String,
    val title: String,
    val timeStart: String,
    val timeEnd: String,
    val category: ActivityCategory = ActivityCategory(id = "other", name = "Other", color = "#808080"),
    val description: String = ""
)
