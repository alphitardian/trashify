package com.alphitardian.trashappta.di

import android.content.Context
import androidx.room.Room
import com.alphitardian.trashappta.data.database.TrashDatabase
import com.alphitardian.trashappta.data.image.local.dao.ImageDao
import com.alphitardian.trashappta.data.waste.local.dao.WasteDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): TrashDatabase {
        return Room.databaseBuilder(
            context,
            TrashDatabase::class.java,
            "trash_database"
        ).build()
    }

    @Provides
    @Singleton
    fun provideWasteDao(database: TrashDatabase): WasteDao = database.wasteDao()

    @Provides
    @Singleton
    fun provideImageDao(database: TrashDatabase): ImageDao = database.imageDao()
}