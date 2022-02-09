package com.alphitardian.trashappta.presentation.history

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alphitardian.trashappta.data.waste.remote.response.WasteHistoryResponse
import com.alphitardian.trashappta.data.waste.remote.response.WasteResponse
import com.alphitardian.trashappta.domain.repository.MapsRepository
import com.alphitardian.trashappta.domain.repository.WasteRepository
import com.alphitardian.trashappta.utils.Resource
import com.google.android.libraries.places.api.model.Place
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val wasteRepository: WasteRepository,
    private val mapsRepository: MapsRepository,
) : ViewModel() {
    private val _wasteHistory: MutableLiveData<Resource<WasteResponse<List<WasteHistoryResponse>>>> =
        MutableLiveData()
    val wasteHistory: LiveData<Resource<WasteResponse<List<WasteHistoryResponse>>>> get() = _wasteHistory

    private val _placeId: MutableLiveData<Resource<String>> = MutableLiveData()
    val placeId: LiveData<Resource<String>> get() = _placeId

    private val _place: MutableLiveData<Resource<Place>> = MutableLiveData()
    val place: LiveData<Resource<Place>> get() = _place

    private val _isRefresh = MutableStateFlow(false)
    val isRefresh: StateFlow<Boolean> get() = _isRefresh

    fun getWasteHistory() {
        viewModelScope.launch {
            runCatching {
                _isRefresh.emit(true)
                _wasteHistory.value = Resource.Loading()
                val response = wasteRepository.getWasteHistory()
                _wasteHistory.value = Resource.Success(response)
                _isRefresh.emit(false)
            }.getOrElse {
                it.printStackTrace()
                _wasteHistory.value = Resource.Error(it)
            }
        }
    }

    fun getPlaceId(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            runCatching {
                _placeId.value = Resource.Loading()
                val response = mapsRepository.getPlaceId(latitude, longitude)
                _placeId.value = response?.let { Resource.Success(it) }
            }.getOrElse {
                it.printStackTrace()
                _placeId.value = Resource.Error(it)
            }
        }
    }

    fun getPlaceDetail(placeId: String) {
        viewModelScope.launch {
            runCatching {
                _place.value = Resource.Loading()
                val response = mapsRepository.getPlaceDetail(placeId)
                _place.value = Resource.Success(response)
            }.getOrElse {
                it.printStackTrace()
                _place.value = Resource.Error(it)
            }
        }
    }
}