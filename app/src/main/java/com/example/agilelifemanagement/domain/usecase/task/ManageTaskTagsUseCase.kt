package com.example.agilelifemanagement.domain.usecase.task

import com.example.agilelifemanagement.domain.model.Result
import com.example.agilelifemanagement.domain.repository.TagRepository
import com.example.agilelifemanagement.domain.repository.TaskRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/**
 * Use case for managing tags associated with a task.
 */
class ManageTaskTagsUseCase @Inject constructor(
    private val taskRepository: TaskRepository,
    private val tagRepository: TagRepository
) {
    /**
     * Add a tag to a task with validation.
     *
     * @param taskId The ID of the task.
     * @param tagId The ID of the tag to add.
     * @return Result indicating success or an error.
     */
    suspend fun addTag(taskId: String, tagId: String): Result<Unit> {
        return try {
            // Validate task
            val task = taskRepository.getTaskById(taskId).first()
                ?: return Result.Error("Task not found")
            
            // Validate tag
            val tag = tagRepository.getTagById(tagId).first()
                ?: return Result.Error("Tag not found")
            
            // Check if tag is already added
            if (tagId in task.tags) {
                return Result.Success(Unit) // Already added, no action needed
            }
            
            // Add tag to task
            taskRepository.addTagToTask(taskId, tagId)
            
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error("Failed to add tag to task: ${e.message}", e)
        }
    }
    
    /**
     * Remove a tag from a task.
     *
     * @param taskId The ID of the task.
     * @param tagId The ID of the tag to remove.
     * @return Result indicating success or an error.
     */
    suspend fun removeTag(taskId: String, tagId: String): Result<Unit> {
        return try {
            // Validate task
            val task = taskRepository.getTaskById(taskId).first()
                ?: return Result.Error("Task not found")
            
            // Check if tag is associated with the task
            if (tagId !in task.tags) {
                return Result.Success(Unit) // Not associated, no action needed
            }
            
            // Remove tag from task
            taskRepository.removeTagFromTask(taskId, tagId)
            
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error("Failed to remove tag from task: ${e.message}", e)
        }
    }
    
    /**
     * Update all tags for a task (replace existing tags with new ones).
     *
     * @param taskId The ID of the task.
     * @param tagIds The list of tag IDs to associate with the task.
     * @return Result indicating success or an error.
     */
    suspend fun updateTags(taskId: String, tagIds: List<String>): Result<Unit> {
        return try {
            // Validate task
            val task = taskRepository.getTaskById(taskId).first()
                ?: return Result.Error("Task not found")
            
            // Validate all tags
            for (tagId in tagIds) {
                val tag = tagRepository.getTagById(tagId).first()
                    ?: return Result.Error("Tag with ID $tagId not found")
            }
            
            // Get current tags
            val currentTags = task.tags
            
            // Remove tags that are no longer associated
            currentTags.filter { it !in tagIds }.forEach { tagId ->
                taskRepository.removeTagFromTask(taskId, tagId)
            }
            
            // Add new tags
            tagIds.filter { it !in currentTags }.forEach { tagId ->
                taskRepository.addTagToTask(taskId, tagId)
            }
            
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error("Failed to update task tags: ${e.message}", e)
        }
    }
}
