package com.example.agilelifemanagement.data.local.dao

import androidx.room.*
import com.example.agilelifemanagement.data.local.entity.GoalEntity
import com.example.agilelifemanagement.domain.model.GoalStatus
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

/**
 * Room DAO for accessing and manipulating goal data in the database.
 */
@Dao
interface GoalDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoal(goal: GoalEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoals(goals: List<GoalEntity>)
    
    @Update
    suspend fun updateGoal(goal: GoalEntity): Int
    
    @Query("UPDATE goals SET status = :status WHERE id = :goalId")
    suspend fun updateGoalStatus(goalId: String, status: GoalStatus): Int
    
    @Query("DELETE FROM goals WHERE id = :goalId")
    suspend fun deleteGoal(goalId: String): Int
    
    @Query("SELECT * FROM goals WHERE id = :goalId")
    suspend fun getGoalById(goalId: String): GoalEntity?
    
    @Query("SELECT * FROM goals")
    fun getAllGoals(): Flow<List<GoalEntity>>
    
    @Query("SELECT * FROM goals WHERE status = :status")
    fun getGoalsByStatus(status: GoalStatus): Flow<List<GoalEntity>>
    
    @Query("SELECT * FROM goals WHERE deadline <= :date AND status != 'COMPLETED'")
    fun getUpcomingGoals(date: LocalDate): Flow<List<GoalEntity>>
}
