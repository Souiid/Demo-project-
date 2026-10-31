package com.souiid.demoproject.ui.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.souiid.demoproject.ui.components.others.SignUpProgressBar
import com.souiid.demoproject.ui.components.others.TTopBar
import com.souiid.demoproject.ui.screens.auth.signin.SignInScreen
import com.souiid.demoproject.ui.screens.auth.signup.credential.SignUpCredentialScreen
import com.souiid.demoproject.ui.screens.auth.signup.description.SignUpDescriptionScreen
import com.souiid.demoproject.ui.screens.auth.signup.name.SignUpNameScreen
import com.souiid.demoproject.ui.theme.Dimens
import demoproject.shared.generated.resources.Res
import demoproject.shared.generated.resources.signUp
import org.jetbrains.compose.resources.stringResource

@Composable
fun AuthNavigation() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val destination = backStackEntry?.destination
    val isSignIn = destination?.hasRoute<Route.SignIn>() == true

    val signUpStep = when {
        destination?.hasRoute<Route.SignUpName>() == true -> 1
        destination?.hasRoute<Route.SignUpDescription>() == true -> 2
        destination?.hasRoute<Route.SignUpCredential>() == true -> 3
        else -> null
    }

    Scaffold(
        topBar = {
            if (!isSignIn && backStackEntry != null) {
                Column(verticalArrangement = Arrangement.spacedBy(Dimens.mediumSpace)) {
                    TTopBar(
                        title = stringResource(Res.string.signUp),
                        onBack = { navController.popBackStack() }
                    )
                    if (signUpStep != null) {
                        SignUpProgressBar(step = signUpStep)
                    }
                }
            }
        }
    ) { paddingValues ->
        TNavHost(
            navController = navController,
            startDestination = Route.SignIn::class,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable<Route.SignIn> {
                SignInScreen(onSignUpClick = { navController.navigate(Route.SignUpName) })
            }
            composable<Route.SignUpName> {
                SignUpNameScreen(onContinueClick = { navController.navigate(Route.SignUpDescription) })
            }
            composable<Route.SignUpDescription> {
                SignUpDescriptionScreen(onContinueClick = { navController.navigate(Route.SignUpCredential) })
            }
            composable<Route.SignUpCredential> {
                SignUpCredentialScreen()
            }
        }
    }
}