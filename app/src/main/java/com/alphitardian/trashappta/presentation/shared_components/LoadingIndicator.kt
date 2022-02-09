package com.alphitardian.trashappta.presentation.shared_components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.alphitardian.trashappta.presentation.theme.PrimaryColor

@Composable
fun LoadingIndicator() {
    Box(
        modifier = Modifier
            .background(Color.DarkGray.copy(0.35f))
            .fillMaxSize(),
    ) {
        CircularProgressIndicator(
            color = PrimaryColor,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Preview
@Composable
fun PreviewIndicator() {
    LoadingIndicator()
}