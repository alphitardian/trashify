package com.alphitardian.trashappta.data.image.remote.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ImageResponse(
    @SerialName("data")
    val data: ImageDataResponse,

    @SerialName("success")
    val success: Boolean,

    @SerialName("status")
    val status: Int
)

@Serializable
data class ImageDataResponse(
    @SerialName("id")
    val id: String,

    @SerialName("title")
    val title: String,

    @SerialName("url_viewer")
    val urlViewer: String,

    @SerialName("url")
    val url: String,

    @SerialName("display_url")
    val displayUrl: String,

    @SerialName("size")
    val size: Int,

    @SerialName("time")
    val time: String,

    @SerialName("expiration")
    val expiration: String,

    @SerialName("is_360")
    val is360: Int? = null,

    @SerialName("image")
    val image: ImageData,

    @SerialName("thumb")
    val thumb: ImageData,

    @SerialName("medium")
    val medium: ImageData,

    @SerialName("delete_url")
    val deleteUrl: String,
)

@Serializable
data class ImageData(
    @SerialName("filename")
    val fileName: String,

    @SerialName("name")
    val name: String,

    @SerialName("mime")
    val mime: String,

    @SerialName("extension")
    val extension: String,

    @SerialName("url")
    val url: String
)
