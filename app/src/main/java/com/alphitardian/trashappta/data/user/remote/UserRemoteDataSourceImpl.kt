package com.alphitardian.trashappta.data.user.remote

import com.alphitardian.trashappta.data.user.remote.network.UserApi
import com.alphitardian.trashappta.data.user.remote.response.*
import javax.inject.Inject

class UserRemoteDataSourceImpl @Inject constructor(private val userApi: UserApi): UserDataSource {
    override suspend fun registerAccount(registerRequest: RegisterRequest): RegisterResponse {
        return userApi.registerAccount(registerRequest)
    }

    override suspend fun loginAccount(loginRequest: LoginRequest): LoginResponse {
        return userApi.loginAccount(loginRequest)
    }

    override suspend fun getUserProfile(): ProfileResponse {
        return userApi.getUserProfile()
    }

    override suspend fun updateUserProfile(profileRequest: ProfileRequest): ProfileResponse {
        return userApi.updateUserProfile(profileRequest)
    }
}