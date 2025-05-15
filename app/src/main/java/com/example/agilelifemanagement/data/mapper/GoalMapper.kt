package com.example.agilelifemanagement.data.mapper

import com.example.agilelifemanagement.data.local.entity.GoalEntity
import com.example.agilelifemanagement.domain.model.Goal
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Mapper class to convert between Goal domain model and GoalEntity data model.
 * This provides a clean separation between the domain and data layers.
 */
@Singleton
class GoalMapper @Inject constructor() {
    
    /**
     * Maps a GoalEntity from the data layer to a Goal domain model.
     * 
     * @param entity The GoalEntity to map from
     * @return The mapped Goal domain model
     */
    fun mapToDomain(entity: GoalEntity): Goal {
        return Goal(
            id = entity.id,
            title = entity.title,
            description = entity.description,
            status = entity.status,
            priority = entity.priority,
            dueDate = entity.dueDate,
            createdDate = entity.createdDate,
            modifiedDate = entity.modifiedDate,
            completedDate = entity.completedDate
        )
    }
    
    /**
     * Maps a Goal domain model to a GoalEntity for the data layer.
     * 
     * @param domainModel The Goal domain model to map from
     * @return The mapped GoalEntity
     */
    fun mapToEntity(domainModel: Goal): GoalEntity {
        return GoalEntity(
            id = domainModel.id,
            title = domainModel.title,
            description = domainModel.description,
            status = domainModel.status,
            priority = domainModel.priority,
            dueDate = domainModel.dueDate,
            createdDate = domainModel.createdDate,
            modifiedDate = domainModel.modifiedDate,
            completedDate = domainModel.completedDate
        )
    }
}
