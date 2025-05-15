package com.example.agilelifemanagement.domain.model

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * Enum representing task priority levels.
 * Maps to the Supabase ENUM 'agile_life.task_priority'.
 */
@Serializable(with = TaskPrioritySerializer::class)
enum class TaskPriority(val value: String) {
    LOW("LOW"),
    MEDIUM("MEDIUM"),
    HIGH("HIGH"),
    URGENT("URGENT");
    
    companion object {
        /**
         * Convert from integer index to TaskPriority.
         */
        fun fromInt(value: Int): TaskPriority = values().getOrElse(value) { MEDIUM }
        
        /**
         * Convert from string value to TaskPriority.
         */
        fun fromString(value: String): TaskPriority = values().find { it.value == value } ?: MEDIUM
    }
}

/**
 * Serializer for TaskPriority enum.
 * Handles conversion between Kotlin enum and Supabase string representation.
 */
object TaskPrioritySerializer : KSerializer<TaskPriority> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("TaskPriority", PrimitiveKind.STRING)
    
    override fun serialize(encoder: Encoder, value: TaskPriority) {
        encoder.encodeString(value.value)
    }
    
    override fun deserialize(decoder: Decoder): TaskPriority {
        return TaskPriority.fromString(decoder.decodeString())
    }
}
