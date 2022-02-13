package com.alphitardian.trashappta.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.airbnb.lottie.compose.*
import com.alphitardian.trashappta.R
import com.alphitardian.trashappta.data.user.remote.response.ProfileResponse
import com.alphitardian.trashappta.utils.Resource
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.fade
import com.google.accompanist.placeholder.material.placeholder

@Composable
fun HomeScreen(
    viewModel: HomeViewModel? = null,
    navigateToDetection: () -> Unit = {},
    navigateToQuiz: () -> Unit = {},
    navigateToHistory: () -> Unit = {},
    navigateToProfile: () -> Unit = {},
    navigateToBank: () -> Unit = {},
) {
    val scaffoldState = rememberScaffoldState()
    var openDialog by remember { mutableStateOf(false) }

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            HomeTopAppBar(
                isWithAction = true,
                onDialogChange = { openDialog = it }
            )
        },
        content = {
            HomeContent(
                viewModel = viewModel,
                navigateToDetection = navigateToDetection,
                navigateToQuiz = navigateToQuiz,
                navigateToHistory = navigateToHistory,
                navigateToProfile = navigateToProfile,
                navigateToBank = navigateToBank
            )

            if (openDialog) {
                AlertDialog(
                    onDismissRequest = { openDialog = !openDialog },
                    title = { Text(text = "About the developer") },
                    text = { Text(text = "Developed by @alphitardian for Final Assignment") },
                    confirmButton = {
                        TextButton(onClick = { openDialog = !openDialog }) {
                            Text(text = "Cancel")
                        }
                    },
                    shape = RoundedCornerShape(16.dp)
                )
            }
        },
    )
}

