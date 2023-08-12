package com.f0x1d.dnsmanager.di

import android.content.Context
import androidx.room.Room
import com.f0x1d.dnsmanager.database.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RoomModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext content: Context) = Room
        .databaseBuilder(content, AppDatabase::class.java, "database")
        .build()
}