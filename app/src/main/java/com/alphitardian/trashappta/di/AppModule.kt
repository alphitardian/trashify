package com.alphitardian.trashappta.di

import android.content.Context
import com.alphitardian.trashappta.data.datastore.AppDatastore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Singleton
    @Provides
    fun provideDatastore(@ApplicationContext context: Context): AppDatastore = AppDatastore(context)
}