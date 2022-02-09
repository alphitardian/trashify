package com.alphitardian.trashappta.presentation.register

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alphitardian.trashappta.data.user.remote.response.RegisterRequest
import com.alphitardian.trashappta.data.user.remote.response.RegisterResponse
import com.alphitardian.trashappta.domain.repository.UserRepository
import com.alphitardian.trashappta.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val userRepository: UserRepository,
) : ViewModel() {

    val name = mutableStateOf("")
    val email = mutableStateOf("")
    val password = mutableStateOf("")

    private var _register: MutableLiveData<Resource<RegisterResponse>> = MutableLiveData()
    val register: LiveData<Resource<RegisterResponse>> get() = _register

    fun registerAccount() {
        viewModelScope.launch {
            runCatching {
                _register.value = Resource.Loading()
                val request = RegisterRequest(
                    name = name.value,
                    email = email.value,
                    password = password.value
                )
                val response = userRepository.registerAccount(request)
                _register.value = Resource.Success(response)
            }.getOrElse {
                it.printStackTrace()
                _register.value = Resource.Error(it)
            }
        }
    }

    fun setEmail(email: String) {
        this.email.value = email
    }

    fun setPassword(password: String) {
        this.password.value = password
    }

    fun setName(name: String) {
        this.name.value = name
    }

    fun checkFieldValidation(): Boolean {
        return email.value.isNotEmpty() && password.value.isNotEmpty() && name.value.isNotEmpty()
    }
}