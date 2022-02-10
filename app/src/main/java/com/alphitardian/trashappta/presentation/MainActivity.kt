package com.alphitardian.trashappta.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Observer
import com.alphitardian.trashappta.presentation.navigation.AppNavigation
import com.alphitardian.trashappta.presentation.navigation.Destination
import com.alphitardian.trashappta.presentation.theme.TrashAppTATheme
import com.alphitardian.trashappta.utils.Resource
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import dagger.hilt.android.AndroidEntryPoint

@ExperimentalPagerApi
@ExperimentalAnimationApi
@ExperimentalFoundationApi
@ExperimentalPermissionsApi
@ExperimentalMaterialApi
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        installSplashScreen().apply {
            setKeepVisibleCondition {
                viewModel.isLoading.value
            }
        }

        viewModel.profile.observe(this) { resource ->
            when (resource) {
                is Resource.Success -> isUserLoggedIn(true)
                is Resource.Error -> isUserLoggedIn(false)
                is Resource.Loading -> {
                    setContent {
                        TrashAppTATheme {
                            Box(modifier = Modifier.fillMaxSize()) {
                                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                            }
                        }
                    }
                }
                else -> Unit
            }
        }
    }

    private fun isUserLoggedIn(isTrue: Boolean) {
        if (isTrue) {
            setContent {
                TrashAppTATheme {
                    val systemUiController = rememberSystemUiController()
                    val useDarkIcon = MaterialTheme.colors.isLight

                    SideEffect {
                        systemUiController.setSystemBarsColor(
                            color = Color.Transparent,
                            darkIcons = useDarkIcon
                        )
                    }

                    AppNavigation(Destination.HOME.name)
                }
            }
        } else {
            setContent {
                TrashAppTATheme {
                    val systemUiController = rememberSystemUiController()
                    val useDarkIcon = MaterialTheme.colors.isLight

                    SideEffect {
                        systemUiController.setSystemBarsColor(
                            color = Color.Transparent,
                            darkIcons = useDarkIcon
                        )
                    }

                    AppNavigation("AUTH_GRAPH")
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    TrashAppTATheme {
        Greeting("Android")
    }
}