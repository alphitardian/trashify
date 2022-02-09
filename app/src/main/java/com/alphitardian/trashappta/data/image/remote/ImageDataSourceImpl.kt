package com.alphitardian.trashappta.data.image.remote

import com.alphitardian.trashappta.data.image.remote.network.ImageApi
import com.alphitardian.trashappta.data.image.remote.response.ImageResponse
import javax.inject.Inject

class ImageDataSourceImpl @Inject constructor(private val imageApi: ImageApi): ImageDataSource {
    override suspend fun uploadImage(base64Data: String): ImageResponse {
        return imageApi.uploadImage(base64Data = base64Data)
    }
}