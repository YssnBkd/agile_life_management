package com.example.agilelifemanagement.data.mapper

import com.example.agilelifemanagement.data.local.entity.SprintEntity
import com.example.agilelifemanagement.domain.model.Sprint
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Mapper class to convert between Sprint domain model and SprintEntity data model.
 * This provides a clean separation between the domain and data layers.
 */
@Singleton
class SprintMapper @Inject constructor() {
    
    /**
     * Maps a SprintEntity from the data layer to a Sprint domain model.
     * 
     * @param entity The SprintEntity to map from
     * @return The mapped Sprint domain model
     */
    fun mapToDomain(entity: SprintEntity): Sprint {
        return Sprint(
            id = entity.id,
            name = entity.name,
            goal = entity.goal,
            startDate = entity.startDate,
            endDate = entity.endDate,
            status = entity.status,
            createdDate = entity.createdDate,
            modifiedDate = entity.modifiedDate
        )
    }
    
    /**
     * Maps a Sprint domain model to a SprintEntity for the data layer.
     * 
     * @param domainModel The Sprint domain model to map from
     * @return The mapped SprintEntity
     */
    fun mapToEntity(domainModel: Sprint): SprintEntity {
        return SprintEntity(
            id = domainModel.id,
            name = domainModel.name,
            goal = domainModel.goal,
            startDate = domainModel.startDate,
            endDate = domainModel.endDate,
            status = domainModel.status,
            createdDate = domainModel.createdDate,
            modifiedDate = domainModel.modifiedDate
        )
    }
}
