package com.example.agilelifemanagement.data.remote.serialization

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.contextual
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

/**
 * Serializers module for date and time types
 */
val dateTimeSerializersModule = SerializersModule {
    contextual(LocalDateSerializer)
    contextual(LocalDateTimeSerializer)
    contextual(LocalTimeSerializer)
}

/**
 * Serializer for LocalDate
 */
@Serializer(forClass = LocalDate::class)
object LocalDateSerializer : KSerializer<LocalDate> {
    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE

    override fun serialize(encoder: Encoder, value: LocalDate) {
        encoder.encodeString(value.format(formatter))
    }

    override fun deserialize(decoder: Decoder): LocalDate {
        return LocalDate.parse(decoder.decodeString(), formatter)
    }
}

/**
 * Serializer for LocalDateTime
 */
@Serializer(forClass = LocalDateTime::class)
object LocalDateTimeSerializer : KSerializer<LocalDateTime> {
    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

    override fun serialize(encoder: Encoder, value: LocalDateTime) {
        encoder.encodeString(value.format(formatter))
    }

    override fun deserialize(decoder: Decoder): LocalDateTime {
        return LocalDateTime.parse(decoder.decodeString(), formatter)
    }
}

/**
 * Serializer for LocalTime
 */
@Serializer(forClass = LocalTime::class)
object LocalTimeSerializer : KSerializer<LocalTime> {
    private val formatter = DateTimeFormatter.ISO_LOCAL_TIME

    override fun serialize(encoder: Encoder, value: LocalTime) {
        encoder.encodeString(value.format(formatter))
    }

    override fun deserialize(decoder: Decoder): LocalTime {
        return LocalTime.parse(decoder.decodeString(), formatter)
    }
}
