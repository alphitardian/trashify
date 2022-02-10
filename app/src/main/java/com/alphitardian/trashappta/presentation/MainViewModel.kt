package com.alphitardian.trashappta.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alphitardian.trashappta.data.user.remote.response.ProfileResponse
import com.alphitardian.trashappta.domain.repository.UserRepository
import com.alphitardian.trashappta.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val userRepository: UserRepository) : ViewModel() {
    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    private val _profile: MutableLiveData<Resource<ProfileResponse>> = MutableLiveData()
    val profile: LiveData<Resource<ProfileResponse>> get() = _profile

    init {
        viewModelScope.launch {
            delay(3000)
            getUserProfile()
            _isLoading.value = false
        }
    }

    fun getUserProfile() {
        viewModelScope.launch {
            runCatching {
                _profile.value = Resource.Loading()
                val response = userRepository.getUserProfile()
                _profile.value = Resource.Success(response)
            }.getOrElse {
                it.printStackTrace()
                _profile.value = Resource.Error(it)
            }
        }
    }
}