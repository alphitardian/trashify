package com.alphitardian.trashappta.data.waste.remote

import com.alphitardian.trashappta.data.waste.remote.response.*

interface WasteDataSource {
    suspend fun getWasteByAlias(alias: String): WasteResponse<WasteDataResponse>
    suspend fun getAllWaste(): WasteResponse<List<WasteDataResponse>>
    suspend fun getWasteHistory(): WasteResponse<List<WasteHistoryResponse>>
    suspend fun sendWasteHistory(wasteRequest: WasteRequest): WasteResponse<WasteRequestResponse>
}