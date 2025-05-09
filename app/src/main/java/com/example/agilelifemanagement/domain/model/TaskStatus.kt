package com.example.agilelifemanagement.domain.model

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * Enum representing task status values.
 * Maps to the Supabase ENUM 'agile_life.task_status'.
 */
@Serializable(with = TaskStatusSerializer::class)
enum class TaskStatus(val value: String) {
    BACKLOG("BACKLOG"),
    TODO("TODO"),
    IN_PROGRESS("IN_PROGRESS"),
    BLOCKED("BLOCKED"),
    REVIEW("REVIEW"),
    DONE("DONE");
    
    companion object {
        /**
         * Convert from integer index to TaskStatus.
         */
        fun fromInt(value: Int): TaskStatus = values().getOrElse(value) { TODO }
        
        /**
         * Convert from string value to TaskStatus.
         */
        fun fromString(value: String): TaskStatus = values().find { it.value == value } ?: TODO
    }
}

/**
 * Serializer for TaskStatus enum.
 * Handles conversion between Kotlin enum and Supabase string representation.
 */
object TaskStatusSerializer : KSerializer<TaskStatus> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("TaskStatus", PrimitiveKind.STRING)
    
    override fun serialize(encoder: Encoder, value: TaskStatus) {
        encoder.encodeString(value.value)
    }
    
    override fun deserialize(decoder: Decoder): TaskStatus {
        return TaskStatus.fromString(decoder.decodeString())
    }
}
