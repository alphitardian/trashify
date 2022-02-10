package com.alphitardian.trashappta.presentation.login

import android.Manifest
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.airbnb.lottie.compose.*
import com.alphitardian.trashappta.R
import com.alphitardian.trashappta.presentation.home.HomeTopAppBar
import com.alphitardian.trashappta.presentation.shared_components.LoadingIndicator
import com.alphitardian.trashappta.utils.Resource
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.skydoves.landscapist.glide.GlideImage
import kotlinx.coroutines.launch

@ExperimentalPermissionsApi
@Composable
fun LoginScreen(
    viewModel: LoginViewModel? = null,
    navigateToHome: () -> Unit = {},
    navigateToRegister: () -> Unit = {},
) {
    val scaffoldState = rememberScaffoldState()
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val permissionState =
        rememberPermissionState(permission = Manifest.permission.ACCESS_FINE_LOCATION)

    val login = viewModel?.login?.observeAsState()

    LaunchedEffect(key1 = login?.value) {
        when (login?.value) {
            is Resource.Success -> navigateToHome()
            is Resource.Error -> Toast.makeText(
                context,
                "Please check your email/password",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(key1 = lifecycleOwner, effect = {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_START) {
                val locationRequest = LocationRequest.create().apply {
                    interval = 10000
                    fastestInterval = 5000
                    priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                }
                val task = LocationServices.getSettingsClient(context).checkLocationSettings(
                    LocationSettingsRequest.Builder()
                        .addLocationRequest(locationRequest)
                        .build()
                )

                task
                    .addOnSuccessListener {
                        permissionState.launchPermissionRequest()
                    }
                    .addOnFailureListener {
                        coroutineScope.launch {
                            scaffoldState.snackbarHostState.showSnackbar("Please turn on your location")
                        }
                    }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    })


    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            scaffoldState = scaffoldState,
            topBar = { HomeTopAppBar() },
            content = { LoginContent(viewModel, navigateToRegister) },
        )
        when (login?.value) {
            is Resource.Loading -> {
                LoadingIndicator()
            }
            else -> Unit
        }
    }
}

@Composable
fun LoginContent(
    viewModel: LoginViewModel? = null,
    navigateToRegister: () -> Unit = {},
) {
    val greetingAnimationSpec by rememberLottieComposition(spec = LottieCompositionSpec.RawRes(R.raw.girl_say_hi))
    val progress by animateLottieCompositionAsState(
        composition = greetingAnimationSpec,
        iterations = LottieConstants.IterateForever
    )

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
    ) {
        val (emailFieldRef, passwordFieldRef, loginButtonRef, imageRef, greetingRef, greetingDescRef, registerButtonRef) = createRefs()
        
        LottieAnimation(
            composition = greetingAnimationSpec,
            progress = progress,
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .constrainAs(imageRef) {
                    start.linkTo(parent.start)
                    top.linkTo(parent.top)
                    end.linkTo(parent.end)
                }
        )

        Text(
            text = "Welcome to Trashify!",
            style = MaterialTheme.typography.h5,
            modifier = Modifier.constrainAs(greetingRef) {
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                top.linkTo(imageRef.bottom, margin = 12.dp)
            }
        )
        Text(
            text = "Please ask your parent to login your account.",
            modifier = Modifier.constrainAs(greetingDescRef) {
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                top.linkTo(greetingRef.bottom, margin = 6.dp)
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
                    top.linkTo(greetingDescRef.bottom, margin = 20.dp)
                    width = Dimension.preferredWrapContent
                }
        )

        var passwordVisibility by remember { mutableStateOf(false) }
        OutlinedTextField(
            value = viewModel?.password?.value.orEmpty(),
            onValueChange = { viewModel?.setPassword(it) },
            placeholder = { Text(text = "Type password here") },
            label = { Text(text = "Password") },
            visualTransformation = if (!passwordVisibility) PasswordVisualTransformation()
            else VisualTransformation.None,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            shape = RoundedCornerShape(28.dp),
            trailingIcon = {
                IconButton(onClick = { passwordVisibility = !passwordVisibility }) {
                    Icon(
                        painter = if (!passwordVisibility) painterResource(id = R.drawable.ic_baseline_visibility_24)
                        else painterResource(id = R.drawable.ic_baseline_visibility_off_24),
                        contentDescription = null
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(passwordFieldRef) {
                    start.linkTo(parent.start, margin = 20.dp)
                    end.linkTo(parent.end, margin = 20.dp)
                    top.linkTo(emailFieldRef.bottom, margin = 10.dp)
                    width = Dimension.preferredWrapContent
                }
        )
        Button(
            onClick = {
                if (viewModel?.checkFieldValidation() == true) viewModel.loginAccount()
            },
            shape = RoundedCornerShape(28.dp),
            modifier = Modifier
                .height(52.dp)
                .fillMaxWidth()
                .constrainAs(loginButtonRef) {
                    start.linkTo(parent.start, margin = 20.dp)
                    end.linkTo(parent.end, margin = 20.dp)
                    bottom.linkTo(registerButtonRef.top, margin = 12.dp)
                    width = Dimension.preferredWrapContent
                }
        ) {
            Text(text = "Login")
        }
        TextButton(
            onClick = navigateToRegister,
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

@ExperimentalPermissionsApi
@Composable
@Preview
private fun PreviewScreen() {
    LoginScreen()
}