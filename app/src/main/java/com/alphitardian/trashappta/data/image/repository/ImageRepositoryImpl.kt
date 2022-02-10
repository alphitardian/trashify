package com.alphitardian.trashappta.data.image.repository

import com.alphitardian.trashappta.data.image.local.ImageLocalDataSource
import com.alphitardian.trashappta.data.image.local.entity.ImageEntity
import com.alphitardian.trashappta.data.image.remote.ImageDataSource
import com.alphitardian.trashappta.data.image.remote.response.ImageResponse
import com.alphitardian.trashappta.domain.repository.ImageRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ImageRepositoryImpl @Inject constructor(
    private val remoteDataSource: ImageDataSource,
    private val localDataSource: ImageLocalDataSource,
    ) : ImageRepository {
    override suspend fun uploadImage(base64Data: String): ImageResponse {
       return remoteDataSource.uploadImage(base64Data)
    }

    override fun getImage(userId: String): Flow<ImageEntity> {
        return localDataSource.getImage(userId)
    }

    override suspend fun insertImage(imageEntity: ImageEntity) {
        localDataSource.insertImage(imageEntity)
    }
}