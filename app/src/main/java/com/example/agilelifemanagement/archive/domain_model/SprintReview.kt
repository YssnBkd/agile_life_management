package com.example.agilelifemanagement.domain.model

import java.time.LocalDate

/**
 * Domain model for SprintReview.
 * Represents a sprint review for evaluating sprint outcomes and planning improvements.
 */
data class SprintReview(
    val id: String = "",
    val sprintId: String,
    val date: LocalDate,
    val completedGoals: List<String> = emptyList(),
    val incompleteGoals: List<String> = emptyList(),
    val whatWentWell: List<String> = emptyList(),
    val whatCouldImprove: List<String> = emptyList(),
    val actionItems: List<String> = emptyList(),
    val rating: Int = 0 // 1-5 rating for the sprint
)
