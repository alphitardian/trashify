package com.alphitardian.trashappta.data.user.remote.network

import com.alphitardian.trashappta.data.user.remote.response.*
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT

interface UserApi {
    @POST("auth/register")
    suspend fun registerAccount(@Body registerRequest: RegisterRequest): RegisterResponse

    @POST("auth/login")
    suspend fun loginAccount(@Body loginRequest: LoginRequest): LoginResponse

    @GET("user")
    suspend fun getUserProfile(): ProfileResponse

    @POST("auth/refresh")
    suspend fun refreshToken(@Body refreshTokenRequest: RefreshTokenRequest): LoginResponse

    @PUT("user")
    suspend fun updateUserProfile(@Body profileRequest: ProfileRequest): ProfileResponse
}