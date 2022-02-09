package com.alphitardian.trashappta.domain.repository

import com.alphitardian.trashappta.data.image.remote.response.ImageResponse

interface ImageRepository {
    suspend fun uploadImage(base64Data: String): ImageResponse
}