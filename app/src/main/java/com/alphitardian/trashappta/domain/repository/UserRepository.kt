package com.alphitardian.trashappta.domain.repository

import com.alphitardian.trashappta.data.user.remote.response.*

interface UserRepository {
    suspend fun registerAccount(registerRequest: RegisterRequest): RegisterResponse
    suspend fun loginAccount(loginRequest: LoginRequest): LoginResponse
    suspend fun getUserProfile(): ProfileResponse
    suspend fun updateUserProfile(profileRequest: ProfileRequest): ProfileResponse
}