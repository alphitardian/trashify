package com.alphitardian.trashappta.data.image.remote.network

import com.alphitardian.trashappta.BuildConfig
import com.alphitardian.trashappta.data.image.remote.response.ImageResponse
import retrofit2.http.*

interface ImageApi {
    @FormUrlEncoded
    @POST("upload?")
    suspend fun uploadImage(
        @Query("key") key: String = BuildConfig.IMAGE_API_KEY,
        @Field("image") base64Data: String
    ): ImageResponse
}