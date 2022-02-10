package com.alphitardian.trashappta.presentation.profile

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.alphitardian.trashappta.R
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.fade
import com.google.accompanist.placeholder.material.placeholder
import com.skydoves.landscapist.glide.GlideImage

@Composable
fun UpdateProfileScreen(
    viewModel: ProfileViewModel? = null,
    navigateBack: () -> Unit = {},
) {
    val scaffoldState = rememberScaffoldState()

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = { UpdateProfileTopBar(navigateBack = navigateBack)},
        content = { UpdateProfileContent(viewModel) }
    )
}

@Composable
fun UpdateProfileTopBar(navigateBack: () -> Unit) {
    TopAppBar(
        title = { Text(text = "Update Profile") },
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
fun UpdateProfileContent(
    viewModel: ProfileViewModel? = null,
    isLoading: Boolean = false,
) {
    ConstraintLayout(modifier = Modifier.fillMaxSize()) {
        val (imageRef, nameFieldRef, emailFieldRef, buttonRef) = createRefs()

        Card(
            modifier = Modifier
                .size(200.dp)
                .constrainAs(imageRef) {
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

        OutlinedTextField(
            value = viewModel?.userName?.value.orEmpty(),
            onValueChange = { viewModel?.setUserName(it) },
            placeholder = { Text(text = "Type name here") },
            label = { Text(text = "Name") },
            shape = RoundedCornerShape(28.dp),
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(nameFieldRef) {
                    start.linkTo(parent.start, margin = 20.dp)
                    end.linkTo(parent.end, margin = 20.dp)
                    top.linkTo(imageRef.bottom, margin = 20.dp)
                    width = Dimension.preferredWrapContent
                }
        )

        OutlinedTextField(
            value = viewModel?.userEmail?.value.orEmpty(),
            onValueChange = { viewModel?.setUserEmail(it) },
            placeholder = { Text(text = "Type email here") },
            label = { Text(text = "Email") },
            shape = RoundedCornerShape(28.dp),
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(emailFieldRef) {
                    start.linkTo(parent.start, margin = 20.dp)
                    end.linkTo(parent.end, margin = 20.dp)
                    top.linkTo(nameFieldRef.bottom, margin = 20.dp)
                    width = Dimension.preferredWrapContent
                }
        )

        Button(
            onClick = { viewModel?.updateUserProfile() },
            shape = RoundedCornerShape(28.dp),
            modifier = Modifier
                .height(52.dp)
                .fillMaxWidth()
                .constrainAs(buttonRef) {
                    start.linkTo(parent.start, margin = 20.dp)
                    end.linkTo(parent.end, margin = 20.dp)
                    bottom.linkTo(parent.bottom, margin = 12.dp)
                    width = Dimension.preferredWrapContent
                }
        ) {
            Text(text = "Update Profile")
        }
    }
}

@Preview
@Composable
fun PreviewUpdateProfile() {
    UpdateProfileScreen()
}