package com.alphitardian.trashappta.data.waste.remote.network

import com.alphitardian.trashappta.data.waste.remote.response.*
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface WasteApi {
    @GET("waste/{alias}")
    suspend fun getWasteByAlias(@Path("alias") alias: String): WasteResponse<WasteDataResponse>

    @GET("waste")
    suspend fun getAllWaste(): WasteResponse<List<WasteDataResponse>>

    @GET("waste-history")
    suspend fun getWasteHistory(): WasteResponse<List<WasteHistoryResponse>>

    @POST("waste-history")
    suspend fun sendWasteHistory(@Body wasteRequest: WasteRequest): WasteResponse<WasteRequestResponse>
}