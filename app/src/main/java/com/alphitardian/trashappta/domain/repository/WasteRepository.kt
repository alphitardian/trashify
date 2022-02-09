package com.alphitardian.trashappta.domain.repository

import com.alphitardian.trashappta.data.waste.remote.response.*

interface WasteRepository {
    suspend fun getWasteByAlias(alias: String) : WasteResponse<WasteDataResponse>
    suspend fun getAllWaste(): WasteResponse<List<WasteDataResponse>>
    suspend fun getWasteHistory(): WasteResponse<List<WasteHistoryResponse>>
    suspend fun sendWasteHistory(wasteRequest: WasteRequest): WasteResponse<WasteRequestResponse>
}