package com.example.agilelifemanagement.domain.model

import java.time.LocalDate

/**
 * Domain model representing a sprint review in the AgileLifeManagement app.
 * Sprint reviews are conducted at the end of a sprint to assess performance and capture learnings.
 */
data class SprintReview(
    val id: String,
    val sprintId: String,
    val completionRate: Float, // Percentage from 0.0 to 1.0
    val lessonsLearned: List<String>,
    val date: LocalDate
)
