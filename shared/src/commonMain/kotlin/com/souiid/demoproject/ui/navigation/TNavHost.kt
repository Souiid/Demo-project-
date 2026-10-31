package com.souiid.demoproject.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.NavHost
import kotlin.reflect.KClass

@Composable
fun TNavHost(
    navController: NavHostController,
    startDestination: KClass<out Route>,
    modifier: Modifier = Modifier,
    builder: NavGraphBuilder.() -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
        enterTransition = {
            if (initialState.destination.isPrimaryDestination() && targetState.destination.isPrimaryDestination()) {
                EnterTransition.None
            } else {
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left)
            }
        },
        exitTransition = {
            if (initialState.destination.isPrimaryDestination() && targetState.destination.isPrimaryDestination()) {
                ExitTransition.None
            } else {
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Left)
            }
        },
        popEnterTransition = {
            if (initialState.destination.isPrimaryDestination() && targetState.destination.isPrimaryDestination()) {
                EnterTransition.None
            } else {
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Right)
            }
        },
        popExitTransition = {
            if (initialState.destination.isPrimaryDestination() && targetState.destination.isPrimaryDestination()) {
                ExitTransition.None
            } else {
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right)
            }
        },
        builder = builder
    )
}

private fun androidx.navigation.NavDestination.isPrimaryDestination(): Boolean {
    return hasRoute<Route.FirebaseFeed>() ||
            hasRoute<Route.HomeMeal>() ||
            hasRoute<Route.Room>()
}
