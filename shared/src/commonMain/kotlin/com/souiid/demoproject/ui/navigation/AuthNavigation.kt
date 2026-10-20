package com.souiid.demoproject.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.souiid.demoproject.ui.screens.auth.SignInScreen
import com.souiid.demoproject.ui.screens.auth.SignUpScreen

@Composable
fun AuthNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Route.SignIn
    ) {
        composable<Route.SignIn> {
            SignInScreen(
                onSignUpClick = { navController.navigate(Route.SignUp) }
            )
        }
        composable<Route.SignUp> {
            SignUpScreen(
                onSignInClick = { navController.popBackStack() }
            )
        }
    }
}