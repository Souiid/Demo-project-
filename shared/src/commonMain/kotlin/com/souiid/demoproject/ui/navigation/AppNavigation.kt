package com.souiid.demoproject.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.souiid.demoproject.ui.components.others.AppBottomBar
import com.souiid.demoproject.ui.components.others.BottomBarItem
import com.souiid.demoproject.ui.components.others.TTopBar
import com.souiid.demoproject.ui.screens.app.datastore.DataStoreScreen
import com.souiid.demoproject.ui.screens.app.ktor.KtorScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val backStackEntry = navController.currentBackStackEntryAsState().value
    val destination = backStackEntry?.destination

    val selectedItem = when {
        destination?.hierarchy?.any { it.hasRoute<Route.Firebase>() } == true -> BottomBarItem.FIREBASE
        destination?.hasRoute<Route.Ktor>() == true -> BottomBarItem.KTOR
        destination?.hasRoute<Route.DataStore>() == true -> BottomBarItem.DATASTORE
        else -> BottomBarItem.FIREBASE
    }

    val isNotPrimaryScreen = destination?.hasRoute<Route.FirebaseProfile>() == true

    Scaffold(
        topBar = {
            TTopBar(
                title = when (selectedItem) {
                    BottomBarItem.FIREBASE -> "Firebase"
                    BottomBarItem.KTOR -> "Ktor"
                    BottomBarItem.DATASTORE -> "DataStore"
                },
                onBack = if (isNotPrimaryScreen) {
                    { navController.popBackStack() }
                } else null
            )
        },
        bottomBar = {
            AppBottomBar(
                selectedItem = selectedItem,
                onItemSelected = { item ->
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    ) { paddingValues ->
        TNavHost(
            navController = navController,
            startDestination = Route.Firebase::class,
            modifier = Modifier.padding(paddingValues)
        ) {
            firebaseNavigation(navController)

            composable<Route.Ktor> {
                KtorScreen()
            }

            composable<Route.DataStore> {
                DataStoreScreen()
            }
        }
    }
}