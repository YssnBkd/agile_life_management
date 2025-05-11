package com.example.agilelifemanagement.auth.di

import android.content.Context
import com.example.agilelifemanagement.auth.data.AuthRemoteDataSource
import com.example.agilelifemanagement.auth.data.AuthLocalDataSource
import com.example.agilelifemanagement.auth.data.AuthRemoteDataSourceImpl
import com.example.agilelifemanagement.auth.data.AuthLocalDataSourceImpl
import com.example.agilelifemanagement.auth.domain.AuthRepository
import com.example.agilelifemanagement.auth.domain.AuthRepositoryImpl
import com.google.gson.Gson
import javax.inject.Singleton
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.hilt.android.qualifiers.ApplicationContext

@Module
@InstallIn(SingletonComponent::class)
object AuthModule {

    @Provides
    @Singleton
    fun provideAuthRepository(
        remoteDataSource: com.example.agilelifemanagement.auth.data.AuthRemoteDataSource,
        localDataSource: com.example.agilelifemanagement.auth.data.AuthLocalDataSource,
        userDao: com.example.agilelifemanagement.data.local.dao.UserDao
    ): com.example.agilelifemanagement.auth.domain.AuthRepository {
        return com.example.agilelifemanagement.auth.domain.AuthRepositoryImpl(
            remoteDataSource,
            localDataSource,
            userDao
        )
    }

    @Provides
    @Singleton
    fun provideAuthRemoteDataSource(
        supabaseManager: com.example.agilelifemanagement.data.remote.SupabaseManager
    ): AuthRemoteDataSource = AuthRemoteDataSourceImpl(supabaseManager)

    @Provides
    @Singleton
    fun provideGson(): Gson = Gson()

    @Provides
    @Singleton
    fun provideAuthLocalDataSource(
        @ApplicationContext context: Context,
        gson: Gson
    ): AuthLocalDataSource = AuthLocalDataSourceImpl(context, gson)
}

