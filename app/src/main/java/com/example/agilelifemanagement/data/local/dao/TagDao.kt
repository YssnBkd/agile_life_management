package com.example.agilelifemanagement.data.local.dao

import androidx.room.*
import com.example.agilelifemanagement.data.local.entity.TagEntity
import com.example.agilelifemanagement.data.local.entity.TaskTagCrossRef
import kotlinx.coroutines.flow.Flow

/**
 * Room DAO for accessing and manipulating tag data in the database.
 */
@Dao
interface TagDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTag(tag: TagEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTags(tags: List<TagEntity>)
    
    @Update
    suspend fun updateTag(tag: TagEntity): Int
    
    @Query("DELETE FROM tags WHERE id = :tagId")
    suspend fun deleteTag(tagId: String): Int
    
    @Query("SELECT * FROM tags WHERE id = :tagId")
    suspend fun getTagById(tagId: String): TagEntity?
    
    @Query("SELECT * FROM tags")
    fun getAllTags(): Flow<List<TagEntity>>
    
    @Transaction
    @Query("SELECT * FROM tags WHERE id IN (SELECT tagId FROM task_tag_cross_ref WHERE taskId = :taskId)")
    fun getTagsForTask(taskId: String): Flow<List<TagEntity>>
    
    @Query("SELECT * FROM tags WHERE name LIKE :query")
    fun searchTags(query: String): Flow<List<TagEntity>>
}
