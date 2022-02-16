package com.alphitardian.trashappta.presentation.bank

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.alphitardian.trashappta.R
import com.alphitardian.trashappta.presentation.history.components.rememberMapViewWithLifecycle
import com.alphitardian.trashappta.utils.Resource
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.fade
import com.google.accompanist.placeholder.material.placeholder
import com.google.android.libraries.maps.CameraUpdateFactory
import com.google.android.libraries.maps.model.LatLng
import com.google.android.libraries.maps.model.MarkerOptions
import com.google.maps.android.ktx.awaitMap
import com.google.maps.model.PlacesSearchResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.URLEncoder

@ExperimentalPagerApi
@Composable
fun WasteBankScreen(
    viewModel: WasteBankViewModel? = null,
    navigateToMaps: (latitude: Double, longitude: Double, query: String) -> Unit = { _, _, _ -> },
    navigateBack: () -> Unit = {},
) {
    val scaffoldState = rememberScaffoldState()
    val mapView = rememberMapViewWithLifecycle()
    val coroutineScope = rememberCoroutineScope()
    val pagerState = rememberPagerState()
    val recomposeScope = currentRecomposeScope
    val context = LocalContext.current
    val currentLocation = viewModel?.location?.observeAsState()
    val nearbyPlace = viewModel?.nearbyPlace?.observeAsState()
    var placeDataResults by remember { mutableStateOf<List<PlacesSearchResult>>(listOf()) }
    var openDialog by remember { mutableStateOf(true) }

    val lottieComposition by rememberLottieComposition(spec = LottieCompositionSpec.RawRes(R.raw.question_mark))
    val progress by animateLottieCompositionAsState(composition = lottieComposition)

    LaunchedEffect(key1 = currentLocation?.value) {
        when (currentLocation?.value) {
            is Resource.Success -> viewModel.getNearbyBank()
            else -> Unit
        }
    }

    LaunchedEffect(key1 = nearbyPlace?.value) {
        when (val value = nearbyPlace?.value) {
            is Resource.Success -> placeDataResults = value.data
            else -> Unit
        }
    }

    LaunchedEffect(key1 = pagerState.currentPage) { recomposeScope.invalidate() } // Trigger change

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = { },
        content = {
            Box(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .align(Alignment.Center)
                ) {
                    AndroidView(
                        factory = { mapView },
                        update = {
                            coroutineScope.launch(Dispatchers.Main) {
                                val map = mapView.awaitMap()

                                if (placeDataResults.isNotEmpty()) {
                                    map.clear()

                                    val latlngValue =
                                        placeDataResults[pagerState.currentPage].geometry.location
                                    val position = LatLng(latlngValue.lat, latlngValue.lng)

                                    map.animateCamera(
                                        CameraUpdateFactory.newLatLngZoom(
                                            position,
                                            18f
                                        )
                                    )
                                    val markerOptions = MarkerOptions().position(position)
                                    map.addMarker(markerOptions)
                                }
                            }
                        }
                    )
                }
                if (placeDataResults.isNotEmpty()) {
                    HorizontalPager(
                        count = placeDataResults.size,
                        state = pagerState,
                        modifier = Modifier
                            .height(200.dp)
                            .align(Alignment.BottomCenter)
                    ) { index ->
                        MapCard(
                            title = placeDataResults[index].name.orEmpty(),
                            description = placeDataResults[index].vicinity.orEmpty(),
                            rating = placeDataResults[index].rating,
                            navigateToMaps = {
                                val latitude = placeDataResults[index].geometry.location.lat
                                val longitude = placeDataResults[index].geometry.location.lng
                                val query = URLEncoder.encode(
                                    placeDataResults[index].name.orEmpty(),
                                    "utf-8"
                                )
                                navigateToMaps(latitude, longitude, query)
                            }
                        )
                    }
                }
                when (nearbyPlace?.value) {
                    is Resource.Success -> Unit
                    is Resource.Loading -> LoadingSkeleton(
                        modifier = Modifier.align(Alignment.BottomCenter)
                    )
                    is Resource.Error -> Toast.makeText(
                        context,
                        "Failed to get data",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                when (currentLocation?.value) {
                    is Resource.Loading -> LoadingSkeleton(
                        modifier = Modifier.align(Alignment.BottomCenter)
                    )
                    else -> Unit
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
                FloatingActionButton(
                    onClick = { openDialog = !openDialog },
                    modifier = Modifier
                        .padding(20.dp)
                        .align(Alignment.TopEnd),
                    backgroundColor = Color.White
                ) {
                    Text(
                        text = "?",
                        style = MaterialTheme.typography.h5
                    )
                }
            }

            if (openDialog) {
                AlertDialog(
                    onDismissRequest = { openDialog = !openDialog },
                    title = {
                        LottieAnimation(
                            composition = lottieComposition,
                            progress = progress,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp)
                        )
                    },
                    text = {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.Start
                        ) {
                            Text(
                                text = "Bank Sampah",
                                style = MaterialTheme.typography.h6,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            Text(text = "Bank sampah adalah suatu tempat yang digunakan untuk mengumpulkan sampah yang sudah dipilah-pilah. Hasil dari pengumpulan sampah yang sudah dipilah akan disetorkan ke tempat pembuatan kerajinan dari sampah atau ke tempat Pengepul sampah. Bank sampah dikelola menggunakan sistem seperti perbankkan yang dilakukan oleh petugas sukarelawan.")
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = { openDialog = !openDialog }) {
                            Text(text = "OK")
                        }
                    },
                    shape = RoundedCornerShape(16.dp)
                )
            }
        }
    )
}

