package com.alphitardian.trashappta.data.waste.remote

import com.alphitardian.trashappta.data.waste.remote.network.WasteApi
import com.alphitardian.trashappta.data.waste.remote.response.*
import javax.inject.Inject

class WasteRemoteDataSourceImpl @Inject constructor(private val wasteApi: WasteApi): WasteDataSource {
    override suspend fun getWasteByAlias(alias: String): WasteResponse<WasteDataResponse> {
        return wasteApi.getWasteByAlias(alias)
    }

    override suspend fun getAllWaste(): WasteResponse<List<WasteDataResponse>> {
        return wasteApi.getAllWaste()
    }

    override suspend fun getWasteHistory(): WasteResponse<List<WasteHistoryResponse>> {
        return wasteApi.getWasteHistory()
    }

    override suspend fun sendWasteHistory(wasteRequest: WasteRequest): WasteResponse<WasteRequestResponse> {
        return wasteApi.sendWasteHistory(wasteRequest)
    }
}