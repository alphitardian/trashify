package com.alphitardian.trashappta.utils

sealed class Resource<T> {
    data class Success<T>(val data: T) : Resource<T>()
    data class Error<T>(val error: Throwable? = null, val code: Int? = null, val message: String? = null) : Resource<T>()
    data class Loading<T>(val message: String? = null) : Resource<T>()
}