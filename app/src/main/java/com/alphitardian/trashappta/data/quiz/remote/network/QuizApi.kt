package com.alphitardian.trashappta.data.quiz.remote.network

import com.alphitardian.trashappta.data.quiz.remote.response.QuizResponse
import retrofit2.http.GET

interface QuizApi {
    @GET("quiz")
    suspend fun getAllQuiz(): QuizResponse

    @GET("quiz/generate")
    suspend fun getRandomQuiz(): QuizResponse
}