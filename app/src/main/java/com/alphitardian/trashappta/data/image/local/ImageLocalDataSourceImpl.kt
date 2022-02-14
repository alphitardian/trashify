package com.alphitardian.trashappta.data.image.local

import com.alphitardian.trashappta.data.image.local.dao.ImageDao
import com.alphitardian.trashappta.data.image.local.entity.ImageEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ImageLocalDataSourceImpl @Inject constructor(private val imageDao: ImageDao): ImageLocalDataSource {
    override suspend fun insertImage(imageEntity: ImageEntity) {
        imageDao.insertImage(imageEntity)
    }

    override fun getImage(id: String): Flow<ImageEntity> {
        return imageDao.getImage(id)
    }

    override suspend fun deleteImage(id: String) {
        return imageDao.deleteImage(id)
    }
}