package com.alphitardian.trashappta.data.quiz.remote

import com.alphitardian.trashappta.data.quiz.remote.response.QuizResponse

interface QuizDataSource {
    suspend fun getAllQuiz(): QuizResponse
    suspend fun getRandomQuiz(): QuizResponse
}