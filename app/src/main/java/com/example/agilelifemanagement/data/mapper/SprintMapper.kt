package com.example.agilelifemanagement.data.mapper

import com.example.agilelifemanagement.data.local.entity.SprintEntity
import com.example.agilelifemanagement.domain.model.Sprint
import java.time.LocalDate
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
            startDate = entity.startDate,
            endDate = entity.endDate,
            goals = entity.goals,
            status = entity.status
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
            userId = "", // Default empty userId as it's required by entity but not in domain model
            name = domainModel.name,
            startDate = domainModel.startDate,
            endDate = domainModel.endDate,
            goals = domainModel.goals,
            status = domainModel.status,
            isCompleted = domainModel.status == com.example.agilelifemanagement.domain.model.SprintStatus.COMPLETED,
            updatedAt = LocalDate.now(),
            isActive = domainModel.status == com.example.agilelifemanagement.domain.model.SprintStatus.ACTIVE
        )
    }
}
