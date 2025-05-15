package com.example.agilelifemanagement.data.local.dao

import androidx.room.*
import com.example.agilelifemanagement.data.local.entity.TaskSprintCrossRefEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskSprintCrossRefDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(taskSprintCrossRef: TaskSprintCrossRefEntity)

    @Delete
    suspend fun delete(taskSprintCrossRef: TaskSprintCrossRefEntity)

    @Query("SELECT * FROM task_sprint_cross_refs WHERE taskId = :taskId")
    fun getSprintsForTask(taskId: String): Flow<List<TaskSprintCrossRefEntity>>

    @Query("SELECT * FROM task_sprint_cross_refs WHERE sprintId = :sprintId")
    fun getTasksForSprint(sprintId: String): Flow<List<TaskSprintCrossRefEntity>>

    @Query("DELETE FROM task_sprint_cross_refs WHERE taskId = :taskId")
    suspend fun deleteSprintsForTask(taskId: String)

    @Query("DELETE FROM task_sprint_cross_refs WHERE sprintId = :sprintId")
    suspend fun deleteTasksForSprint(sprintId: String)
    
    @Query("SELECT * FROM task_sprint_cross_refs WHERE taskId = :taskId AND sprintId = :sprintId LIMIT 1")
    suspend fun getTaskSprintCrossRef(taskId: String, sprintId: String): TaskSprintCrossRefEntity?
    
    @Query("DELETE FROM task_sprint_cross_refs WHERE taskId = :taskId AND sprintId = :sprintId")
    suspend fun delete(taskId: String, sprintId: String)
}
