package com.example.agilelifemanagement.data.mappers

import com.example.agilelifemanagement.data.local.entity.SprintReviewEntity
import com.example.agilelifemanagement.data.remote.dto.SprintReviewDto

fun SprintReviewDto.toSprintReviewEntity(): SprintReviewEntity = SprintReviewEntity(
    id = id,
    sprintId = sprint_id,
    date = date,
    rating = rating,
    userId = user_id,
    createdAt = created_at,
    updatedAt = updated_at
)
