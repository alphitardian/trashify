package com.alphitardian.trashappta.data.quiz.remote.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class QuizResponse(
    @SerialName("success")
    val success: Boolean,

    @SerialName("message")
    val message: String,

    @SerialName("data")
    val data: List<QuizDataResponse>,

    @SerialName("timestamp")
    val timestamp: String,
)

@Serializable
data class QuizDataResponse(
    @SerialName("id")
    val id: String,

    @SerialName("question")
    val question: String,

    @SerialName("imgurl")
    val imageUrl: String? = null,

    @SerialName("createdAt")
    val createdAt: String,

    @SerialName("updatedAt")
    val updatedAt: String,

    @SerialName("choices")
    val choices: List<ChoiceResponse>
)

@Serializable
data class ChoiceResponse(
    @SerialName("id")
    val id: String,

    @SerialName("title")
    val title: String,

    @SerialName("isTrue")
    val isTrue: Boolean,

    @SerialName("quizId")
    val quizId: String,

    @SerialName("createdAt")
    val createdAt: String,

    @SerialName("updatedAt")
    val updatedAt: String,
)