package com.alphitardian.trashappta.di

import com.alphitardian.trashappta.data.image.local.ImageLocalDataSource
import com.alphitardian.trashappta.data.image.local.ImageLocalDataSourceImpl
import com.alphitardian.trashappta.data.image.remote.ImageDataSource
import com.alphitardian.trashappta.data.image.remote.ImageDataSourceImpl
import com.alphitardian.trashappta.data.maps.MapsDataSource
import com.alphitardian.trashappta.data.maps.MapsDataSourceImpl
import com.alphitardian.trashappta.data.quiz.remote.QuizDataSource
import com.alphitardian.trashappta.data.quiz.remote.QuizRemoteDataSource
import com.alphitardian.trashappta.data.user.remote.UserDataSource
import com.alphitardian.trashappta.data.user.remote.UserRemoteDataSourceImpl
import com.alphitardian.trashappta.data.waste.remote.WasteDataSource
import com.alphitardian.trashappta.data.waste.remote.WasteRemoteDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class DataSourceModule {
    @Binds
    abstract fun bindUserRemoteDataSource(userRemoteDataSource: UserRemoteDataSourceImpl): UserDataSource

    @Binds
    abstract fun bindWasteRemoteDataSource(wasteRemoteRemoteDataSource: WasteRemoteDataSourceImpl): WasteDataSource

    @Binds
    abstract fun bindQuizRemoteDataSource(quizRemoteDataSource: QuizRemoteDataSource): QuizDataSource

    @Binds
    abstract fun bindMapsDataSource(mapsDataSource: MapsDataSourceImpl): MapsDataSource

    @Binds
    abstract fun bindImageRemoteDataSource(imageDataSource: ImageDataSourceImpl): ImageDataSource

    @Binds
    abstract fun bindImageLocalDataSource(imageDataSource: ImageLocalDataSourceImpl): ImageLocalDataSource
}