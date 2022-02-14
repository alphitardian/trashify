package com.alphitardian.trashappta.data.image.local

import com.alphitardian.trashappta.data.image.local.entity.ImageEntity
import kotlinx.coroutines.flow.Flow

interface ImageLocalDataSource {
    suspend fun insertImage(imageEntity: ImageEntity)
    fun getImage(id: String): Flow<ImageEntity>
    suspend fun deleteImage(id: String)
}