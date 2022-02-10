package com.alphitardian.trashappta.presentation.profile

import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun UpdateProfileScreen(
    navigateBack: () -> Unit = {},
) {
    val scaffoldState = rememberScaffoldState()

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = { UpdateProfileTopBar(navigateBack = navigateBack)},
        content = {}
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

@Preview
@Composable
fun PreviewUpdateProfile() {
    UpdateProfileScreen()
}