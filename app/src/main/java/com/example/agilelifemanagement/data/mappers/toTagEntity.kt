package com.example.agilelifemanagement.data.mappers

import com.example.agilelifemanagement.data.local.entity.TagEntity
import com.example.agilelifemanagement.domain.model.Tag

fun Tag.toTagEntity(userId: String, createdAt: Long): TagEntity = TagEntity(
    id = id,
    name = name,
    color = color,
    userId = userId,
    createdAt = createdAt
)
