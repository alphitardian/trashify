package com.alphitardian.trashappta.data.user.remote.network

import com.alphitardian.trashappta.data.datastore.AppDatastore
import com.alphitardian.trashappta.data.image.remote.response.AuthErrorResponse
import com.alphitardian.trashappta.data.user.remote.response.LoginResponse
import com.alphitardian.trashappta.data.user.remote.response.RefreshTokenRequest
import com.alphitardian.trashappta.di.MainWasteRetrofit
import com.auth0.android.jwt.JWT
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import javax.inject.Inject

class UserInterceptor @Inject constructor(
    @MainWasteRetrofit private val retrofit: Retrofit.Builder,
    private val datastore: AppDatastore,
    private val loggingInterceptor: HttpLoggingInterceptor
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        synchronized(this) {
            val originalRequest = chain.request()
            val authenticationRequest = runBlocking(Dispatchers.IO) {
                val token = datastore.getUserToken().orEmpty()
                originalRequest.newBuilder().addHeader("Authorization", token).build()
            }
            val initialResponse = chain.proceed(authenticationRequest)

            when {
                initialResponse.code == 401 -> {
                    val errorResponse = initialResponse.body?.string()
                    val jsonResponse = Json.decodeFromString<AuthErrorResponse>(errorResponse.toString())
                    if (jsonResponse.message == "Token expired") {
                        initialResponse.close()
                        val tokenResponse = runBlocking { getNewToken() }
                        datastoreTransaction(tokenResponse)
                        val newRequest = originalRequest.newBuilder()
                            .addHeader("Authorization", tokenResponse.data.accessToken)
                            .build()

                        return chain.proceed(newRequest)
                    }
                }
            }

            return initialResponse
        }
    }

    private suspend fun getNewToken(): LoginResponse {
        val refreshToken = datastore.getRefreshToken().orEmpty()
        val accessToken = datastore.getUserToken().orEmpty()
        val request = RefreshTokenRequest(refreshToken)
        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor {
                val refreshRequest = it.request().newBuilder()
                    .addHeader("Authorization", accessToken)
                    .build()
                it.proceed(refreshRequest)
            }
            .build()

        return retrofit.client(client)
            .build()
            .create(UserApi::class.java)
            .refreshToken(request)
    }

    private fun datastoreTransaction(response: LoginResponse) {
        runBlocking(Dispatchers.IO) {
            runCatching {
                saveUserToken(response.data.accessToken)

                val token = response.data.accessToken.split("Bearer ")
                val jwt = JWT(token[1])
                val expiredTime = jwt.expiresAt?.time
                expiredTime?.let { saveExpiredTime(it) }
            }.getOrElse {
                it.printStackTrace()
            }
        }
    }

    private fun saveUserToken(token: String) {
        runBlocking(Dispatchers.IO) {
            datastore.saveToken(token)
        }
    }

    private fun saveExpiredTime(time: Long) {
        runBlocking(Dispatchers.IO) {
            datastore.saveExpiredTime(time)
        }
    }
}