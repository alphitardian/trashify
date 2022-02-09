package com.alphitardian.trashappta.domain.model

import android.os.Bundle
import androidx.navigation.NavType
import com.alphitardian.trashappta.data.waste.remote.response.WasteHistoryResponse
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class WasteParamType : NavType<WasteHistoryResponse>(isNullableAllowed = true) {
    override fun get(bundle: Bundle, key: String): WasteHistoryResponse? {
        return bundle.getParcelable(key)
    }

    override fun parseValue(value: String): WasteHistoryResponse {
        val decoder = Json(from = Json, builderAction = {
            ignoreUnknownKeys = true
        })
        return decoder.decodeFromString(value)
    }

    override fun put(bundle: Bundle, key: String, value: WasteHistoryResponse) {
        bundle.putParcelable(key, value)
    }
}