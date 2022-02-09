package com.alphitardian.trashappta.data.user.remote.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(
    @SerialName("success")
    val success: Boolean,

    @SerialName("message")
    val message: String,

    @SerialName("data")
    val data: LoginDataResponse,

    @SerialName("timestamp")
    val timestamp: String,
)

@Serializable
data class LoginDataResponse(
    @SerialName("auth")
    val auth: Boolean,

    @SerialName("success")
    val success: Boolean,

    @SerialName("accessToken")
    val accessToken: String,

    @SerialName("refreshToken")
    val refreshToken: String? = null,
)
