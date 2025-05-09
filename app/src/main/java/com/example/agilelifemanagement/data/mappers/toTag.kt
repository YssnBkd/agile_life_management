package com.example.agilelifemanagement.data.mappers

import com.example.agilelifemanagement.data.local.entity.TagEntity
import com.example.agilelifemanagement.domain.model.Tag

fun TagEntity.toTag(): Tag = Tag(
    id = id,
    name = name,
    color = color
)
