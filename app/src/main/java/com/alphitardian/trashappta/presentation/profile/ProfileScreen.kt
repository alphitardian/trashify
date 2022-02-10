package com.alphitardian.trashappta.presentation.profile

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.alphitardian.trashappta.R
import com.alphitardian.trashappta.presentation.theme.PrimaryColor
import com.alphitardian.trashappta.utils.Resource
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.fade
import com.google.accompanist.placeholder.material.placeholder
import com.skydoves.landscapist.glide.GlideImage
import java.io.ByteArrayOutputStream
import java.util.*

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel? = null,
    navigateBack: () -> Unit = {},
    navigateToUpdateProfile: () -> Unit = {},
) {
    val scaffoldState = rememberScaffoldState()
    val profile = viewModel?.profile?.observeAsState()

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = { ProfileTopBar(navigateBack = navigateBack) },
        content = {
            when (val value = profile?.value) {
                is Resource.Success -> {
                    ProfileContent(
                        name = value.data.data.name,
                        email = value.data.data.email,
                        wasteCollected = value.data.data.wasteCollected,
                        isLoading = false,
                        viewModel = viewModel,
                        navigateToUpdateProfile = navigateToUpdateProfile
                    )
                }
                is Resource.Loading -> {
                    ProfileContent(
                        name = "",
                        email = "",
                        wasteCollected = "",
                        isLoading = true
                    )
                }
                else -> Unit
            }
        }
    )
}

@Composable
fun ProfileTopBar(navigateBack: () -> Unit) {
    TopAppBar(
        title = { Text(text = "Profile") },
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
fun ProfileContent(
    name: String,
    email: String,
    wasteCollected: String,
    isLoading: Boolean,
    viewModel: ProfileViewModel? = null,
    navigateToUpdateProfile: () -> Unit = {},
) {
    val context = LocalContext.current
    val images = viewModel?.getImage?.collectAsState(initial = null)
    val imageUrl = remember { mutableStateOf<Any?>(null) }
    val pickPhotoLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) {
            if (it != null) {
                val base64Image = convertToBase64(it, context)
                base64Image?.let { image ->
                    viewModel?.uploadImage(image)
                }
            }
        }

    ConstraintLayout(modifier = Modifier.fillMaxSize()) {
        val (profileImageRef, nameRef, emailRef, wasteDescRef, wasteCollectedRef, updateButtonRef) = createRefs()

        Card(
            modifier = Modifier
                .size(200.dp)
                .constrainAs(profileImageRef) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    top.linkTo(parent.top, margin = 64.dp)
                }
                .placeholder(
                    visible = isLoading,
                    highlight = PlaceholderHighlight.fade(),
                    shape = RoundedCornerShape(100.dp),
                    color = Color.LightGray
                ),
            shape = RoundedCornerShape(100.dp)
        ) {
            GlideImage(
                imageModel = if (images?.value != null) images.value?.imageUrl
                else painterResource(id = R.drawable.placeholder_image),
                error = painterResource(id = R.drawable.placeholder_image),
                previewPlaceholder = R.drawable.placeholder_image,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        Text(
            text = name,
            style = MaterialTheme.typography.h5,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .constrainAs(nameRef) {
                    start.linkTo(parent.start, margin = 32.dp)
                    end.linkTo(parent.end, margin = 20.dp)
                    top.linkTo(profileImageRef.bottom, margin = 20.dp)
                }
                .placeholder(
                    visible = isLoading,
                    highlight = PlaceholderHighlight.fade(),
                    shape = RoundedCornerShape(8.dp),
                    color = Color.LightGray
                ),
        )

        Text(
            text = email,
            style = MaterialTheme.typography.body1,
            color = Color.Gray,
            modifier = Modifier
                .constrainAs(emailRef) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    top.linkTo(nameRef.bottom, margin = 8.dp)
                }
                .placeholder(
                    visible = isLoading,
                    highlight = PlaceholderHighlight.fade(),
                    shape = RoundedCornerShape(8.dp),
                    color = Color.LightGray
                ),
        )

        Text(
            text = "Waste Collected:",
            style = MaterialTheme.typography.body1,
            modifier = Modifier
                .constrainAs(wasteDescRef) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    top.linkTo(emailRef.bottom, 15.dp)
                }
                .placeholder(
                    visible = isLoading,
                    highlight = PlaceholderHighlight.fade(),
                    shape = RoundedCornerShape(8.dp),
                    color = Color.LightGray
                ),
        )

        Text(
            text = wasteCollected,
            style = MaterialTheme.typography.h3,
            modifier = Modifier
                .constrainAs(wasteCollectedRef) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    top.linkTo(wasteDescRef.bottom, margin = 2.dp)
                }
                .placeholder(
                    visible = isLoading,
                    highlight = PlaceholderHighlight.fade(),
                    shape = RoundedCornerShape(8.dp),
                    color = Color.LightGray
                ),
        )

        Card(
            modifier = Modifier
                .clickable { pickPhotoLauncher.launch("image/*") }
                .size(48.dp)
                .constrainAs(updateButtonRef) {
                    end.linkTo(profileImageRef.end, margin = 4.dp)
                    bottom.linkTo(profileImageRef.bottom, margin = 4.dp)
                },
            shape = RoundedCornerShape(20.dp),
            backgroundColor = PrimaryColor
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                modifier = Modifier.padding(10.dp)
            )
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

@Preview(device = Devices.PIXEL_2)
@Composable
fun PreviewScreen() {
    ProfileScreen()
}