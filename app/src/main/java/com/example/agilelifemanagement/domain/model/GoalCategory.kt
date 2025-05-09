package com.example.agilelifemanagement.domain.model

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * Enum representing goal categories.
 * Maps to the Supabase ENUM 'agile_life.goal_category'.
 */
@Serializable(with = GoalCategorySerializer::class)
enum class GoalCategory(val value: String) {
    PERSONAL("PERSONAL"),
    PROFESSIONAL("PROFESSIONAL"),
    HEALTH("HEALTH"),
    FINANCIAL("FINANCIAL"),
    LEARNING("LEARNING"),
    OTHER("OTHER");
    
    companion object {
        /**
         * Convert from integer index to GoalCategory.
         */
        fun fromInt(value: Int): GoalCategory = values().getOrElse(value) { OTHER }
        
        /**
         * Convert from string value to GoalCategory.
         */
        fun fromString(value: String): GoalCategory = values().find { it.value == value } ?: OTHER
    }
}

/**
 * Serializer for GoalCategory enum.
 * Handles conversion between Kotlin enum and Supabase string representation.
 */
object GoalCategorySerializer : KSerializer<GoalCategory> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("GoalCategory", PrimitiveKind.STRING)
    
    override fun serialize(encoder: Encoder, value: GoalCategory) {
        encoder.encodeString(value.value)
    }
    
    override fun deserialize(decoder: Decoder): GoalCategory {
        return GoalCategory.fromString(decoder.decodeString())
    }
}
