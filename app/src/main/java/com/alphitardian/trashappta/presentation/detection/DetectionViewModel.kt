package com.alphitardian.trashappta.presentation.detection

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.toLowerCase
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alphitardian.trashappta.data.datastore.AppDatastore
import com.alphitardian.trashappta.data.image.remote.response.ImageResponse
import com.alphitardian.trashappta.data.waste.remote.response.WasteDataResponse
import com.alphitardian.trashappta.data.waste.remote.response.WasteRequest
import com.alphitardian.trashappta.data.waste.remote.response.WasteRequestResponse
import com.alphitardian.trashappta.data.waste.remote.response.WasteResponse
import com.alphitardian.trashappta.domain.repository.ImageRepository
import com.alphitardian.trashappta.domain.repository.MapsRepository
import com.alphitardian.trashappta.domain.repository.WasteRepository
import com.alphitardian.trashappta.utils.Resource
import com.google.android.libraries.places.api.model.Place
import com.google.mlkit.common.model.LocalModel
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.custom.CustomImageLabelerOptions
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject
import kotlin.math.roundToInt

@HiltViewModel
class DetectionViewModel @Inject constructor(
    private val wasteRepository: WasteRepository,
    private val imageRepository: ImageRepository,
    private val mapsRepository: MapsRepository,
    private val datastore: AppDatastore,
) : ViewModel() {
    val result = mutableStateOf("")
    val wasteCategory = mutableStateOf("")

    private var _detection: MutableLiveData<Resource<Boolean>> = MutableLiveData()
    val detection: LiveData<Resource<Boolean>> get() = _detection

    private var _waste: MutableLiveData<Resource<WasteResponse<WasteDataResponse>>> =
        MutableLiveData()
    val waste: LiveData<Resource<WasteResponse<WasteDataResponse>>> get() = _waste

    private var _image: MutableLiveData<Resource<ImageResponse>> = MutableLiveData()
    val image: LiveData<Resource<ImageResponse>> get() = _image

    private var _uploadWaste: MutableLiveData<Resource<WasteResponse<WasteRequestResponse>>> =
        MutableLiveData()
    val uploadWaste: LiveData<Resource<WasteResponse<WasteRequestResponse>>> get() = _uploadWaste

    private var _location: MutableLiveData<Resource<Place>> = MutableLiveData()
    val location: LiveData<Resource<Place>> get() = _location

    fun detectImage(
        localModel: LocalModel,
        uriImg: Uri,
        context: Context,
    ) {
        _detection.value = Resource.Loading()

        val options = CustomImageLabelerOptions.Builder(localModel)
            .setConfidenceThreshold(0.0f) // Batasan prediksi 0.0 - 1.0
            .setMaxResultCount(5)
            .build()
        val labeler = ImageLabeling.getClient(options)
        val image = InputImage.fromFilePath(context, uriImg)

        viewModelScope.launch {
            delay(2000)

            labeler.process(image)
                .addOnSuccessListener { labels ->
                    val label = labels.first()
                    val text = label.text
                    val confidence = label.confidence

                    result.value = "$text $confidence"
                    println("result " + result.value)
                    _detection.value = Resource.Success(true)
                }
                .addOnFailureListener {
                    result.value = "fail"
                    _detection.value = Resource.Error(it)
                }
                .addOnCanceledListener {
                    result.value = "cancel"
                    _detection.value = Resource.Success(true)
                }
        }
    }

    fun getWasteType(alias: String) {
        viewModelScope.launch {
            runCatching {
                _waste.value = Resource.Loading()
                val response = wasteRepository.getWasteByAlias(alias.lowercase(Locale.getDefault()))
                wasteCategory.value = response.data?.id.orEmpty()
                _waste.value = Resource.Success(response)
            }.getOrElse {
                it.printStackTrace()
                _waste.value = Resource.Error(it)
            }
        }
    }

    fun uploadImage(base64Data: String) {
        viewModelScope.launch {
            runCatching {
                _image.value = Resource.Loading()
                val response = imageRepository.uploadImage(base64Data)
                _image.value = Resource.Success(response)
            }.getOrElse {
                it.printStackTrace()
                _waste.value = Resource.Error(it)
            }
        }
    }

    fun sendWasteHistory(wasteRequest: WasteRequest) {
        viewModelScope.launch {
            runCatching {
                _uploadWaste.value = Resource.Loading()
                val response = wasteRepository.sendWasteHistory(wasteRequest)
                _uploadWaste.value = Resource.Success(response)
            }.getOrElse {
                it.printStackTrace()
                _uploadWaste.value = Resource.Error(it)
            }
        }
    }

    fun getCurrentLocation() {
        viewModelScope.launch {
            runCatching {
                _location.value = Resource.Loading()
                val response = mapsRepository.getCurrentLocation()
                response?.let { _location.value = Resource.Success(it) }
            }.getOrElse {
                it.printStackTrace()
                _location.value = Resource.Error(it)
            }
        }
    }

    fun extractWasteType(): String {
        return when (result.value.split(" ")[0]) {
            "O" -> "Organic"
            "N" -> "Non-Organic"
            "R" -> "Recyclable"
            else -> ""
        }

    }

    fun extractWasteConfidence(): String {
        val result = result.value.split(" ")[1]
        val finalConfidence = (result.toDouble() * 100).roundToInt()
        return finalConfidence.toString()
    }

    suspend fun getUserId(): String {
        return withContext(Dispatchers.IO) {
            datastore.userId.firstOrNull().orEmpty()
        }
    }
}