package com.alphitardian.trashappta.presentation.bank

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alphitardian.trashappta.domain.repository.MapsRepository
import com.alphitardian.trashappta.utils.Resource
import com.google.android.libraries.places.api.model.Place
import com.google.maps.model.PlacesSearchResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WasteBankViewModel @Inject constructor(private val mapsRepository: MapsRepository) : ViewModel() {

    private var _location: MutableLiveData<Resource<Place>> = MutableLiveData()
    val location: LiveData<Resource<Place>> get() = _location

    private var _nearbyPlace: MutableLiveData<Resource<List<PlacesSearchResult>>> = MutableLiveData()
    val nearbyPlace: LiveData<Resource<List<PlacesSearchResult>>> get() = _nearbyPlace

    init {
        getCurrentPlace()
    }

    private fun getCurrentPlace() {
        viewModelScope.launch(Dispatchers.IO) {
            runCatching {
                _location.postValue(Resource.Loading())
                val response = mapsRepository.getCurrentLocation()
                response?.let { _location.postValue(Resource.Success(it)) }
            }.getOrElse {
                it.printStackTrace()
                _location.postValue(Resource.Error(it))
            }
        }
    }

    fun getNearbyBank() {
        viewModelScope.launch {
            runCatching {
                _nearbyPlace.value = Resource.Loading()
                when (val value = location.value) {
                    is Resource.Success -> {
                        val response = mapsRepository.getNearbyPlace(
                            latitude = value.data.latLng?.latitude ?: 0.0,
                            longitude = value.data.latLng?.longitude ?: 0.0
                        )
                        _nearbyPlace.value = response?.let { Resource.Success(response) }
                    }
                    is Resource.Loading -> _nearbyPlace.value = Resource.Loading()
                    is Resource.Error -> {
                        value.error?.printStackTrace()
                        _nearbyPlace.value = Resource.Error(value.error)
                    }
                }
            }.getOrElse {
                it.printStackTrace()
                _nearbyPlace.value = Resource.Error(it)
            }
        }
    }
}