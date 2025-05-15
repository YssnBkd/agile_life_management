package com.example.agilelifemanagement.data.mapper

import com.example.agilelifemanagement.data.local.entity.ActivityCategoryEntity
import com.example.agilelifemanagement.domain.model.ActivityCategory
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Mapper for converting between [ActivityCategory] domain model and [ActivityCategoryEntity] data model.
 */
@Singleton
class CategoryMapper @Inject constructor() {
    
    /**
     * Maps a data layer entity to a domain layer model.
     * @param entity The entity to map from
     * @return The mapped domain model
     */
    fun mapToDomain(entity: ActivityCategoryEntity): ActivityCategory {
        return ActivityCategory(
            id = entity.id,
            name = entity.name,
            color = entity.color,
            icon = entity.icon,
            description = entity.description
        )
    }
    
    /**
     * Maps a domain layer model to a data layer entity.
     * @param domainModel The domain model to map from
     * @return The mapped entity
     */
    fun mapToEntity(domainModel: ActivityCategory): ActivityCategoryEntity {
        return ActivityCategoryEntity(
            id = domainModel.id,
            name = domainModel.name,
            color = domainModel.color,
            icon = domainModel.icon,
            description = domainModel.description
        )
    }
}
