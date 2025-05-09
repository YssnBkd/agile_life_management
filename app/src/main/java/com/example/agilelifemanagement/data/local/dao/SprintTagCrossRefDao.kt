package com.example.agilelifemanagement.data.local.dao

import androidx.room.*
import com.example.agilelifemanagement.data.local.entity.SprintTagCrossRefEntity

@Dao
interface SprintTagCrossRefDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(crossRef: SprintTagCrossRefEntity)

    @Query("SELECT * FROM sprint_tag_cross_refs WHERE sprintId = :sprintId")
    suspend fun getTagsForSprint(sprintId: String): List<SprintTagCrossRefEntity>

    @Query("SELECT * FROM sprint_tag_cross_refs WHERE tagId = :tagId")
    suspend fun getSprintsForTag(tagId: String): List<SprintTagCrossRefEntity>

    @Query("SELECT * FROM sprint_tag_cross_refs WHERE sprintId = :sprintId AND tagId = :tagId")
    suspend fun getSprintTagCrossRef(sprintId: String, tagId: String): SprintTagCrossRefEntity?

    @Query("DELETE FROM sprint_tag_cross_refs WHERE sprintId = :sprintId AND tagId = :tagId")
    suspend fun delete(sprintId: String, tagId: String)
}
