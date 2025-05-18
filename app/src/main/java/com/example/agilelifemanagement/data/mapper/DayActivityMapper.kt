package com.example.agilelifemanagement.data.mapper

import com.example.agilelifemanagement.data.local.entity.DayActivityEntity
import com.example.agilelifemanagement.domain.model.DayActivity
import java.time.LocalTime
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Mapper for converting between [DayActivity] domain model and [DayActivityEntity] data model.
 */
@Singleton
class DayActivityMapper @Inject constructor() {
    
    /**
     * Maps a data layer entity to a domain layer model.
     * @param entity The entity to map from
     * @return The mapped domain model
     */
    fun mapToDomain(entity: DayActivityEntity): DayActivity {
        return DayActivity(
            id = entity.id,
            title = entity.title,
            description = entity.description,
            date = entity.date,
            scheduledTime = entity.scheduledTime,
            duration = entity.duration,
            completed = entity.completed,
            categoryId = entity.categoryId ?: ""
        )
    }
    
    /**
     * Maps a domain layer model to a data layer entity.
     * @param domainModel The domain model to map from
     * @return The mapped entity
     */
    fun mapToEntity(domainModel: DayActivity): DayActivityEntity {
        return DayActivityEntity(
            id = domainModel.id,
            title = domainModel.title,
            description = domainModel.description,
            date = domainModel.date,
            scheduledTime = domainModel.scheduledTime,
            duration = domainModel.duration,
            completed = domainModel.completed,
            categoryId = domainModel.categoryId
        )
    }
}
