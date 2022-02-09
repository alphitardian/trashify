package com.alphitardian.trashappta.presentation.navigation.graph

import android.net.Uri
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.alphitardian.trashappta.data.waste.remote.response.WasteHistoryResponse
import com.alphitardian.trashappta.domain.model.WasteParamType
import com.alphitardian.trashappta.presentation.history.HistoryDetailScreen
import com.alphitardian.trashappta.presentation.history.HistoryScreen
import com.alphitardian.trashappta.presentation.history.HistoryViewModel
import com.alphitardian.trashappta.presentation.navigation.Destination
import com.google.accompanist.navigation.animation.composable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@ExperimentalMaterialApi
@ExperimentalFoundationApi
@ExperimentalAnimationApi
fun NavGraphBuilder.historyGraph(navController: NavController) {
    navigation(startDestination = Destination.HISTORY.name, route = "HISTORY_GRAPH") {
        composable(route = Destination.HISTORY.name) {
            val historyViewModel = hiltViewModel<HistoryViewModel>()
            HistoryScreen(
                viewModel = historyViewModel,
                navigateToDetail = {
                    val json = Uri.encode(Json.encodeToString(it))
                    navController.navigate("${Destination.MAPS.name}/$json")
                }
            )
        }
        composable(
            route = "${Destination.MAPS.name}/{wasteData}",
            arguments = listOf(navArgument("wasteData") { type = WasteParamType() }),
            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentScope.SlideDirection.Left,
                    animationSpec = tween(300)
                )
            },
            popEnterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentScope.SlideDirection.Left,
                    animationSpec = tween(300)
                )
            },
            popExitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentScope.SlideDirection.Right,
                    animationSpec = tween(300)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentScope.SlideDirection.Right,
                    animationSpec = tween(300)
                )
            }
        ) {
            val historyViewModel = hiltViewModel<HistoryViewModel>()
            val waste = it.arguments?.getParcelable<WasteHistoryResponse>("wasteData")
            HistoryDetailScreen(
                viewModel = historyViewModel,
                waste = waste,
                navigateBack = { navController.navigateUp() }
            )
        }
    }
}