package com.alphitardian.trashappta.presentation.profile

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.alphitardian.trashappta.R
import com.alphitardian.trashappta.data.user.remote.response.ProfileResponse
import com.alphitardian.trashappta.presentation.home.HomeTopAppBar
import com.alphitardian.trashappta.utils.Resource
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.fade
import com.google.accompanist.placeholder.material.placeholder
import com.skydoves.landscapist.glide.GlideImage

@Composable
fun ProfileScreen(viewModel: ProfileViewModel? = null) {
    val scaffoldState = rememberScaffoldState()
    val profile = viewModel?.profile?.observeAsState()

    LaunchedEffect(key1 = Unit) { viewModel?.getUserProfile() }

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = { HomeTopAppBar() },
        content = {
            when (val value = profile?.value) {
                is Resource.Success -> {
                    ProfileContent(
                        name = value.data.data.name,
                        email = value.data.data.email,
                        wasteCollected = value.data.data.wasteCollected,
                        isLoading = false
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
            }
        }
    )
}

@Composable
fun ProfileContent(
    name: String,
    email: String,
    wasteCollected: String,
    isLoading: Boolean,
) {
    ConstraintLayout(modifier = Modifier.fillMaxSize()) {
        val (profileImageRef, nameRef, emailRef, wasteDescRef, wasteCollectedRef) = createRefs()

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
                imageModel = painterResource(id = R.drawable.placeholder_image),
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
    }
}

@Preview(device = Devices.PIXEL_2)
@Composable
fun PreviewScreen() {
    ProfileScreen()
}