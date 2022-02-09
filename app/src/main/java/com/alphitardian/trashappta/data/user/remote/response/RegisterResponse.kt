package com.alphitardian.trashappta.data.user.remote.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RegisterResponse(
    @SerialName("success")
    val success: Boolean,

    @SerialName("message")
    val message: String,

    @SerialName("data")
    val data: RegisterDataResponse,

    @SerialName("timestamp")
    val timestamp: String,
)

@Serializable
data class RegisterDataResponse(
    @SerialName("id")
    val id: String,

    @SerialName("name")
    val name: String,

    @SerialName("email")
    val email: String,

    @SerialName("wasteCollected")
    val wasteCollected: Int,

    @SerialName("updatedAt")
    val updatedAt: String,

    @SerialName("createdAt")
    val createdAt: String,
)
