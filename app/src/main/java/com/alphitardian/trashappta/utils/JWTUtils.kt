package com.alphitardian.trashappta.utils

import android.util.Base64
import android.util.Log

object JWTUtils {
    fun jwtDecoded(value: String) {
        try {
            val splitString = value.split("\\.")
            Log.d("JWT_DECODED", "Header: " + jwtGetJson(splitString[0]));
            Log.d("JWT_DECODED", "Body: " + jwtGetJson(splitString[1]));
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    fun jwtGetJson(encodedValue: String): String {
        val decodedBytes = Base64.decode(encodedValue, Base64.URL_SAFE)
        return String(decodedBytes, Charsets.UTF_8)
    }
}