package com.example.agilelifemanagement.data.mapper

import com.example.agilelifemanagement.data.local.entity.DayActivityTemplateEntity
import com.example.agilelifemanagement.domain.model.DayActivityTemplate
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Mapper for converting between [DayActivityTemplate] domain model and [DayActivityTemplateEntity] data model.
 */
@Singleton
class DayActivityTemplateMapper @Inject constructor() {
    
    /**
     * Maps a data layer entity to a domain layer model.
     * @param entity The entity to map from
     * @return The mapped domain model
     */
    fun mapToDomain(entity: DayActivityTemplateEntity): DayActivityTemplate {
        return DayActivityTemplate(
            id = entity.id,
            title = entity.title,
            description = entity.description,
            defaultDuration = entity.defaultDuration,
            categoryId = entity.categoryId ?: ""
        )
    }
    
    /**
     * Maps a domain layer model to a data layer entity.
     * @param domainModel The domain model to map from
     * @return The mapped entity
     */
    fun mapToEntity(domainModel: DayActivityTemplate): DayActivityTemplateEntity {
        return DayActivityTemplateEntity(
            id = domainModel.id,
            title = domainModel.title,
            description = domainModel.description,
            defaultDuration = domainModel.defaultDuration,
            categoryId = domainModel.categoryId
        )
    }
}
