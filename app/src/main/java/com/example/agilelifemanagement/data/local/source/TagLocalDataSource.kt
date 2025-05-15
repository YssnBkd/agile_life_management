package com.example.agilelifemanagement.data.local.source

import com.example.agilelifemanagement.data.local.dao.TagDao
import com.example.agilelifemanagement.data.local.entity.TagEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Local data source for tags.
 * Uses Room DAO to perform database operations.
 */
class TagLocalDataSource @Inject constructor(
    private val tagDao: TagDao
) {
    /**
     * Get all tags as an observable flow.
     */
    fun observeTags(): Flow<List<TagEntity>> = tagDao.getAllTags()
    
    /**
     * Get tags for a specific task.
     */
    fun observeTagsForTask(taskId: String): Flow<List<TagEntity>> = 
        tagDao.getTagsForTask(taskId)
    
    /**
     * Search tags by name.
     */
    fun searchTags(query: String): Flow<List<TagEntity>> = 
        tagDao.searchTags("%$query%")
    
    /**
     * Get a specific tag by ID.
     */
    suspend fun getTagById(tagId: String): TagEntity? = 
        tagDao.getTagById(tagId)
    
    /**
     * Insert a tag.
     */
    suspend fun insertTag(tag: TagEntity) {
        tagDao.insertTag(tag)
    }
    
    /**
     * Insert multiple tags.
     */
    suspend fun insertTags(tags: List<TagEntity>) {
        tagDao.insertTags(tags)
    }
    
    /**
     * Update a tag.
     */
    suspend fun updateTag(tag: TagEntity): Int =
        tagDao.updateTag(tag)
    
    /**
     * Delete a tag.
     */
    suspend fun deleteTag(tagId: String): Int =
        tagDao.deleteTag(tagId)
}
