package com.alphitardian.trashappta.presentation.login

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alphitardian.trashappta.data.datastore.AppDatastore
import com.alphitardian.trashappta.data.user.remote.response.LoginRequest
import com.alphitardian.trashappta.data.user.remote.response.LoginResponse
import com.alphitardian.trashappta.domain.repository.UserRepository
import com.alphitardian.trashappta.utils.Resource
import com.auth0.android.jwt.JWT
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val datastore: AppDatastore,
) : ViewModel() {

    val email = mutableStateOf("")
    val password = mutableStateOf("")

    private var _login: MutableLiveData<Resource<LoginResponse>> = MutableLiveData()
    val login: LiveData<Resource<LoginResponse>> get() = _login

    fun loginAccount() {
        viewModelScope.launch {
            runCatching {
                _login.value = Resource.Loading()
                val request = LoginRequest(
                    email = email.value.trim(),
                    password = password.value,
                )
                val response = userRepository.loginAccount(request)
                datastoreTransaction(response)
                _login.value = Resource.Success(response)
            }.getOrElse {
                it.printStackTrace()
                _login.value = Resource.Error(it)
            }
        }
    }

    fun setEmail(email: String) {
        this.email.value = email
    }

    fun setPassword(password: String) {
        this.password.value = password
    }

    fun checkFieldValidation(): Boolean {
        return email.value.isNotEmpty() && password.value.isNotEmpty()
    }

    private fun datastoreTransaction(response: LoginResponse) {
        viewModelScope.launch(Dispatchers.IO) {
            runCatching {
                saveUserToken(response.data.accessToken)
                response.data.refreshToken?.let { saveRefreshToken(it) }

                val token = response.data.accessToken.split("Bearer ")
                val jwt = JWT(token[1])
                val expiredTime = jwt.expiresAt?.time
                expiredTime?.let { saveExpiredTime(it) }
            }.getOrElse {
                it.printStackTrace()
            }
        }
    }

    private fun saveUserToken(token: String) {
        viewModelScope.launch(Dispatchers.IO) {
            datastore.saveToken(token)
        }
    }

    private fun saveRefreshToken(token: String) {
        viewModelScope.launch(Dispatchers.IO) {
            datastore.saveRefreshToken(token)
        }
    }

    private fun saveExpiredTime(time: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            datastore.saveExpiredTime(time)
        }
    }
}