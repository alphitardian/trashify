package com.alphitardian.trashappta.presentation.solution

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.alphitardian.trashappta.R
import com.alphitardian.trashappta.data.waste.remote.response.WasteDataResponse
import com.alphitardian.trashappta.data.waste.remote.response.WasteRequest
import com.alphitardian.trashappta.data.waste.remote.response.WasteResponse
import com.alphitardian.trashappta.presentation.detection.DetectionViewModel
import com.alphitardian.trashappta.utils.Resource
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.fade
import com.google.accompanist.placeholder.material.placeholder
import com.google.android.libraries.places.api.model.Place

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun SolutionScreen(
    viewModel: DetectionViewModel? = null,
    closeSheet: () -> Unit = {},
) {
    val context = LocalContext.current
    val waste = viewModel?.waste?.observeAsState()
    val uploadImage = viewModel?.image?.observeAsState()
    val uploadWaste = viewModel?.uploadWaste?.observeAsState()
    val currentLocation = viewModel?.location?.observeAsState()
    var wasteResult by remember { mutableStateOf<WasteResponse<WasteDataResponse>?>(null) }
    var userId by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("") }
    var locationLatLng by remember { mutableStateOf<Place?>(null) }

    LaunchedEffect(key1 = Unit) {
        val response = viewModel?.extractWasteType()
        if (!response.isNullOrEmpty()) viewModel.getWasteType(response)
        userId = viewModel?.getUserId().toString()
    }

    LaunchedEffect(key1 = waste?.value) {
        when (val value = waste?.value) {
            is Resource.Success -> wasteResult = value.data
        }
    }

    LaunchedEffect(key1 = uploadImage?.value) {
        when (val value = uploadImage?.value) {
            is Resource.Success -> imageUrl = value.data.data.displayUrl
        }
    }

    LaunchedEffect(key1 = currentLocation?.value) {
        when (val value = currentLocation?.value) {
            is Resource.Success -> locationLatLng = value.data
        }
    }

    LaunchedEffect(key1 = uploadWaste?.value) {
        when (uploadWaste?.value) {
            is Resource.Success -> Toast.makeText(context, "Upload success!", Toast.LENGTH_SHORT)
                .show()
            is Resource.Error -> Toast.makeText(context, "Upload failed.", Toast.LENGTH_SHORT)
                .show()
        }
    }

    if (imageUrl.isNotEmpty() && userId.isNotEmpty() && !viewModel?.wasteCategory?.value.isNullOrEmpty()) {
        val request = WasteRequest(
            userId = userId,
            url = imageUrl,
            latitude = locationLatLng?.latLng?.latitude ?: 0.0,
            longitude = locationLatLng?.latLng?.longitude ?: 0.0,
            wasteCategory = viewModel?.wasteCategory?.value.orEmpty()
        )
        LaunchedEffect(key1 = Unit) { viewModel?.sendWasteHistory(request) }
    }

    ConstraintLayout(
        modifier = Modifier
            .background(Color.White)
            .fillMaxSize(),
    ) {
        val (imageRef, titleRef, descRef, backButtonRef, loadingRef, pageTitleRef, solutionSectionRef, solutionRef) = createRefs()
        val wasteType = viewModel?.extractWasteType().orEmpty()

        if (waste?.value is Resource.Loading) {
            CircularProgressIndicator(
                modifier = Modifier.constrainAs(loadingRef) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                }
            )
        }

        Text(
            text = "Solution",
            style = MaterialTheme.typography.h5,
            fontWeight = FontWeight.W700,
            modifier = Modifier.constrainAs(pageTitleRef) {
                start.linkTo(parent.start, margin = 20.dp)
                top.linkTo(parent.top, margin = 20.dp)
            }
        )

        IconButton(
            onClick = closeSheet,
            modifier = Modifier.constrainAs(backButtonRef) {
                end.linkTo(parent.end, margin = 14.dp)
                top.linkTo(parent.top, margin = 14.dp)
            }
        ) {
            Icon(imageVector = Icons.Default.Close, contentDescription = null)
        }

        Card(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .fillMaxWidth()
                .height(300.dp)
                .constrainAs(imageRef) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    top.linkTo(backButtonRef.bottom, margin = 16.dp)
                }
                .placeholder(
                    visible = waste?.value is Resource.Loading,
                    highlight = PlaceholderHighlight.fade(),
                    shape = RoundedCornerShape(16.dp)
                ),
            shape = RoundedCornerShape(16.dp)
        ) {
            val image = when (wasteType) {
                "Organic" -> R.drawable.organic_waste
                "No-or" -> R.drawable.non_organic_waste
                "Poison" -> R.drawable.poison_waste
                "Recyclable" -> R.drawable.recyclable_waste
                else -> R.drawable.placeholder_image
            }
            Image(
                painter = painterResource(id = image),
                contentDescription = null,
                contentScale = ContentScale.Crop,
            )
        }

        Text(
            text = wasteType,
            style = MaterialTheme.typography.h4,
            textAlign = TextAlign.Start,
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(titleRef) {
                    start.linkTo(parent.start, margin = 20.dp)
                    end.linkTo(parent.end, margin = 20.dp)
                    top.linkTo(imageRef.bottom, margin = 32.dp)
                    width = Dimension.preferredWrapContent
                }
                .placeholder(
                    visible = waste?.value is Resource.Loading,
                    highlight = PlaceholderHighlight.fade(),
                    shape = RoundedCornerShape(8.dp)
                )
        )
        Text(
            text = wasteResult?.data?.description.orEmpty(),
            textAlign = TextAlign.Start,
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(descRef) {
                    start.linkTo(parent.start, margin = 20.dp)
                    end.linkTo(parent.end, margin = 20.dp)
                    top.linkTo(titleRef.bottom, margin = 8.dp)
                    width = Dimension.preferredWrapContent
                }
                .placeholder(
                    visible = waste?.value is Resource.Loading,
                    highlight = PlaceholderHighlight.fade(),
                    shape = RoundedCornerShape(8.dp)
                )
        )
        Text(
            text = "Solution",
            style = MaterialTheme.typography.h6,
            modifier = Modifier
                .constrainAs(solutionSectionRef) {
                    start.linkTo(parent.start, margin = 20.dp)
                    top.linkTo(descRef.bottom, margin = 16.dp)
                }
                .placeholder(
                    visible = waste?.value is Resource.Loading,
                    highlight = PlaceholderHighlight.fade(),
                    shape = RoundedCornerShape(8.dp)
                )
        )
        Text(
            text = wasteResult?.data?.solution?.description.orEmpty(),
            textAlign = TextAlign.Start,
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(solutionRef) {
                    start.linkTo(parent.start, margin = 20.dp)
                    end.linkTo(parent.end, margin = 20.dp)
                    top.linkTo(solutionSectionRef.bottom, margin = 8.dp)
                    width = Dimension.preferredWrapContent
                }
                .placeholder(
                    visible = waste?.value is Resource.Loading,
                    highlight = PlaceholderHighlight.fade(),
                    shape = RoundedCornerShape(8.dp)
                )
        )
    }
}

@Preview(device = Devices.PIXEL_2)
@Composable
private fun PreviewScreen() {
    SolutionScreen()
}