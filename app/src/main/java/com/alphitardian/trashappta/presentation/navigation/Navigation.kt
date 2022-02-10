package com.alphitardian.trashappta.presentation.navigation

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.alphitardian.trashappta.presentation.bank.WasteBankScreen
import com.alphitardian.trashappta.presentation.bank.WasteBankViewModel
import com.alphitardian.trashappta.presentation.detection.DetectionScreen
import com.alphitardian.trashappta.presentation.detection.DetectionViewModel
import com.alphitardian.trashappta.presentation.home.HomeScreen
import com.alphitardian.trashappta.presentation.home.HomeViewModel
import com.alphitardian.trashappta.presentation.navigation.graph.authenticationGraph
import com.alphitardian.trashappta.presentation.navigation.graph.historyGraph
import com.alphitardian.trashappta.presentation.navigation.graph.quizGraph
import com.alphitardian.trashappta.presentation.profile.ProfileScreen
import com.alphitardian.trashappta.presentation.profile.ProfileViewModel
import com.alphitardian.trashappta.presentation.profile.UpdateProfileScreen
import com.alphitardian.trashappta.presentation.quiz.QuizViewModel
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.permissions.ExperimentalPermissionsApi

@ExperimentalPagerApi
@ExperimentalAnimationApi
@ExperimentalFoundationApi
@ExperimentalPermissionsApi
@ExperimentalMaterialApi
@Composable
fun AppNavigation(startDestination: String) {
    val navController = rememberAnimatedNavController()
    val quizViewModel = hiltViewModel<QuizViewModel>()

    AnimatedNavHost(navController = navController, startDestination = startDestination) {
        composable(
            route = Destination.HOME.name,
            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentScope.SlideDirection.Up,
                    animationSpec = tween(300)
                )
            },
            popEnterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentScope.SlideDirection.Up,
                    animationSpec = tween(300)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentScope.SlideDirection.Down,
                    animationSpec = tween(300)
                )
            },
            popExitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentScope.SlideDirection.Down,
                    animationSpec = tween(300)
                )
            }
        ) {
            val homeViewModel = hiltViewModel<HomeViewModel>()
            HomeScreen(
                viewModel = homeViewModel,
                navigateToDetection = { navController.navigate(Destination.DETECTION.name) },
                navigateToQuiz = { navController.navigate("QUIZ_GRAPH") },
                navigateToProfile = { navController.navigate(Destination.PROFILE.name) },
                navigateToHistory = { navController.navigate("HISTORY_GRAPH") },
                navigateToBank = { navController.navigate(Destination.BANK.name) }
            )
        }
        composable(route = Destination.DETECTION.name) {
            val detectionViewModel = hiltViewModel<DetectionViewModel>()
            DetectionScreen(
                viewModel = detectionViewModel,
                navigateBack = { navController.navigateUp() }
            )
        }
        composable(route = Destination.PROFILE.name) {
            val profileViewModel = hiltViewModel<ProfileViewModel>()
            ProfileScreen(
                viewModel = profileViewModel,
                navigateBack = { navController.navigateUp() },
                navigateToUpdateProfile = { navController.navigate(Destination.UPDATE_PROFILE.name) }
            )
        }
        composable(route = Destination.UPDATE_PROFILE.name) {
            val profileViewModel = hiltViewModel<ProfileViewModel>()
            UpdateProfileScreen(
                navigateBack = { navController.navigateUp() }
            )
        }
        composable(route = Destination.BANK.name) {
            val wasteBankViewModel = hiltViewModel<WasteBankViewModel>()
            val context = LocalContext.current as Activity
            WasteBankScreen(
                viewModel = wasteBankViewModel,
                navigateToMaps = { latitude, longitude, query ->
                    val mapIntentUri = Uri.parse("geo:${latitude},${longitude}?q=$query")

                    println(mapIntentUri)
                    val mapIntent = Intent(Intent.ACTION_VIEW, mapIntentUri)
                    mapIntent.setPackage("com.google.android.apps.maps")
                    context.startActivity(mapIntent)
                },
                navigateBack = { navController.navigateUp() }
            )
        }
        quizGraph(navController = navController, quizViewModel = quizViewModel)
        authenticationGraph(navController = navController)
        historyGraph(navController = navController)
    }
}
