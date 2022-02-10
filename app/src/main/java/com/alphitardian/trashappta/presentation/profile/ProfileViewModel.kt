package com.alphitardian.trashappta.presentation.profile

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alphitardian.trashappta.data.image.local.entity.ImageEntity
import com.alphitardian.trashappta.data.image.remote.response.ImageResponse
import com.alphitardian.trashappta.data.user.remote.response.ProfileRequest
import com.alphitardian.trashappta.data.user.remote.response.ProfileResponse
import com.alphitardian.trashappta.domain.repository.ImageRepository
import com.alphitardian.trashappta.domain.repository.UserRepository
import com.alphitardian.trashappta.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val imageRepository: ImageRepository,
) : ViewModel() {
    private val _profile: MutableLiveData<Resource<ProfileResponse>> = MutableLiveData()
    val profile: LiveData<Resource<ProfileResponse>> get() = _profile

    private var _image: MutableLiveData<Resource<ImageResponse>> = MutableLiveData()
    val image: LiveData<Resource<ImageResponse>> get() = _image

    val userName = mutableStateOf("")
    val userEmail = mutableStateOf("")
    val userId = mutableStateOf("")

    init {
        getUserProfile()
    }

    fun getUserProfile() {
        viewModelScope.launch {
            runCatching {
                _profile.value = Resource.Loading()
                val response = userRepository.getUserProfile()
                initiateValue(response)
                _profile.value = Resource.Success(response)
            }.getOrElse {
                it.printStackTrace()
                _profile.value = Resource.Error(it)
            }
        }
    }

    fun updateUserProfile() {
        viewModelScope.launch {
            runCatching {
                _profile.value = Resource.Loading()
                val request = ProfileRequest(
                    name = userName.value,
                    email = userEmail.value
                )
                val response = userRepository.updateUserProfile(request)
                userId.value = response.data.id
                _profile.value = Resource.Success(response)
            }.getOrElse {
                it.printStackTrace()
                _profile.value = Resource.Error(it)
            }
        }
    }

    fun uploadImage(base64Data: String) {
        viewModelScope.launch {
            runCatching {
                _image.value = Resource.Loading()
                val response = imageRepository.uploadImage(base64Data)
                val entity = ImageEntity(
                    userId = userId.value,
                    imageUrl = response.data.url
                )
                imageRepository.insertImage(entity)
                _image.value = Resource.Success(response)
            }.getOrElse {
                it.printStackTrace()
                _image.value = Resource.Error(it)
            }
        }
    }

    val getImage = imageRepository.getImage(userId.value)

    fun initiateValue(response: ProfileResponse) {
        userName.value = response.data.name
        userEmail.value = response.data.email
    }

    fun setUserName(name: String) {
        userName.value = name
    }

    fun setUserEmail(email: String) {
        userEmail.value = email
    }
}