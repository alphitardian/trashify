package com.alphitardian.trashappta.presentation.quiz

import android.content.Context
import android.media.MediaPlayer
import android.os.CountDownTimer
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alphitardian.trashappta.R
import com.alphitardian.trashappta.data.quiz.remote.response.QuizResponse
import com.alphitardian.trashappta.domain.repository.QuizRepository
import com.alphitardian.trashappta.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuizViewModel @Inject constructor(private val quizRepository: QuizRepository) : ViewModel() {

    private var _quiz: MutableLiveData<Resource<QuizResponse>> = MutableLiveData()
    val quiz: LiveData<Resource<QuizResponse>> get() = _quiz

    private var mediaPlayer = mutableStateOf<MediaPlayer?>(null)

    var tappedAnswerId = mutableStateOf("")
    val quizItemCount = mutableStateOf(0)
    val correctQuizAnswer = mutableStateOf(0)

    var START_COUNTDOWN_TIMER = 3
    var START_QUIZ_TIMER = 10

    var countDownTimer: CountDownTimer? = null
    var quizTimer: CountDownTimer? = null

    val isCountdown = mutableStateOf(true)

    override fun onCleared() {
        super.onCleared()
        stopBackgroundMusic()

        // reset state
        quizItemCount.value = 0
        correctQuizAnswer.value = 0
        countDownTimer = null
        quizTimer = null
        START_COUNTDOWN_TIMER = 3
        START_QUIZ_TIMER = 10
        isCountdown.value = true
        _quiz.value = Resource.Loading()
    }

    fun playBackgroundMusic(context: Context) {
        mediaPlayer.value = MediaPlayer.create(context, R.raw.kazoom)
        mediaPlayer.value?.isLooping = true
        mediaPlayer.value?.start()
    }

    fun stopBackgroundMusic() {
        mediaPlayer.value?.stop()
        mediaPlayer.value?.release()
        mediaPlayer.value = null
    }

    fun getRandomQuiz() {
        viewModelScope.launch {
            runCatching {
                _quiz.value = Resource.Loading()
                val response = quizRepository.getAllQuiz()
                val data = response.data.shuffled()
                println(data.size)
                _quiz.value = Resource.Success(response.copy(data = data))
            }.getOrElse {
                it.printStackTrace()
                _quiz.value = Resource.Error(it)
            }
        }
    }

    fun checkAnswer(): Boolean {
        return when (val value = quiz.value) {
            is Resource.Success -> {
                val answer: Boolean
                val response = value.data.data[quizItemCount.value].choices.filter {
                     it.id == tappedAnswerId.value
                }
                answer = response.firstOrNull()?.isTrue == true
                if (answer) correctQuizAnswer.value++
                answer
            }
            else -> false
        }
    }

    fun getQuizScore(): Int {
        val totalQuiz: Int
        return when (val value = quiz.value) {
            is Resource.Success -> {
                totalQuiz = value.data.data.size
                correctQuizAnswer.value * (100 / totalQuiz)
            }
            else -> 0
        }
    }

    fun close() {
        stopBackgroundMusic()
        onCleared()
    }
}