package com.example.agilelifemanagement.data.local.dao

import androidx.room.Dao
import androidx.room.Upsert

@Dao
interface UserDao {
    @Upsert
    suspend fun upsert(user: com.example.agilelifemanagement.data.local.entity.UserEntity)
}
