package com.alphitardian.trashappta.presentation.quiz

import android.os.CountDownTimer
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.airbnb.lottie.compose.*
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
        topBar = { QuizTopBar(navigateBack = navigateBack) },
        content = {
            when (val value = quiz?.value) {
                is Resource.Success -> {
                    println("final: ${value.data.data.size}")
                    QuizSession(viewModel, value.data, navigateToResult)
                }
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
fun QuizTopBar(navigateBack: () -> Unit) {
    TopAppBar(
        title = { Text(text = "Quiz") },
        navigationIcon = {
            IconButton(onClick = navigateBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = null,
                    tint = Color.Black
                )
            }
        },
        backgroundColor = Color.Transparent,
        elevation = 0.dp
    )
}

@Composable
fun QuizContent(viewModel: QuizViewModel?) {
    val lottieComposition by rememberLottieComposition(spec = LottieCompositionSpec.RawRes(R.raw.quiz_mode))
    val progress by animateLottieCompositionAsState(
        composition = lottieComposition,
        iterations = LottieConstants.IterateForever
    )

    ConstraintLayout(modifier = Modifier.fillMaxSize()) {
        val (imageRef, titleRef, buttonRef) = createRefs()

        LottieAnimation(
            composition = lottieComposition,
            progress = progress,
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
            onClick = { viewModel?.getRandomQuiz() },
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
                    .padding(20.dp)
                    .fillMaxWidth()
                    .height(250.dp)
                    .constrainAs(imageRef) {
                        start.linkTo(parent.start, margin = 20.dp)
                        top.linkTo(parent.top, margin = 20.dp)
                        end.linkTo(parent.end, margin = 20.dp)
                    },
                shape = RoundedCornerShape(16.dp)
            ) {
                GlideImage(
                    imageModel = quizResponse.data[viewModel.quizItemCount.value].imageUrl,
                    contentScale = ContentScale.Crop,
                    previewPlaceholder = R.drawable.placeholder_image,
                    error = painterResource(id = R.drawable.placeholder_image)
                )
            }

            Text(
                text = quizResponse.data[viewModel.quizItemCount.value].question.orEmpty(),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .constrainAs(questionRef) {
                        start.linkTo(parent.start, margin = 20.dp)
                        end.linkTo(parent.end, margin = 20.dp)
                        top.linkTo(imageRef.bottom, margin = 20.dp)
                        width = Dimension.preferredWrapContent
                    }
            )

            Row(
                modifier = Modifier.constrainAs(answerRef) {
                    start.linkTo(parent.start, margin = 20.dp)
                    end.linkTo(parent.end, margin = 20.dp)
                    top.linkTo(questionRef.bottom, margin = 20.dp)
                }
            ) {
                quizResponse.data[viewModel.quizItemCount.value].choices.forEachIndexed { index, choiceResponse ->
                    Box(
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .size(120.dp)
                            .clickable(
                                onClick = {
                                    viewModel.tappedAnswerId.value = choiceResponse.id
                                    selectedAnswer.value = index
                                    viewModel.quizTimer?.cancel()
                                    isQuizRunning.value = false
                                    viewModel.START_QUIZ_TIMER = 20
                                },
                                enabled = isQuizRunning.value
                            )
                            .border(
                                width = if (selectedAnswer.value == index) 2.dp else (-1).dp,
                                color = Color.Green,
                            )
                            .clip(shape = RoundedCornerShape(8.dp))
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
                            val image = when (choiceResponse.title) {
                                "Poison" -> R.raw.warning_sign
                                "Organic" -> R.raw.bouncing_fruits
                                "Non-Organic" -> R.raw.compact_plastic_bottle
                                "Recyclable" -> R.raw.paper_notes
                                "Yes" -> R.raw.tick_green
                                "No" -> R.raw.cross_animation
                                else -> R.raw.paper_notes
                            }
                            val lottieComposition by rememberLottieComposition(
                                spec = LottieCompositionSpec.RawRes(
                                    image
                                )
                            )
                            val progress by animateLottieCompositionAsState(
                                composition = lottieComposition,
                                iterations = LottieConstants.IterateForever
                            )

                            LottieAnimation(
                                composition = lottieComposition,
                                progress = progress,
                                modifier = Modifier.size(81.dp)
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
    viewModel.quizTimer = object : CountDownTimer(20000, 1000) {
        override fun onTick(p0: Long) {
            onTextChange(viewModel.START_QUIZ_TIMER.toString())
            viewModel.START_QUIZ_TIMER--
        }

        override fun onFinish() {
            onTextChange("0")
            onActive(false)
            viewModel.START_QUIZ_TIMER = 20
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