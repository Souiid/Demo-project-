package com.souiid.demoproject.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.souiid.demoproject.ui.components.others.TTopBar
import com.souiid.demoproject.ui.screens.auth.signin.SignInScreen
import com.souiid.demoproject.ui.screens.auth.signup.credential.SignUpCredentialScreen
import com.souiid.demoproject.ui.screens.auth.signup.description.SignUpDescriptionScreen
import com.souiid.demoproject.ui.screens.auth.signup.name.SignUpNameScreen

@Composable
fun AuthNavigation() {

    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val isSignIn = backStackEntry?.destination?.hasRoute<Route.SignIn>() == true

    Scaffold(
        topBar = {
            if (!isSignIn && backStackEntry != null) {
                TTopBar(
                    title = "",
                    onBack = { navController.popBackStack() }
                )
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Route.SignIn,
            modifier = Modifier.padding(paddingValues)
        )  {
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
                    onContinueClick = { navController.navigate(Route.SignUpCredential) }
                )
            }
            composable<Route.SignUpCredential> {
                SignUpCredentialScreen()
            }
        }
    }
}