package com.example.agilelifemanagement.data.mapper

import com.example.agilelifemanagement.data.local.entity.TaskEntity
import com.example.agilelifemanagement.domain.model.Task
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Mapper class to convert between Task domain model and TaskEntity data model.
 * This provides a clean separation between the domain and data layers.
 */
@Singleton
class TaskMapper @Inject constructor() {
    
    /**
     * Maps a TaskEntity from the data layer to a Task domain model.
     * 
     * @param entity The TaskEntity to map from
     * @return The mapped Task domain model
     */
    fun mapToDomain(entity: TaskEntity): Task {
        return Task(
            id = entity.id,
            title = entity.title,
            description = entity.description,
            status = entity.status,
            priority = entity.priority,
            dueDate = entity.dueDate,
            createdDate = entity.createdDate,
            modifiedDate = entity.modifiedDate,
            completedDate = entity.completedDate,
            sprintId = entity.sprintId,
            goalId = entity.goalId
        )
    }
    
    /**
     * Maps a Task domain model to a TaskEntity for the data layer.
     * 
     * @param domainModel The Task domain model to map from
     * @return The mapped TaskEntity
     */
    fun mapToEntity(domainModel: Task): TaskEntity {
        return TaskEntity(
            id = domainModel.id,
            title = domainModel.title,
            description = domainModel.description,
            status = domainModel.status,
            priority = domainModel.priority,
            dueDate = domainModel.dueDate,
            createdDate = domainModel.createdDate,
            modifiedDate = domainModel.modifiedDate,
            completedDate = domainModel.completedDate,
            sprintId = domainModel.sprintId,
            goalId = domainModel.goalId
        )
    }
}
