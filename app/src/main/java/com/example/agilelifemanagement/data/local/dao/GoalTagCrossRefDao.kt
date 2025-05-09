package com.example.agilelifemanagement.data.local.dao

import androidx.room.*
import com.example.agilelifemanagement.data.local.entity.GoalTagCrossRefEntity

@Dao
interface GoalTagCrossRefDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(crossRef: GoalTagCrossRefEntity)

    @Query("SELECT * FROM goal_tag_cross_refs WHERE goalId = :goalId")
    suspend fun getTagsForGoal(goalId: String): List<GoalTagCrossRefEntity>

    @Query("SELECT * FROM goal_tag_cross_refs WHERE tagId = :tagId")
    suspend fun getGoalsForTag(tagId: String): List<GoalTagCrossRefEntity>

    @Query("SELECT * FROM goal_tag_cross_refs WHERE goalId = :goalId AND tagId = :tagId")
    suspend fun getGoalTagCrossRef(goalId: String, tagId: String): GoalTagCrossRefEntity?

    @Query("DELETE FROM goal_tag_cross_refs WHERE goalId = :goalId AND tagId = :tagId")
    suspend fun delete(goalId: String, tagId: String)
}
