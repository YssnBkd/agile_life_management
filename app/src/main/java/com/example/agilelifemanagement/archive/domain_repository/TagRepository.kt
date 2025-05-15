package com.example.agilelifemanagement.domain.repository

import com.example.agilelifemanagement.domain.model.Tag
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for Tag operations.
 */
interface TagRepository {
    fun getTags(): Flow<List<Tag>>
    fun getTagById(id: String): Flow<Tag?>
    fun getTagsByTaskId(taskId: String): Flow<List<Tag>>
    fun getTagsByGoalId(goalId: String): Flow<List<Tag>>
    fun getTagsBySprintId(sprintId: String): Flow<List<Tag>>
    suspend fun insertTag(tag: Tag): String
    suspend fun updateTag(tag: Tag)
    suspend fun deleteTag(id: String)
}
