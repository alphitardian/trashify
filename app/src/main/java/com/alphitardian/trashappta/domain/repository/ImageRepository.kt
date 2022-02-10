package com.alphitardian.trashappta.domain.repository

import com.alphitardian.trashappta.data.image.local.entity.ImageEntity
import com.alphitardian.trashappta.data.image.remote.response.ImageResponse
import kotlinx.coroutines.flow.Flow

interface ImageRepository {
    suspend fun uploadImage(base64Data: String): ImageResponse
    fun getImage(userId: String): Flow<ImageEntity>
    suspend fun insertImage(imageEntity: ImageEntity)
}