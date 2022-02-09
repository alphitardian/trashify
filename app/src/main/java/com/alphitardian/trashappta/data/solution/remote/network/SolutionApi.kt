package com.alphitardian.trashappta.data.solution.remote.network

import com.alphitardian.trashappta.data.solution.remote.response.SolutionResponse
import retrofit2.http.GET

interface SolutionApi {
    @GET("solution")
    suspend fun getAllSolution(): SolutionResponse
}