@Composable
fun HomeContent(
    viewModel: HomeViewModel? = null,
    navigateToDetection: () -> Unit,
    navigateToQuiz: () -> Unit,
    navigateToHistory: () -> Unit,
    navigateToProfile: () -> Unit,
    navigateToBank: () -> Unit,
) {
    val profile = viewModel?.profile?.observeAsState()
    var profileResult by remember { mutableStateOf<ProfileResponse?>(null) }

    LaunchedEffect(key1 = Unit) {
        viewModel?.getUserProfile()
    }

    LaunchedEffect(key1 = profile?.value) {
        when (val value = profile?.value) {
            is Resource.Success -> profileResult = value.data
        }
    }

    ConstraintLayout(
        modifier = Modifier
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.White,
                        Color(0xFF74D3FF).copy(0.5f)
                    )
                )
            )
            .fillMaxSize()
    ) {
        val (titleRef, subtitleRef, scanButtonRef, quizButtonRef, historyButtonRef, mapsButtonRef, wasteBankRef) = createRefs()

        Text(
            text = "Hello! ${profileResult?.data?.name}",
            style = MaterialTheme.typography.h5,
            fontWeight = FontWeight.W700,
            modifier = Modifier
                .constrainAs(titleRef) {
                    start.linkTo(parent.start, margin = 20.dp)
                    top.linkTo(parent.top, margin = 20.dp)
                }
                .placeholder(
                    visible = profile?.value is Resource.Loading,
                    highlight = PlaceholderHighlight.fade(),
                    shape = RoundedCornerShape(8.dp)
                )
        )

        Text(
            text = "You have collect ${profileResult?.data?.wasteCollected} waste!",
            style = MaterialTheme.typography.h6,
            fontWeight = FontWeight.W400,
            modifier = Modifier
                .constrainAs(subtitleRef) {
                    start.linkTo(parent.start, margin = 20.dp)
                    top.linkTo(titleRef.bottom, margin = 8.dp)
                }
                .placeholder(
                    visible = profile?.value is Resource.Loading,
                    highlight = PlaceholderHighlight.fade(),
                    shape = RoundedCornerShape(8.dp)
                )
        )

        HomeMenuButton(
            icon = R.raw.camera,
            text = "Scan Trash",
            backgroundColor = Color.Red.copy(0.45f),
            textColor = Color.White,
            modifier = Modifier
                .clickable(onClick = navigateToDetection)
                .constrainAs(scanButtonRef) {
                    start.linkTo(parent.start, margin = 20.dp)
                    top.linkTo(subtitleRef.bottom, margin = 28.dp)
                }
        )

        HomeMenuButton(
            icon = R.raw.quiz,
            text = "Quiz",
            isIconIterate = true,
            backgroundColor = Color.Yellow.copy(0.45f),
            textColor = Color.Red.copy(0.45f),
            modifier = Modifier
                .clickable(onClick = navigateToQuiz)
                .constrainAs(quizButtonRef) {
                    top.linkTo(subtitleRef.bottom, margin = 28.dp)
                    end.linkTo(parent.end, margin = 20.dp)
                },
            lottieModifier = Modifier.size(120.dp)
        )

        HomeMenuButton(
            icon = R.raw.history,
            text = "History",
            backgroundColor = Color.Magenta.copy(0.25f),
            textColor = Color.White,
            modifier = Modifier
                .clickable(onClick = navigateToHistory)
                .constrainAs(historyButtonRef) {
                    top.linkTo(scanButtonRef.bottom, margin = 20.dp)
                    start.linkTo(parent.start, margin = 20.dp)
                }
        )

        HomeMenuButton(
            icon = R.raw.woman_profile_icon,
            text = "Profile",
            isIconIterate = true,
            backgroundColor = Color.Cyan.copy(0.25f),
            textColor = Color.DarkGray,
            modifier = Modifier
                .clickable(onClick = navigateToProfile)
                .constrainAs(mapsButtonRef) {
                    top.linkTo(quizButtonRef.bottom, margin = 20.dp)
                    end.linkTo(parent.end, margin = 20.dp)
                },
            lottieModifier = Modifier.size(120.dp)
        )

        HomeMenuButton(
            icon = R.raw.banking_finance_icon,
            text = "Waste Bank",
            isIconIterate = true,
            backgroundColor = Color.Green.copy(0.25f),
            textColor = Color.Black,
            modifier = Modifier
                .clickable(onClick = navigateToBank)
                .constrainAs(wasteBankRef) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    top.linkTo(historyButtonRef.bottom, margin = 20.dp)
                }
        )
    }
}

@Composable
fun HomeMenuButton(
    icon: Int,
    text: String,
    modifier: Modifier = Modifier,
    lottieModifier: Modifier = Modifier,
    isIconIterate: Boolean = false,
    backgroundColor: Color = Color.LightGray,
    textColor: Color = Color.Black,
) {
    val lottieComposition by rememberLottieComposition(spec = LottieCompositionSpec.RawRes(icon))
    val progress by animateLottieCompositionAsState(
        composition = lottieComposition,
        iterations = if (isIconIterate) LottieConstants.IterateForever else 1
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .size(170.dp, 200.dp)
            .background(backgroundColor)
    ) {
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LottieAnimation(
                composition = lottieComposition,
                progress = progress,
                modifier = lottieModifier.size(100.dp)
            )
            Text(
                text = text,
                style = MaterialTheme.typography.h6,
                color = textColor,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
    }
}

@Composable
fun HomeTopAppBar(
    isWithAction: Boolean = false,
    onDialogChange: (Boolean) -> Unit = {},
) {
    var openDialog by remember { mutableStateOf(false) }

    TopAppBar(
        title = {
            Text(text = "Trashify")
        },
        elevation = 0.dp,
        backgroundColor = Color.Transparent,
        actions = {
            if (isWithAction) {
                IconButton(onClick = { onDialogChange(!openDialog) }) {
                    Icon(imageVector = Icons.Default.MoreVert, contentDescription = null)
                }
            }
        }
    )
}

@Preview
@Composable
fun PreviewHomeScreen() {
    HomeScreen()
}