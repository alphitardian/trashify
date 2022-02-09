package com.alphitardian.trashappta.presentation.detection

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.core.content.FileProvider
import com.airbnb.lottie.compose.*
import com.alphitardian.trashappta.BuildConfig
import com.alphitardian.trashappta.R
import com.alphitardian.trashappta.presentation.home.HomeTopAppBar
import com.alphitardian.trashappta.presentation.shared_components.LoadingIndicator
import com.alphitardian.trashappta.presentation.solution.SolutionScreen
import com.alphitardian.trashappta.utils.Resource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.mlkit.common.model.LocalModel
import com.skydoves.landscapist.glide.GlideImage
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.*

@ExperimentalMaterialApi
@Composable
fun DetectionScreen(viewModel: DetectionViewModel? = null) {
    val detection = viewModel?.detection?.observeAsState()
    val image = viewModel?.image?.observeAsState()
    val scaffoldState = rememberBottomSheetScaffoldState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(key1 = image?.value, block = {
        when (image?.value) {
            is Resource.Success -> scaffoldState.bottomSheetState.expand()
        }
    })

    Box(modifier = Modifier.fillMaxSize()) {
        BottomSheetScaffold(
            scaffoldState = scaffoldState,
            sheetPeekHeight = 0.dp,
            sheetContent = {
                when (detection?.value) {
                    is Resource.Success -> {
                        SolutionScreen(
                            closeSheet = {
                                coroutineScope.launch {
                                    scaffoldState.bottomSheetState.collapse()
                                }
                            },
                            viewModel = viewModel
                        )
                    }
                }
            },
            topBar = { HomeTopAppBar() },
            content = { DetectionContent(viewModel) }
        )
        when (detection?.value) {
            is Resource.Loading -> {
                LoadingIndicator()
            }
        }
    }
}

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun DetectionContent(
    viewModel: DetectionViewModel? = null,
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val imgUri = remember { mutableStateOf<Uri?>(null) }
    val tempUri = remember { mutableStateOf<Uri?>(null) }
    val result = viewModel?.result?.value
    var showPicture by remember { mutableStateOf(false) }
    val lottieComposition by rememberLottieComposition(
        spec = LottieCompositionSpec.RawRes(R.raw.walking)
    )
    val progress by animateLottieCompositionAsState(
        composition = lottieComposition,
        iterations = LottieConstants.IterateForever
    )

    LaunchedEffect(key1 = Unit) { viewModel?.getCurrentLocation() }

    val localModel = LocalModel.Builder()
        .setAssetFilePath("model.tflite")
        .build()

    val pickPhotoLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) {
            if (it != null) {
                imgUri.value = it
                showPicture = true
                val base64Image = convertToBase64(it, context)
                base64Image?.let { image ->
                    viewModel?.uploadImage(image)
                }

                viewModel?.detectImage(
                    localModel = localModel,
                    context = context,
                    uriImg = it,
                )
            }
        }

    val takePhotoLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                tempUri.value?.let {
                    imgUri.value = it
                    showPicture = true
                    val base64Image = convertToBase64(it, context)
                    base64Image?.let { image ->
                        viewModel?.uploadImage(image)
                    }

                    viewModel?.detectImage(
                        localModel = localModel,
                        context = context,
                        uriImg = it,
                    )
                }
            }
        }

    ConstraintLayout(modifier = Modifier.fillMaxSize()) {
        val (imageRef, resultTextRef, descRef, takePhotoButtonRef, pickPhotoButtonRef) = createRefs()

        if (showPicture) {
            GlideImage(
                imageModel = imgUri.value,
                error = painterResource(id = R.drawable.placeholder_image),
                placeHolder = painterResource(id = R.drawable.placeholder_image),
                previewPlaceholder = R.drawable.placeholder_image,
                requestOptions = {
                    RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .centerCrop()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp)
                    .constrainAs(imageRef) {
                        start.linkTo(parent.start, margin = 20.dp)
                        end.linkTo(parent.end, margin = 20.dp)
                        top.linkTo(parent.top, margin = 20.dp)
                    }
            )
        } else {
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
                    }
            )
        }

        if (result?.isNotEmpty() == true) {
            val text = "${viewModel.extractWasteConfidence()}% ${viewModel.extractWasteType()}"
            Text(
                text = text,
                style = MaterialTheme.typography.h4,
                fontWeight = FontWeight.W800,
                modifier = Modifier.constrainAs(resultTextRef) {
                    start.linkTo(parent.start, margin = 20.dp)
                    end.linkTo(parent.end, margin = 20.dp)
                    top.linkTo(imageRef.bottom, margin = 32.dp)
                }
            )
            Text(
                text = "Congrats! You have found a waste!",
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .constrainAs(descRef) {
                        start.linkTo(parent.start, margin = 20.dp)
                        end.linkTo(parent.end, margin = 20.dp)
                        top.linkTo(resultTextRef.bottom, margin = 8.dp)
                        width = Dimension.preferredWrapContent
                    }
            )
        } else {
            Text(
                text = "Let's go find some waste to help our world!",
                style = MaterialTheme.typography.h6,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .constrainAs(resultTextRef) {
                        start.linkTo(parent.start, margin = 20.dp)
                        end.linkTo(parent.end, margin = 20.dp)
                        top.linkTo(imageRef.bottom, margin = 64.dp)
                        width = Dimension.preferredWrapContent
                    }
            )
            Text(
                text = "Please ask your parent to accompany you!",
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .constrainAs(descRef) {
                        start.linkTo(parent.start, margin = 20.dp)
                        end.linkTo(parent.end, margin = 20.dp)
                        top.linkTo(resultTextRef.bottom, margin = 8.dp)
                        width = Dimension.preferredWrapContent
                    }
            )
        }

        Button(
            onClick = {
                coroutineScope.launch {
                    val tmpFile =
                        File.createTempFile("tmp_image_file", ".png", context.cacheDir).apply {
                            createNewFile()
                            deleteOnExit()
                        }
                    val uri =
                        FileProvider.getUriForFile(
                            context.applicationContext,
                            "${BuildConfig.APPLICATION_ID}.provider",
                            tmpFile
                        )
                    uri?.let {
                        tempUri.value = it
                        takePhotoLauncher.launch(it)
                    }
                }
            },
            shape = RoundedCornerShape(28.dp),
            modifier = Modifier
                .height(52.dp)
                .fillMaxWidth()
                .constrainAs(takePhotoButtonRef) {
                    start.linkTo(parent.start, margin = 20.dp)
                    end.linkTo(parent.end, margin = 20.dp)
                    bottom.linkTo(pickPhotoButtonRef.top, margin = 16.dp)
                    width = Dimension.preferredWrapContent
                }
        ) {
            Text(text = "Take Photo")
        }
        Button(
            onClick = { pickPhotoLauncher.launch("image/*") },
            shape = RoundedCornerShape(28.dp),
            modifier = Modifier
                .height(52.dp)
                .fillMaxWidth()
                .constrainAs(pickPhotoButtonRef) {
                    start.linkTo(parent.start, margin = 20.dp)
                    end.linkTo(parent.end, margin = 20.dp)
                    bottom.linkTo(parent.bottom, margin = 20.dp)
                    width = Dimension.preferredWrapContent
                }
        ) {
            Text(text = "Pick Photo")
        }
    }
}

private fun convertToBase64(uri: Uri, context: Context): String? {
    val imageStream = context.contentResolver.openInputStream(uri)
    val selectedImageBitmap = BitmapFactory.decodeStream(imageStream)
    val baos = ByteArrayOutputStream()
    selectedImageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
    val byte = baos.toByteArray()
    return Base64.getEncoder().encodeToString(byte)
}

@ExperimentalMaterialApi
@Preview
@Composable
fun PreviewScreen() {
    DetectionScreen()
}