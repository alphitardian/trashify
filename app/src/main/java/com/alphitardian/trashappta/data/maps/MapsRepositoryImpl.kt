package com.alphitardian.trashappta.data.maps

import com.alphitardian.trashappta.domain.repository.MapsRepository
import com.google.android.libraries.places.api.model.Place
import com.google.maps.model.PlacesSearchResult
import javax.inject.Inject

class MapsRepositoryImpl @Inject constructor(private val mapsDataSource: MapsDataSource) : MapsRepository {
    override suspend fun getCurrentLocation(): Place? {
        return mapsDataSource.getCurrentLocation()
    }

    override suspend fun getPlaceId(latitude: Double, longitude: Double): String? {
        return mapsDataSource.getPlaceId(latitude, longitude)
    }

    override suspend fun getPlaceDetail(placeId: String): Place {
        return mapsDataSource.getPlaceDetail(placeId)
    }

    override suspend fun getNearbyPlace(
        latitude: Double,
        longitude: Double
    ): List<PlacesSearchResult>? {
        return mapsDataSource.getNearbyPlace(latitude, longitude)
    }
}