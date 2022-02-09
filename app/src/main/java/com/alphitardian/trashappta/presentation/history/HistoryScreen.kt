package com.alphitardian.trashappta.presentation.history

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.alphitardian.trashappta.R
import com.alphitardian.trashappta.data.waste.remote.response.WasteHistoryResponse
import com.alphitardian.trashappta.presentation.home.HomeTopAppBar
import com.alphitardian.trashappta.utils.Resource
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.fade
import com.google.accompanist.placeholder.placeholder
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.skydoves.landscapist.glide.GlideImage

@ExperimentalFoundationApi
@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel? = null,
    navigateToDetail: (WasteHistoryResponse) -> Unit = {},
) {
    val scaffoldState = rememberScaffoldState()
    val waste = viewModel?.wasteHistory?.observeAsState()
    val isRefresh = viewModel?.isRefresh?.collectAsState()

    LaunchedEffect(key1 = Unit) { viewModel?.getWasteHistory() }

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = { HomeTopAppBar() },
        content = {
            isRefresh?.let {
                SwipeRefresh(
                    state = rememberSwipeRefreshState(isRefreshing = isRefresh.value),
                    onRefresh = { viewModel.getWasteHistory() }
                ) {
                    LazyVerticalGrid(
                        cells = GridCells.Adaptive(minSize = 150.dp),
                        contentPadding = PaddingValues(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        when (val value = waste?.value) {
                            is Resource.Success -> {
                                value.data.data?.let {
                                    items(it) {
                                        CardTiles(
                                            photoUrl = it.url,
                                            name = it.waste.name,
                                            description = it.waste.solution.description,
                                            onItemClick = { navigateToDetail(it) },
                                        )
                                    }
                                }
                            }
                            is Resource.Loading -> {
                                items(5) {
                                    Box(
                                        modifier = Modifier
                                            .padding(8.dp)
                                            .placeholder(
                                                visible = true,
                                                highlight = PlaceholderHighlight.fade(
                                                    highlightColor = Color.White
                                                ),
                                                color = Color.LightGray,
                                                shape = RoundedCornerShape(16.dp)
                                            )
                                    ) {
                                        CardTiles(
                                            photoUrl = "",
                                            name = "",
                                            description = "",
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun CardTiles(
    photoUrl: String,
    name: String,
    description: String,
    modifier: Modifier = Modifier,
    onItemClick: () -> Unit = {},
) {
    Card(
        modifier = modifier
            .padding(8.dp)
            .width(170.dp)
            .height(225.dp),
        shape = RoundedCornerShape(16.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(onClick = onItemClick)
        ) {
            GlideImage(
                imageModel = photoUrl,
                previewPlaceholder = R.drawable.placeholder_image,
                contentScale = ContentScale.Crop,
                modifier = Modifier.align(Alignment.Center)
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.LightGray
                            ), startY = 200f
                        )
                    )
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 8.dp),
                contentAlignment = Alignment.BottomStart
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = name,
                        style = MaterialTheme.typography.h6,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(start = 12.dp, end = 12.dp, top = 8.dp)
                    )
                    Text(
                        text = description,
                        style = MaterialTheme.typography.body2,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )
                }
            }
        }
    }
}

@ExperimentalFoundationApi
@Preview
@Composable
fun PreviewScreen() {
    HistoryScreen()
}