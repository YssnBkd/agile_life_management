package com.example.agilelifemanagement.data.mappers

import com.example.agilelifemanagement.data.local.entity.SprintEntity
import com.example.agilelifemanagement.domain.model.Sprint

fun SprintEntity.toSprint(): Sprint = Sprint(
    id = id,
    name = name,
    summary = summary ?: "",
    description = description ?: emptyList(),
    startDate = java.time.Instant.ofEpochMilli(startDate).atZone(java.time.ZoneId.systemDefault()).toLocalDate(),
    endDate = java.time.Instant.ofEpochMilli(endDate).atZone(java.time.ZoneId.systemDefault()).toLocalDate(),
    isActive = isActive,
    isCompleted = isCompleted
    // Add other fields as needed
)
