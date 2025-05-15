package com.example.agilelifemanagement.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity for activity categories.
 */
@Entity(tableName = "activity_categories")
data class ActivityCategoryEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val color: Int,
    val syncedAt: Long? = null
)
