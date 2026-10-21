package com.souiid.demoproject.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.souiid.demoproject.ui.screens.auth.SignInScreen
import com.souiid.demoproject.ui.screens.auth.signup.credentials.SignUpCredentialScreen
import com.souiid.demoproject.ui.screens.auth.signup.description.SignUpDescriptionScreen
import com.souiid.demoproject.ui.screens.auth.signup.name.SignUpNameScreen

@Composable
fun AuthNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Route.SignIn
    ) {
        composable<Route.SignIn> {
            SignInScreen(
                onSignUpClick = { navController.navigate(Route.SignUpName) }
            )
        }
        composable<Route.SignUpName> {
            SignUpNameScreen(
                onContinueClick = { navController.navigate(Route.SignUpDescription) }
            )
        }

        composable<Route.SignUpDescription> {
            SignUpDescriptionScreen(
                onContinueClick = {navController.navigate(Route.SignUpCredential)}
            )
        }

        composable<Route.SignUpCredential> {
            SignUpCredentialScreen()
        }
    }
}