package com.example.agilelifemanagement.data.local.dao

import androidx.room.*
import com.example.agilelifemanagement.data.local.entity.TaskTagCrossRefEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskTagCrossRefDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(taskTagCrossRef: TaskTagCrossRefEntity)

    @Delete
    suspend fun delete(taskTagCrossRef: TaskTagCrossRefEntity)

    @Query("SELECT * FROM task_tag_cross_refs WHERE taskId = :taskId")
    fun getTagsForTask(taskId: String): Flow<List<TaskTagCrossRefEntity>>

    @Query("SELECT * FROM task_tag_cross_refs WHERE tagId = :tagId")
    fun getTasksForTag(tagId: String): Flow<List<TaskTagCrossRefEntity>>

    @Query("DELETE FROM task_tag_cross_refs WHERE taskId = :taskId")
    suspend fun deleteTagsForTask(taskId: String)

    @Query("DELETE FROM task_tag_cross_refs WHERE tagId = :tagId")
    suspend fun deleteTasksForTag(tagId: String)
    
    @Query("SELECT * FROM task_tag_cross_refs WHERE taskId = :taskId AND tagId = :tagId LIMIT 1")
    suspend fun getTaskTagCrossRef(taskId: String, tagId: String): TaskTagCrossRefEntity?
    
    @Query("DELETE FROM task_tag_cross_refs WHERE taskId = :taskId AND tagId = :tagId")
    suspend fun delete(taskId: String, tagId: String)
}
