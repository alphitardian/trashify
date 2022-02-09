package com.alphitardian.trashappta.data.solution.remote.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SolutionResponse(
    @SerialName("success")
    val success: Boolean,

    @SerialName("message")
    val message: String,

    @SerialName("data")
    val data: List<SolutionDataResponse>,

    @SerialName("timestamp")
    val timestamp: String
)

@Serializable
data class SolutionDataResponse(
    @SerialName("id")
    val id: String,

    @SerialName("description")
    val description: String
)
