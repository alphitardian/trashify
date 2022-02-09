package com.alphitardian.trashappta.data.waste.repository

import com.alphitardian.trashappta.data.waste.remote.WasteDataSource
import com.alphitardian.trashappta.data.waste.remote.response.*
import com.alphitardian.trashappta.domain.repository.WasteRepository
import javax.inject.Inject

class WasteRepositoryImpl @Inject constructor(private val dataSource: WasteDataSource): WasteRepository {
    override suspend fun getWasteByAlias(alias: String): WasteResponse<WasteDataResponse> {
        return dataSource.getWasteByAlias(alias)
    }

    override suspend fun getAllWaste(): WasteResponse<List<WasteDataResponse>> {
        return dataSource.getAllWaste()
    }

    override suspend fun getWasteHistory(): WasteResponse<List<WasteHistoryResponse>> {
        return dataSource.getWasteHistory()
    }

    override suspend fun sendWasteHistory(wasteRequest: WasteRequest): WasteResponse<WasteRequestResponse> {
        return dataSource.sendWasteHistory(wasteRequest)
    }
}