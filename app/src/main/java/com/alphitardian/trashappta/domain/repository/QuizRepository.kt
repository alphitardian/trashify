package com.alphitardian.trashappta.domain.repository

import com.alphitardian.trashappta.data.quiz.remote.response.QuizResponse

interface QuizRepository {
    suspend fun getAllQuiz(): QuizResponse
}