@Composable
fun LoadingSkeleton(modifier: Modifier = Modifier) {
    Row(modifier = modifier) {
        repeat(2) {
            Box(
                modifier = Modifier
                    .padding(start = 20.dp, bottom = 20.dp)
                    .width(300.dp)
                    .height(150.dp)
                    .placeholder(
                        visible = true,
                        shape = RoundedCornerShape(8.dp),
                        highlight = PlaceholderHighlight.fade(),
                        color = Color.LightGray
                    )
            )
        }
    }
}

@Composable
fun MapCard(
    title: String = "",
    description: String = "",
    rating: Float = 0.0f,
    navigateToMaps: () -> Unit = {},
) {
    Card(
        modifier = Modifier
            .padding(20.dp)
            .fillMaxWidth()
            .height(175.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = 20.dp
    ) {
        ConstraintLayout(modifier = Modifier.fillMaxSize()) {
            val (titleRef, descriptionRef, iconRef, ratingRef, mapButtonRef) = createRefs()

            Text(
                text = title,
                style = MaterialTheme.typography.h5,
                fontWeight = FontWeight.W700,
                textAlign = TextAlign.Start,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .fillMaxWidth()
                    .constrainAs(titleRef) {
                        start.linkTo(iconRef.end, margin = 8.dp)
                        top.linkTo(parent.top, margin = 20.dp)
                        end.linkTo(parent.end, margin = 20.dp)
                        width = Dimension.preferredWrapContent
                    }
            )
            Text(
                text = description,
                style = MaterialTheme.typography.body2,
                textAlign = TextAlign.Start,
                modifier = Modifier
                    .fillMaxWidth()
                    .constrainAs(descriptionRef) {
                        start.linkTo(iconRef.end, margin = 8.dp)
                        top.linkTo(titleRef.bottom, margin = 4.dp)
                        end.linkTo(parent.end, margin = 20.dp)
                        width = Dimension.preferredWrapContent
                    }
            )
            Icon(
                imageVector = Icons.Default.Place,
                contentDescription = null,
                tint = Color(0xFF047C00),
                modifier = Modifier
                    .size(42.dp)
                    .constrainAs(iconRef) {
                        start.linkTo(parent.start, margin = 10.dp)
                        top.linkTo(parent.top, margin = 20.dp)
                    }
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.constrainAs(ratingRef) {
                    start.linkTo(iconRef.end, margin = 6.dp)
                    top.linkTo(descriptionRef.bottom, margin = 4.dp)
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = Color(0xFFFFAF23),
                    modifier = Modifier.padding(end = 4.dp)
                )
                Text(
                    text = rating.toString(),
                    style = MaterialTheme.typography.body2,
                    color = Color.Gray
                )
            }
            OutlinedButton(
                onClick = navigateToMaps,
                border = BorderStroke(1.dp, Color(0xFF047C00)),
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    backgroundColor = Color.White,
                    contentColor = Color(0xFF047C00)
                ),
                modifier = Modifier.constrainAs(mapButtonRef) {
                    end.linkTo(parent.end, margin = 20.dp)
                    bottom.linkTo(parent.bottom, margin = 10.dp)
                }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_baseline_map_24),
                    contentDescription = null,
                    tint = Color(0xFF047C00),
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(text = "Open In Maps")
            }
        }
    }
}

@Preview
@Composable
fun PreviewMapCard() {
    MapCard(
        title = "Dliko Indah",
        description = "Jl. Dliko Indah IV no. 10, Salatiga, Jawa Tengah"
    )
}

@ExperimentalPagerApi
@Preview
@Composable
fun PreviewWasteBank() {
    WasteBankScreen()
}