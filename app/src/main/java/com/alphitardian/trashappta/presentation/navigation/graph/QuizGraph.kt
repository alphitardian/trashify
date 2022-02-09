package com.alphitardian.trashappta.presentation.navigation.graph

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import com.alphitardian.trashappta.presentation.navigation.Destination
import com.alphitardian.trashappta.presentation.quiz.QuizResultScreen
import com.alphitardian.trashappta.presentation.quiz.QuizScreen
import com.alphitardian.trashappta.presentation.quiz.QuizViewModel
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.navigation

@ExperimentalAnimationApi
fun NavGraphBuilder.quizGraph(navController: NavController, quizViewModel: QuizViewModel) {
    navigation(startDestination = Destination.QUIZ.name, route = "QUIZ_GRAPH") {
        composable(route = Destination.QUIZ.name) {
            QuizScreen(
                viewModel = quizViewModel,
                navigateBack = {
                    quizViewModel.close()
                    navController.navigateUp()
                },
                navigateToResult = { navController.navigate(Destination.QUIZ_RESULT.name) }
            )
        }
        composable(route = Destination.QUIZ_RESULT.name) {
            QuizResultScreen(
                viewModel = quizViewModel,
                navigateBack = {
                    quizViewModel.close()
                    navController.navigate(Destination.HOME.name) {
                        popUpTo(Destination.HOME.name) {
                            inclusive = true
                        }
                    }
                }
            )
        }
    }
}