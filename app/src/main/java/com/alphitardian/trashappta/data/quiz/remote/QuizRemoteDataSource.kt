package com.alphitardian.trashappta.data.quiz.remote

import com.alphitardian.trashappta.data.quiz.remote.network.QuizApi
import com.alphitardian.trashappta.data.quiz.remote.response.QuizResponse
import javax.inject.Inject

class QuizRemoteDataSource @Inject constructor(private val quizApi: QuizApi): QuizDataSource {
    override suspend fun getAllQuiz(): QuizResponse {
        return quizApi.getAllQuiz()
    }
}