package com.example.agilelifemanagement.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.agilelifemanagement.data.local.entity.GoalEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GoalDao {
    @Query("SELECT * FROM goals ORDER BY deadline ASC")
    fun getAllGoals(): Flow<List<GoalEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoal(goal: GoalEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(goal: GoalEntity)

    @Update
    suspend fun updateGoal(goal: GoalEntity)

    @Query("DELETE FROM goals WHERE id = :id")
    suspend fun deleteGoalById(id: String)

    @Query("DELETE FROM goals WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("SELECT * FROM goals WHERE id = :id")
    fun getGoalById(id: String): Flow<GoalEntity?>

    @Query("SELECT * FROM goals WHERE category = :category")
    fun getGoalsByCategory(category: Int): Flow<List<GoalEntity>>

    @Query("SELECT * FROM goals WHERE deadline = :deadlineTimestamp")
    fun getGoalsByDeadline(deadlineTimestamp: Long): Flow<List<GoalEntity>>

    @Query("SELECT goals.* FROM goals INNER JOIN goal_sprint_cross_refs ON goals.id = goal_sprint_cross_refs.goalId WHERE goal_sprint_cross_refs.sprintId = :sprintId")
    fun getGoalsBySprintId(sprintId: String): Flow<List<GoalEntity>>
}
