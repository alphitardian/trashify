package com.alphitardian.trashappta.data.waste.remote.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WasteResponse <T> (
    @SerialName("success")
    val success: Boolean,

    @SerialName("message")
    val message: String,

    @SerialName("data")
    val data: T? = null,

    @SerialName("timestamp")
    val timestamp: String,
)

@Serializable
data class WasteDataResponse(
    @SerialName("id")
    val id: String,

    @SerialName("name")
    val name: String? = null,

    @SerialName("alias")
    val alias: String,

    @SerialName("description")
    val description: String,

    @SerialName("solutions")
    val solution: WasteSolutionResponse,
)

@Serializable
@Parcelize
data class WasteHistoryResponse(
    @SerialName("id")
    val id: String,

    @SerialName("url")
    val url: String,

    @SerialName("latitude")
    val latitude: Double,

    @SerialName("longitude")
    val longitude: Double,

    @SerialName("createdAt")
    val createdAt: String,

    @SerialName("waste")
    val waste: Waste
): Parcelable

@Serializable
@Parcelize
data class Waste(
    @SerialName("name")
    val name: String,

    @SerialName("solutions")
    val solution: WasteSolutionResponse
): Parcelable

@Serializable
@Parcelize
data class WasteSolutionResponse(
    @SerialName("description")
    val description: String,
): Parcelable
