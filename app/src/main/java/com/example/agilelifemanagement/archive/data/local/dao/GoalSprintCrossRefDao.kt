package com.example.agilelifemanagement.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.agilelifemanagement.data.local.entity.GoalSprintCrossRefEntity

@Dao
interface GoalSprintCrossRefDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(crossRef: GoalSprintCrossRefEntity)

    @Query("SELECT * FROM goal_sprint_cross_refs WHERE goalId = :goalId")
    suspend fun getSprintsForGoal(goalId: String): List<GoalSprintCrossRefEntity>

    @Query("SELECT * FROM goal_sprint_cross_refs WHERE sprintId = :sprintId")
    suspend fun getGoalsForSprint(sprintId: String): List<GoalSprintCrossRefEntity>

    @Query("SELECT * FROM goal_sprint_cross_refs WHERE goalId = :goalId AND sprintId = :sprintId")
    suspend fun getGoalSprintCrossRef(goalId: String, sprintId: String): GoalSprintCrossRefEntity?

    @Query("DELETE FROM goal_sprint_cross_refs WHERE goalId = :goalId AND sprintId = :sprintId")
    suspend fun delete(goalId: String, sprintId: String)
}
