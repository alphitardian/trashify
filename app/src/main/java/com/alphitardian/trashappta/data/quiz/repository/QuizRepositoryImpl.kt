package com.alphitardian.trashappta.data.quiz.repository

import com.alphitardian.trashappta.data.quiz.remote.QuizDataSource
import com.alphitardian.trashappta.data.quiz.remote.response.QuizResponse
import com.alphitardian.trashappta.domain.repository.QuizRepository
import javax.inject.Inject

class QuizRepositoryImpl @Inject constructor(private val quizDataSource: QuizDataSource): QuizRepository {
    override suspend fun getAllQuiz(): QuizResponse {
        return quizDataSource.getAllQuiz()
    }
}