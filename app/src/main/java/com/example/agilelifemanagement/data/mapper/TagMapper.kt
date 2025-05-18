package com.example.agilelifemanagement.data.mapper

import com.example.agilelifemanagement.data.local.entity.TagEntity
import com.example.agilelifemanagement.domain.model.Tag
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Mapper for converting between [Tag] domain model and [TagEntity] data model.
 */
@Singleton
class TagMapper @Inject constructor() {
    
    /**
     * Maps a data layer entity to a domain layer model.
     * @param entity The entity to map from
     * @return The mapped domain model
     */
    fun mapToDomain(entity: TagEntity): Tag {
        return Tag(
            id = entity.id,
            name = entity.name,
            color = entity.color
        )
    }
    
    /**
     * Maps a domain layer model to a data layer entity.
     * @param domainModel The domain model to map from
     * @return The mapped entity
     */
    fun mapToEntity(domainModel: Tag): TagEntity {
        return TagEntity(
            id = domainModel.id,
            userId = "current_user", // This should be properly injected in a production implementation
            name = domainModel.name,
            color = domainModel.color
        )
    }
}
