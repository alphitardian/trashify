package com.alphitardian.trashappta.data.user.remote

import com.alphitardian.trashappta.data.user.remote.response.*

interface UserDataSource {
    suspend fun registerAccount(registerRequest: RegisterRequest): RegisterResponse
    suspend fun loginAccount(loginRequest: LoginRequest): LoginResponse
    suspend fun getUserProfile(): ProfileResponse
}