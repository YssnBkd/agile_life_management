package com.example.agilelifemanagement.data.local.converter

import androidx.room.TypeConverter
import java.util.UUID

/**
 * Type converter for Room database to handle UUID type.
 * These converters allow Room to store UUID objects by converting
 * them to/from String representations.
 */
class UUIDConverter {
    @TypeConverter
    fun fromUUID(uuid: UUID?): String? {
        return uuid?.toString()
    }

    @TypeConverter
    fun toUUID(uuidString: String?): UUID? {
        return uuidString?.let { UUID.fromString(it) }
    }
}
