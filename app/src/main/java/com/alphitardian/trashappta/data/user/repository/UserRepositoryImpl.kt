package com.alphitardian.trashappta.data.user.repository

import com.alphitardian.trashappta.data.user.remote.UserDataSource
import com.alphitardian.trashappta.data.user.remote.response.*
import com.alphitardian.trashappta.domain.repository.UserRepository
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(private val dataSource: UserDataSource): UserRepository {
    override suspend fun registerAccount(registerRequest: RegisterRequest): RegisterResponse {
        return dataSource.registerAccount(registerRequest)
    }

    override suspend fun loginAccount(loginRequest: LoginRequest): LoginResponse {
        return dataSource.loginAccount(loginRequest)
    }

    override suspend fun getUserProfile(): ProfileResponse {
        return dataSource.getUserProfile()
    }
}