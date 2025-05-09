package com.example.agilelifemanagement.data.local.dao

import androidx.room.*
import com.example.agilelifemanagement.data.local.entity.TaskGoalCrossRefEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskGoalCrossRefDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(taskGoalCrossRef: TaskGoalCrossRefEntity)

    @Delete
    suspend fun delete(taskGoalCrossRef: TaskGoalCrossRefEntity)

    @Query("SELECT * FROM task_goal_cross_refs WHERE taskId = :taskId")
    fun getGoalsForTask(taskId: String): Flow<List<TaskGoalCrossRefEntity>>

    @Query("SELECT * FROM task_goal_cross_refs WHERE goalId = :goalId")
    fun getTasksForGoal(goalId: String): Flow<List<TaskGoalCrossRefEntity>>

    @Query("DELETE FROM task_goal_cross_refs WHERE taskId = :taskId")
    suspend fun deleteGoalsForTask(taskId: String)

    @Query("DELETE FROM task_goal_cross_refs WHERE goalId = :goalId")
    suspend fun deleteTasksForGoal(goalId: String)
}
