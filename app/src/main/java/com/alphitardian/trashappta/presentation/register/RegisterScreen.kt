package com.alphitardian.trashappta.presentation.register

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.airbnb.lottie.compose.*
import com.alphitardian.trashappta.R
import com.alphitardian.trashappta.presentation.home.HomeTopAppBar
import com.alphitardian.trashappta.presentation.shared_components.LoadingIndicator
import com.alphitardian.trashappta.utils.Resource

@Composable
fun RegisterScreen(
    viewModel: RegisterViewModel? = null,
    navigateToLogin: () -> Unit = {},
) {
    val scaffoldState = rememberScaffoldState()
    val register = viewModel?.register?.observeAsState()

    LaunchedEffect(key1 = register?.value) {
        when (register?.value) {
            is Resource.Success -> navigateToLogin()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            scaffoldState = scaffoldState,
            topBar = { HomeTopAppBar() },
            content = {
                RegisterContent(viewModel)
            }
        )
        when (register?.value) {
            is Resource.Loading -> {
                LoadingIndicator()
            }
        }
    }
}

@Composable
fun RegisterContent(viewModel: RegisterViewModel? = null) {
    val lottieComposition by rememberLottieComposition(spec = LottieCompositionSpec.RawRes(R.raw.register))
    val progress by animateLottieCompositionAsState(
        composition = lottieComposition,
        iterations = LottieConstants.IterateForever
    )

    ConstraintLayout(modifier = Modifier.fillMaxSize()) {
        val (nameFieldRef, emailFieldRef, passwordFieldRef, registerButtonRef, imageRef, greetingRef, greetingDescRef) = createRefs()

        LottieAnimation(
            composition = lottieComposition,
            progress = progress,
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .constrainAs(imageRef) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    top.linkTo(parent.top)
                }
        )

        Text(
            text = "Get Yourself Registered!",
            style = MaterialTheme.typography.h5,
            modifier = Modifier.constrainAs(greetingRef) {
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                top.linkTo(imageRef.bottom, margin = 12.dp)
            }
        )
        Text(
            text = "Please ask your parent to register your account",
            modifier = Modifier.constrainAs(greetingDescRef) {
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                top.linkTo(greetingRef.bottom, margin = 6.dp)
            }
        )

        OutlinedTextField(
            value = viewModel?.name?.value.orEmpty(),
            onValueChange = { viewModel?.setName(it) },
            placeholder = { Text(text = "Type name here") },
            label = { Text(text = "Name") },
            shape = RoundedCornerShape(28.dp),
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(nameFieldRef) {
                    start.linkTo(parent.start, margin = 20.dp)
                    end.linkTo(parent.end, margin = 20.dp)
                    top.linkTo(greetingDescRef.bottom, margin = 20.dp)
                    width = Dimension.preferredWrapContent
                }
        )
        OutlinedTextField(
            value = viewModel?.email?.value.orEmpty(),
            onValueChange = { viewModel?.setEmail(it) },
            placeholder = { Text(text = "Type email here") },
            label = { Text(text = "Email") },
            shape = RoundedCornerShape(28.dp),
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(emailFieldRef) {
                    start.linkTo(parent.start, margin = 20.dp)
                    end.linkTo(parent.end, margin = 20.dp)
                    top.linkTo(nameFieldRef.bottom, margin = 8.dp)
                    width = Dimension.preferredWrapContent

                }
        )
        OutlinedTextField(
            value = viewModel?.password?.value.orEmpty(),
            onValueChange = { viewModel?.setPassword(it) },
            placeholder = { Text(text = "Type password here") },
            label = { Text(text = "Password") },
            shape = RoundedCornerShape(28.dp),
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(passwordFieldRef) {
                    start.linkTo(parent.start, margin = 20.dp)
                    end.linkTo(parent.end, margin = 20.dp)
                    top.linkTo(emailFieldRef.bottom, margin = 8.dp)
                    width = Dimension.preferredWrapContent
                }
        )
        Button(
            onClick = {
                if (viewModel?.checkFieldValidation() == true) viewModel.registerAccount()
            },
            shape = RoundedCornerShape(28.dp),
            modifier = Modifier
                .height(52.dp)
                .fillMaxWidth()
                .constrainAs(registerButtonRef) {
                    start.linkTo(parent.start, margin = 20.dp)
                    end.linkTo(parent.end, margin = 20.dp)
                    bottom.linkTo(parent.bottom, margin = 20.dp)
                    width = Dimension.preferredWrapContent
                }
        ) {
            Text(text = "Register")
        }
    }
}

@Preview
@Composable
private fun PreviewScreen() {
    RegisterScreen()
}