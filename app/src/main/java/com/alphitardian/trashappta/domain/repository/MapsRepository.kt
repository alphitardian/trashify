package com.alphitardian.trashappta.domain.repository

import com.google.android.libraries.places.api.model.Place
import com.google.maps.model.PlacesSearchResult

interface MapsRepository {
    suspend fun getCurrentLocation(): Place?
    suspend fun getPlaceId(latitude: Double, longitude: Double): String?
    suspend fun getPlaceDetail(placeId: String): Place
    suspend fun getNearbyPlace(latitude: Double, longitude: Double): List<PlacesSearchResult>?
}