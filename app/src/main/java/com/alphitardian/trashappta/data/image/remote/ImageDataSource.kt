package com.alphitardian.trashappta.data.image.remote

import com.alphitardian.trashappta.data.image.remote.response.ImageResponse

interface ImageDataSource {
    suspend fun uploadImage(base64Data: String): ImageResponse
}