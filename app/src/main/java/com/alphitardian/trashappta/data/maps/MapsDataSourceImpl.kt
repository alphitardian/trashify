package com.alphitardian.trashappta.data.maps

import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.ktx.api.net.awaitFetchPlace
import com.google.android.libraries.places.ktx.api.net.awaitFindCurrentPlace
import com.google.android.libraries.places.ktx.api.net.fetchPlaceRequest
import com.google.android.libraries.places.ktx.api.net.findCurrentPlaceRequest
import com.google.maps.FindPlaceFromTextRequest
import com.google.maps.GeoApiContext
import com.google.maps.GeocodingApi
import com.google.maps.PlacesApi
import com.google.maps.model.PlacesSearchResult
import com.google.maps.model.RankBy
import javax.inject.Inject

class MapsDataSourceImpl @Inject constructor(
    private val placesClient: PlacesClient,
    private val geoApiContext: GeoApiContext,
) : MapsDataSource {
    val token = AutocompleteSessionToken.newInstance()

    override suspend fun getCurrentLocation(): Place? {
        return try {
            val placeField =
                listOf(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG)
            val request = findCurrentPlaceRequest(placeField)
            val response = placesClient.awaitFindCurrentPlace(request)
            response.placeLikelihoods.firstOrNull()?.place
        } catch (e: SecurityException) {
            e.printStackTrace()
            null
        }
    }

    override suspend fun getPlaceId(latitude: Double, longitude: Double): String? {
        return try {
            val response = GeocodingApi.reverseGeocode(
                geoApiContext,
                com.google.maps.model.LatLng(latitude, longitude)
            ).await()
            response.first().placeId
        } catch (e: Throwable) {
            e.printStackTrace()
            null
        }
    }

    override suspend fun getPlaceDetail(placeId: String): Place {
        val placeField = listOf(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS)
        val request = fetchPlaceRequest(placeId, placeField) {
            sessionToken = token
        }
        val response = placesClient.awaitFetchPlace(request)
        return response.place
    }

    override suspend fun getNearbyPlace(
        latitude: Double,
        longitude: Double
    ): List<PlacesSearchResult>? {
        return try {
            val response = PlacesApi.nearbySearchQuery(
                geoApiContext,
                com.google.maps.model.LatLng(latitude, longitude)
            )
                .keyword("Bank sampah")
                .rankby(RankBy.DISTANCE)
                .await()
            response.results.toList()
        } catch (e: Throwable) {
            e.printStackTrace()
            null
        }
    }


}