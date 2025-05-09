package com.example.agilelifemanagement.domain.model

import java.time.LocalDate

/**
 * Domain model for Goal.
 * Represents a goal that can have multiple tasks associated with it.
 */
data class Goal(
    val id: String = "",
    val title: String,
    val summary: String = "",
    val description: List<String> = emptyList(),
    val category: Category = Category.PERSONAL,
    val deadline: LocalDate? = null,
    val progress: Float = 0f, // 0.0 to 1.0
    val isCompleted: Boolean = false
) {
    enum class Category {
        PERSONAL, PROFESSIONAL, HEALTH, FINANCIAL, LEARNING, OTHER
    }
}
