package com.alphitardian.trashappta.data.image.repository

import com.alphitardian.trashappta.data.image.remote.ImageDataSource
import com.alphitardian.trashappta.data.image.remote.response.ImageResponse
import com.alphitardian.trashappta.domain.repository.ImageRepository
import javax.inject.Inject

class ImageRepositoryImpl @Inject constructor(private val dataSource: ImageDataSource) : ImageRepository {
    override suspend fun uploadImage(base64Data: String): ImageResponse {
       return dataSource.uploadImage(base64Data)
    }
}