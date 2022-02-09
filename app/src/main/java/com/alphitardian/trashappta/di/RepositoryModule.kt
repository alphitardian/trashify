package com.alphitardian.trashappta.di

import com.alphitardian.trashappta.data.image.repository.ImageRepositoryImpl
import com.alphitardian.trashappta.data.maps.MapsRepositoryImpl
import com.alphitardian.trashappta.data.quiz.repository.QuizRepositoryImpl
import com.alphitardian.trashappta.data.user.repository.UserRepositoryImpl
import com.alphitardian.trashappta.data.waste.repository.WasteRepositoryImpl
import com.alphitardian.trashappta.domain.repository.*
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    abstract fun bindUserRepository(userRepository: UserRepositoryImpl): UserRepository

    @Binds
    abstract fun bindWasteRepository(wasteRepository: WasteRepositoryImpl): WasteRepository

    @Binds
    abstract fun bindQuizRepository(quizRepository: QuizRepositoryImpl): QuizRepository

    @Binds
    abstract fun bindMapsRepository(mapsRepository: MapsRepositoryImpl): MapsRepository

    @Binds
    abstract fun bindImageRepository(imageRepository: ImageRepositoryImpl): ImageRepository
}