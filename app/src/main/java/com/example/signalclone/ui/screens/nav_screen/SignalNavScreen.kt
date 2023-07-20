package com.example.signalclone.ui.screens.nav_screen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.signalclone.ui.screens.auth_screens.AuthViewModel
import com.example.signalclone.ui.screens.auth_screens.login_screen.LoginScreen
import com.example.signalclone.ui.screens.auth_screens.signup_screen.SignUpScreen
import com.example.signalclone.ui.screens.main_screen.MainScreen
import com.example.signalclone.ui.screens.main_screen.posts_screen.PostsScreen
import com.example.signalclone.ui.screens.main_screen.profile_screen.ProfileScreen
import com.example.signalclone.ui.utils.AnimatedComposable
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

@Composable
fun SignalNavScreen(
    navController: NavHostController,
    authViewModel: AuthViewModel
) {
    val startingDestination = Firebase.auth.currentUser.let {
        if (it == null) Screens.SignInScreen.route
        else Screens.MainScreen.route
    }
    NavHost(navController = navController, startDestination = startingDestination) {
        composable(route = Screens.SignInScreen.route) {
            AnimatedComposable {
                LoginScreen(
                    authViewModel = authViewModel,
                    navController = navController
                )
            }
        }
        composable(route = Screens.SignUpScreen.route) {
            AnimatedComposable {

                SignUpScreen(
                    navController = navController,
                    viewModel = authViewModel,
                    onEvent = authViewModel::onEvent,
                )
            }
        }

        composable(route = Screens.MainScreen.route) {
            AnimatedComposable {
                MainScreen(
                    authViewModel = authViewModel,
                    navController = navController
                )
            }
        }
    }
}


@Composable
fun MainNavScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    authViewModel: AuthViewModel
) {
    NavHost(
        navController = navController,
        startDestination = BottomNavScreens.PostsScreen.route,
        modifier = modifier.then(Modifier.fillMaxSize())
    ) {
        composable(route = BottomNavScreens.PostsScreen.route) {
            AnimatedComposable {
                PostsScreen(navController = navController)
            }
        }
        composable(route = BottomNavScreens.ProfileScreen.route) {
            AnimatedComposable {
                ProfileScreen(navController = navController,authViewModel = authViewModel)
            }
        }

    }
}