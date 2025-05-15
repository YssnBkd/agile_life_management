package com.example.agilelifemanagement.data.mappers

import com.example.agilelifemanagement.data.local.entity.SprintReviewEntity
import com.example.agilelifemanagement.domain.model.SprintReview
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

fun SprintReviewEntity.toSprintReview(): SprintReview = SprintReview(
    id = id,
    sprintId = sprintId,
    date = Instant.ofEpochMilli(date).atZone(ZoneId.systemDefault()).toLocalDate(),
    rating = rating
)
