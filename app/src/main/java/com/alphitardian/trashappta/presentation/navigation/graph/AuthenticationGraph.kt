package com.alphitardian.trashappta.presentation.navigation.graph

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.navigation
import com.alphitardian.trashappta.presentation.login.LoginScreen
import com.alphitardian.trashappta.presentation.login.LoginViewModel
import com.alphitardian.trashappta.presentation.navigation.Destination
import com.alphitardian.trashappta.presentation.register.RegisterScreen
import com.alphitardian.trashappta.presentation.register.RegisterViewModel
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.permissions.ExperimentalPermissionsApi

@ExperimentalPermissionsApi
@ExperimentalAnimationApi
fun NavGraphBuilder.authenticationGraph(navController: NavController) {
    navigation(startDestination = Destination.LOGIN.name, route = "AUTH_GRAPH") {
        composable(route = Destination.LOGIN.name) {
            val loginViewModel = hiltViewModel<LoginViewModel>()
            LoginScreen(
                viewModel = loginViewModel,
                navigateToHome = {
                    navController.navigate(Destination.HOME.name) {
                        popUpTo(Destination.LOGIN.name) {
                            inclusive = true
                        }
                    }
                },
                navigateToRegister = { navController.navigate(Destination.REGISTER.name) }
            )
        }
        composable(route = Destination.REGISTER.name) {
            val registerViewModel = hiltViewModel<RegisterViewModel>()
            RegisterScreen(registerViewModel)
        }
    }
}