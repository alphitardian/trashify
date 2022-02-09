package com.alphitardian.trashappta.presentation.quiz

import android.os.CountDownTimer
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.alphitardian.trashappta.R
import com.alphitardian.trashappta.data.quiz.remote.response.QuizResponse
import com.alphitardian.trashappta.presentation.home.HomeTopAppBar
import com.alphitardian.trashappta.utils.Resource
import com.skydoves.landscapist.glide.GlideImage

@Composable
fun QuizScreen(
    viewModel: QuizViewModel? = null,
    navigateBack: () -> Unit = {},
    navigateToResult: () -> Unit = {},
) {
    val scaffoldState = rememberScaffoldState()
    val context = LocalContext.current
    val quiz = viewModel?.quiz?.observeAsState()

    LaunchedEffect(key1 = Unit) {
        viewModel?.playBackgroundMusic(context)
    }

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = { HomeTopAppBar() },
        content = {
            when (val value = quiz?.value) {
                is Resource.Success -> QuizSession(viewModel, value.data, navigateToResult)
                else -> QuizContent(viewModel)
            }
        }
    )
    BackHandler {
        viewModel?.quizTimer?.cancel()
        navigateBack()
    }
}

@Composable
fun QuizContent(viewModel: QuizViewModel?) {
    ConstraintLayout(modifier = Modifier.fillMaxSize()) {
        val (imageRef, titleRef, buttonRef) = createRefs()

        GlideImage(
            imageModel = "https://media0.giphy.com/media/KDnZAzNpawWH1iAn1B/giphy.gif",
            previewPlaceholder = R.drawable.placeholder_image,
            error = painterResource(id = R.drawable.placeholder_image),
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .constrainAs(imageRef) {
                    start.linkTo(parent.start, margin = 20.dp)
                    end.linkTo(parent.end, margin = 20.dp)
                    top.linkTo(parent.top, margin = 20.dp)
                    width = Dimension.preferredWrapContent
                }
        )
        Text(
            text = "Are you ready for quiz?",
            style = MaterialTheme.typography.h5,
            fontWeight = FontWeight.W700,
            modifier = Modifier.constrainAs(titleRef) {
                start.linkTo(parent.start, margin = 20.dp)
                end.linkTo(parent.end, margin = 20.dp)
                top.linkTo(imageRef.bottom, margin = 16.dp)
            }
        )
        Button(
            onClick = { viewModel?.getAllQuiz() },
            shape = RoundedCornerShape(28.dp),
            modifier = Modifier
                .height(52.dp)
                .fillMaxWidth()
                .constrainAs(buttonRef) {
                    start.linkTo(parent.start, margin = 20.dp)
                    end.linkTo(parent.end, margin = 20.dp)
                    bottom.linkTo(parent.bottom, margin = 20.dp)
                    width = Dimension.preferredWrapContent
                }
        ) {
            Text(text = "Ready!")
        }
    }
}

