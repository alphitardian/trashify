package com.alphitardian.trashappta.presentation.history

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.alphitardian.trashappta.presentation.history.components.rememberMapViewWithLifecycle
import com.alphitardian.trashappta.presentation.home.HomeTopAppBar
import com.google.android.libraries.maps.CameraUpdateFactory
import com.google.android.libraries.maps.model.LatLng
import com.google.android.libraries.maps.model.MarkerOptions
import com.google.maps.android.ktx.awaitMap
import com.skydoves.landscapist.glide.GlideImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.alphitardian.trashappta.R
import com.alphitardian.trashappta.data.waste.remote.response.WasteHistoryResponse
import com.alphitardian.trashappta.presentation.solution.SolutionDialog
import com.alphitardian.trashappta.utils.Resource
import com.alphitardian.trashappta.utils.formatDate
import com.google.android.libraries.places.api.model.Place

@ExperimentalMaterialApi
@Composable
fun HistoryDetailScreen(
    waste: WasteHistoryResponse? = null,
    viewModel: HistoryViewModel? = null,
    navigateBack: () -> Unit = {},
) {
    val mapView = rememberMapViewWithLifecycle()
    val coroutineScope = rememberCoroutineScope()
    val scaffoldState = rememberBottomSheetScaffoldState()
    val placeId = viewModel?.placeId?.observeAsState()
    val placeDetail = viewModel?.place?.observeAsState()
    val wasteDetail = viewModel?.waste?.observeAsState()
    val context = LocalContext.current
    val isDialogOpen = remember { mutableStateOf(false) }

    LaunchedEffect(key1 = Unit) {
        waste?.let {
            viewModel?.getPlaceId(it.latitude, it.longitude)
            val alias = when (it.waste.name) {
                "Non-Organic" -> "no-or"
                else -> it.waste.name.lowercase()
            }
            viewModel?.getWasteByAlias(alias)
        }
    }

    LaunchedEffect(key1 = placeId?.value) {
        when (val value = placeId?.value) {
            is Resource.Success -> viewModel.getPlaceDetail(value.data)
            is Resource.Error -> Toast.makeText(
                context,
                "Failed to get place id",
                Toast.LENGTH_SHORT
            ).show()
            is Resource.Loading -> Unit
        }
    }

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetContent = {
            when (val value = placeDetail?.value) {
                is Resource.Success -> FrontLayerContent(
                    waste = waste,
                    placeDetail = value.data,
                    isDialogOpen = isDialogOpen
                )
                is Resource.Loading -> {
                    Box(modifier = Modifier.fillMaxSize()) {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }
                }
                is Resource.Error -> Toast.makeText(
                    context,
                    "Failed to get place detail",
                    Toast.LENGTH_SHORT
                ).show()
            }
        },
        sheetPeekHeight = 200.dp,
        sheetShape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        sheetElevation = 16.dp,
        content = {
            Box(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    AndroidView(
                        factory = { mapView },
                        update = {
                            coroutineScope.launch(Dispatchers.Main) {
                                val map = mapView.awaitMap()
                                val position = waste?.let { LatLng(it.latitude, it.longitude) }

                                map.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 18f))
                                val markerOptions = position?.let { MarkerOptions().position(it) }
                                map.addMarker(markerOptions)
                            }
                        }
                    )
                }
                FloatingActionButton(
                    onClick = navigateBack,
                    modifier = Modifier
                        .padding(20.dp)
                        .align(Alignment.TopStart),
                    backgroundColor = Color.White
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = null
                    )
                }
            }

            if (isDialogOpen.value) {
                when (val value = wasteDetail?.value) {
                    is Resource.Success -> SolutionDialog(
                        isDialogOpen = isDialogOpen,
                        waste = value.data.data,
                        wasteType = value.data.data?.name.orEmpty()
                    )
                    else -> Unit
                }
            }
        }
    )
}

@Composable
fun FrontLayerContent(
    waste: WasteHistoryResponse? = null,
    placeDetail: Place? = null,
    isDialogOpen: MutableState<Boolean>? = null,
) {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
    ) {
        val (placeNameRef, placeDetailRef, imageRef, wasteTypeRef, dateRef, infoButtonRef) = createRefs()

        Text(
            text = placeDetail?.name.orEmpty(),
            style = MaterialTheme.typography.h5,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Start,
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(placeNameRef) {
                    start.linkTo(parent.start)
                    top.linkTo(parent.top)
                    end.linkTo(parent.end)
                    width = Dimension.preferredWrapContent
                }
        )
        Text(
            text = placeDetail?.address.orEmpty(),
            style = MaterialTheme.typography.body1,
            textAlign = TextAlign.Start,
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(placeDetailRef) {
                    start.linkTo(parent.start)
                    top.linkTo(placeNameRef.bottom)
                    end.linkTo(parent.end)
                    width = Dimension.preferredWrapContent
                }
        )
        Card(
            modifier = Modifier
                .size(64.dp)
                .constrainAs(imageRef) {
                    start.linkTo(parent.start)
                    top.linkTo(placeDetailRef.bottom, margin = 16.dp)
                },
            shape = RoundedCornerShape(8.dp)
        ) {
            GlideImage(
                imageModel = waste?.url,
                previewPlaceholder = R.drawable.placeholder_image,
                error = painterResource(id = R.drawable.placeholder_image),
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
        Text(
            text = buildAnnotatedString {
                append("Waste Type: ")
                withStyle(
                    style = SpanStyle(
                        color = Color.Black,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.W700
                    )
                ) {
                    append(waste?.waste?.name.orEmpty())
                }
            },
            style = MaterialTheme.typography.body1,
            textAlign = TextAlign.Start,
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(wasteTypeRef) {
                    start.linkTo(imageRef.end, margin = 16.dp)
                    top.linkTo(placeDetailRef.bottom, margin = 24.dp)
                    end.linkTo(parent.end)
                    width = Dimension.preferredWrapContent
                }
        )
        Text(
            text = waste?.createdAt.orEmpty().formatDate(),
            style = MaterialTheme.typography.body2,
            textAlign = TextAlign.Start,
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(dateRef) {
                    start.linkTo(imageRef.end, margin = 16.dp)
                    top.linkTo(wasteTypeRef.bottom, margin = 4.dp)
                    end.linkTo(parent.end)
                    width = Dimension.preferredWrapContent
                }
        )
        IconButton(
            onClick = { isDialogOpen?.value = !isDialogOpen?.value!! },
            modifier = Modifier.constrainAs(infoButtonRef) {
                end.linkTo(parent.end)
                bottom.linkTo(parent.bottom, margin = 10.dp)
            }
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

@Preview
@Composable
fun PreviewFrontLayer() {
    FrontLayerContent()
}

@ExperimentalMaterialApi
@Preview
@Composable
fun PreviewHistoryDetailScreen() {
    HistoryDetailScreen()
}