package com.alphitardian.trashappta.data.waste.remote.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WasteRequest(
    @SerialName("userId")
    val userId: String,

    @SerialName("url")
    val url: String,

    @SerialName("latitude")
    val latitude: Double,

    @SerialName("longitude")
    val longitude: Double,

    @SerialName("wasteCategory")
    val wasteCategory: String, // waste ID
)

@Serializable
data class WasteRequestResponse(
    @SerialName("id")
    val id: String,

    @SerialName("userId")
    val userId: String,

    @SerialName("url")
    val url: String,

    @SerialName("latitude")
    val latitude: Double,

    @SerialName("longitude")
    val longitude: Double,

    @SerialName("wasteCategory")
    val wasteCategory: String,

    @SerialName("createdAt")
    val createdAt: String,

    @SerialName("updatedAt")
    val updatedAt: String,
)
