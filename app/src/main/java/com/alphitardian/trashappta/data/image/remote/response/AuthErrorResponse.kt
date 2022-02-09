package com.alphitardian.trashappta.data.image.remote.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AuthErrorResponse(
    @SerialName("message")
    val message: String
)