@Composable
fun QuizSession(
    viewModel: QuizViewModel,
    quizResponse: QuizResponse,
    navigateToResult: () -> Unit = {},
) {
    val isQuizRunning = remember { mutableStateOf(false) }
    val countDownText = remember { mutableStateOf("") }
    val selectedAnswer = remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(key1 = Unit) {
        countdownTimer(
            viewModel = viewModel,
            onTextChange = { countDownText.value = it },
            onActive = { viewModel.isCountdown.value = it }
        )
    }

    if (viewModel.isCountdown.value) {
        Box(modifier = Modifier.fillMaxSize()) {
            Text(
                text = countDownText.value,
                style = MaterialTheme.typography.h1,
                fontWeight = FontWeight.W700,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    } else {
        quizTimer(
            viewModel = viewModel,
            onTextChange = { countDownText.value = it },
            onActive = { isQuizRunning.value = it }
        )

        ConstraintLayout(modifier = Modifier.fillMaxSize()) {
            val (imageRef, questionRef, answerRef, countdownRef, nextButtonRef) = createRefs()

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .constrainAs(imageRef) {
                        start.linkTo(parent.start)
                        top.linkTo(parent.top)
                        end.linkTo(parent.end)
                    }
            ) {
                GlideImage(
                    imageModel = quizResponse.data[viewModel.quizItemCount.value].imageUrl,
                    contentScale = ContentScale.Fit,
                    previewPlaceholder = R.drawable.placeholder_image,
                    error = painterResource(id = R.drawable.placeholder_image)
                )
            }

            Text(
                text = quizResponse.data[viewModel.quizItemCount.value].question.orEmpty(),
                modifier = Modifier.constrainAs(questionRef) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    top.linkTo(imageRef.bottom, margin = 20.dp)
                }
            )

            Row(
                modifier = Modifier.constrainAs(answerRef) {
                    start.linkTo(parent.start, margin = 20.dp)
                    end.linkTo(parent.end, margin = 20.dp)
                    top.linkTo(questionRef.bottom, margin = 10.dp)
                }
            ) {
                quizResponse.data[viewModel.quizItemCount.value].choices.forEachIndexed { index, choiceResponse ->
                    Box(
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .size(120.dp)
                            .clickable {
                                viewModel.tappedAnswerId.value = choiceResponse.id
                                selectedAnswer.value = index
                            }
                            .border(
                                width = if (selectedAnswer.value == index) 2.dp else (-1).dp,
                                color = Color.Green,
                            )
                            .clip(
                                shape = RoundedCornerShape(8.dp),
                            )
                            .background(
                                if (selectedAnswer.value == index) Color.Green.copy(0.3f)
                                else Color.LightGray
                            )
                            .padding(4.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .align(Alignment.Center),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_baseline_delete_outline_24),
                                contentDescription = null,
                                modifier = Modifier.size(81.dp),

                            )
                            Text(text = choiceResponse.title)
                        }
                    }
                }
            }

            if (isQuizRunning.value) {
                Text(
                    text = countDownText.value,
                    style = MaterialTheme.typography.h5,
                    fontWeight = FontWeight.W700,
                    modifier = Modifier.constrainAs(countdownRef) {
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        bottom.linkTo(parent.bottom, margin = 20.dp)
                    }
                )
            } else {
                Button(
                    onClick = {
                        println(viewModel.checkAnswer())
                        if (viewModel.quizItemCount.value < quizResponse.data.size - 1) {
                            viewModel.quizItemCount.value++
                            selectedAnswer.value = null
                            quizTimer(
                                viewModel = viewModel,
                                onTextChange = { countDownText.value = it },
                                onActive = { isQuizRunning.value = it }
                            )
                        } else {
                            navigateToResult()
                        }
                    },
                    shape = RoundedCornerShape(28.dp),
                    modifier = Modifier
                        .height(52.dp)
                        .fillMaxWidth()
                        .constrainAs(nextButtonRef) {
                            start.linkTo(parent.start, margin = 20.dp)
                            end.linkTo(parent.end, margin = 20.dp)
                            bottom.linkTo(parent.bottom, margin = 20.dp)
                            width = Dimension.preferredWrapContent
                        }
                ) {
                    val text =
                        if (viewModel.quizItemCount.value < quizResponse.data.size - 1) "Next" else "Finish"
                    Text(text = text)
                }
            }
        }
    }
}

private fun countdownTimer(
    viewModel: QuizViewModel,
    onTextChange: (String) -> Unit,
    onActive: (Boolean) -> Unit,
) {
    viewModel.countDownTimer = object : CountDownTimer(3000, 1000) {
        override fun onTick(p0: Long) {
            onTextChange(viewModel.START_COUNTDOWN_TIMER.toString())
            viewModel.START_COUNTDOWN_TIMER--
        }

        override fun onFinish() {
            onTextChange("0")
            onActive(false)
            viewModel.START_COUNTDOWN_TIMER = 3
        }
    }
    viewModel.countDownTimer?.start()
    onActive(true)
}

private fun quizTimer(
    viewModel: QuizViewModel,
    onTextChange: (String) -> Unit,
    onActive: (Boolean) -> Unit,
) {
    viewModel.quizTimer = object : CountDownTimer(10000, 1000) {
        override fun onTick(p0: Long) {
            onTextChange(viewModel.START_QUIZ_TIMER.toString())
            viewModel.START_QUIZ_TIMER--
        }

        override fun onFinish() {
            onTextChange("0")
            onActive(false)
            viewModel.START_QUIZ_TIMER = 10
        }
    }
    if (!viewModel.isCountdown.value) {
        viewModel.quizTimer?.start()
        onActive(true)
    }
}

@Preview
@Composable
fun PreviewScreen() {
    QuizScreen()
}