package com.alphitardian.trashappta.presentation.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alphitardian.trashappta.data.datastore.AppDatastore
import com.alphitardian.trashappta.data.user.remote.response.ProfileResponse
import com.alphitardian.trashappta.domain.repository.UserRepository
import com.alphitardian.trashappta.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val datastore: AppDatastore,
) : ViewModel() {
    private var _profile: MutableLiveData<Resource<ProfileResponse>> = MutableLiveData()
    val profile: LiveData<Resource<ProfileResponse>> get() = _profile

    fun getUserProfile() {
        viewModelScope.launch {
            runCatching {
                _profile.value = Resource.Loading()
                val response = userRepository.getUserProfile()
                saveUserId(response.data.id)
                _profile.value = Resource.Success(response)
            }.getOrElse {
                it.printStackTrace()
                _profile.value = Resource.Error(it)
            }
        }
    }

    private fun saveUserId(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            datastore.saveUserId(id)
        }
    }
}