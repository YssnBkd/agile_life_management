package com.example.agilelifemanagement.data.local.dao

import androidx.room.*
import com.example.agilelifemanagement.data.local.entity.TaskEntity
import com.example.agilelifemanagement.data.local.entity.TaskTagCrossRef
import com.example.agilelifemanagement.domain.model.TaskStatus
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

/**
 * Room DAO for accessing and manipulating task data in the database.
 * 
 * This DAO supports Material 3 Expressive design principles by providing:
 * - Reactive data streams using Flow for responsive UI updates
 * - Status-based filtering for visual state representation
 * - Priority-based sorting for visual hierarchy
 * - Tag-based grouping for visual organization
 * - Due date queries for timeline visualization
 */
@Dao
interface TaskDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTasks(tasks: List<TaskEntity>)
    
    @Update
    suspend fun updateTask(task: TaskEntity): Int
    
    @Query("UPDATE tasks SET status = :status WHERE id = :taskId")
    suspend fun updateTaskStatus(taskId: String, status: TaskStatus): Int
    
    @Query("DELETE FROM tasks WHERE id = :taskId")
    suspend fun deleteTask(taskId: String): Int
    
    @Query("SELECT * FROM tasks WHERE id = :taskId")
    suspend fun getTaskById(taskId: String): TaskEntity?
    
    @Query("SELECT * FROM tasks")
    fun getAllTasks(): Flow<List<TaskEntity>>
    
    @Query("SELECT * FROM tasks WHERE sprintId = :sprintId")
    fun getTasksBySprintId(sprintId: String): Flow<List<TaskEntity>>
    
    @Query("SELECT * FROM tasks WHERE dueDate = :date OR (dueDate IS NULL AND createdDate = :date)")
    fun getTasksByDate(date: LocalDate): Flow<List<TaskEntity>>
    
    @Query("SELECT * FROM tasks WHERE dueDate BETWEEN :startDate AND :endDate")
    fun getTasksByDateRange(startDate: LocalDate, endDate: LocalDate): Flow<List<TaskEntity>>
    
    // Task-Tag relationship operations
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTaskTagCrossRef(crossRef: TaskTagCrossRef)
    
    @Query("DELETE FROM task_tag_cross_ref WHERE taskId = :taskId")
    suspend fun deleteTaskTagCrossRefs(taskId: String)
    
    @Query("DELETE FROM task_tag_cross_ref WHERE taskId = :taskId AND tagId = :tagId")
    suspend fun deleteTaskTagCrossRef(taskId: String, tagId: String)
    
    @Transaction
    @Query("SELECT * FROM tasks WHERE id IN (SELECT taskId FROM task_tag_cross_ref WHERE tagId IN (:tagIds))")
    fun getTasksByTags(tagIds: List<String>): Flow<List<TaskEntity>>
    
    // Material 3 Dashboard and Analytics Support
    
    /**
     * Get tasks approaching their deadlines within a specified number of days.
     * Useful for displaying deadline proximity in Material 3 card components.
     */
    @Query("SELECT * FROM tasks WHERE status != 'COMPLETED' AND dueDate BETWEEN :today AND :futureDate ORDER BY dueDate")
    fun getTasksApproachingDeadline(today: LocalDate, futureDate: LocalDate): Flow<List<TaskEntity>>
    
    /**
     * Get overdue tasks for visual highlighting.
     */
    @Query("SELECT * FROM tasks WHERE status != 'COMPLETED' AND dueDate < :today ORDER BY dueDate")
    fun getOverdueTasks(today: LocalDate): Flow<List<TaskEntity>>
    
    /**
     * Get tasks by priority for visual hierarchy in Material 3 lists.
     */
    @Query("SELECT * FROM tasks WHERE priority = :priority ORDER BY dueDate")
    fun getTasksByPriority(priority: String): Flow<List<TaskEntity>>
    
    /**
     * Get task count by status for dashboard visualizations.
     * This result class is needed to properly map the SQL query results.
     */
    data class TaskStatusCount(val status: String, val count: Int)
    
    /**
     * Get task count by status for dashboard visualizations.
     * 
     * @return A Flow emitting a list of status counts
     */
    @Query("SELECT status, COUNT(*) as count FROM tasks GROUP BY status")
    fun getTaskCountByStatus(): Flow<List<TaskStatusCount>>
    
    /**
     * Get recently completed tasks for achievement displays.
     */
    @Query("SELECT * FROM tasks WHERE status = 'COMPLETED' ORDER BY dueDate DESC LIMIT :limit")
    fun getRecentlyCompletedTasks(limit: Int = 5): Flow<List<TaskEntity>>
    
    @Transaction
    suspend fun insertTaskWithTags(task: TaskEntity, tagIds: List<String>) {
        insertTask(task)
        tagIds.forEach { tagId ->
            insertTaskTagCrossRef(TaskTagCrossRef(task.id, tagId))
        }
    }
    
    @Transaction
    suspend fun updateTaskWithTags(task: TaskEntity, tagIds: List<String>) {
        updateTask(task)
        deleteTaskTagCrossRefs(task.id)
        tagIds.forEach { tagId ->
            insertTaskTagCrossRef(TaskTagCrossRef(task.id, tagId))
        }
    }
}
