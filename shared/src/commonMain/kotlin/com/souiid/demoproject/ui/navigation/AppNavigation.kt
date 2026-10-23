package com.souiid.demoproject.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.souiid.demoproject.ui.components.others.AppBottomBar
import com.souiid.demoproject.ui.components.others.BottomBarItem
import com.souiid.demoproject.ui.components.others.TTopBar
import com.souiid.demoproject.ui.screens.app.firebase.FirebaseFeedScreen
import com.souiid.demoproject.ui.screens.app.ktor.KtorScreen
import com.souiid.demoproject.ui.screens.app.datastore.DataStoreScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val backStackEntry = navController.currentBackStackEntryAsState().value

    val selectedItem = when {
        backStackEntry?.destination?.hasRoute<Route.FirebaseFeed>() == true -> BottomBarItem.FIREBASE
        backStackEntry?.destination?.hasRoute<Route.Ktor>() == true -> BottomBarItem.KTOR
        backStackEntry?.destination?.hasRoute<Route.DataStore>() == true -> BottomBarItem.DATASTORE
        else -> BottomBarItem.FIREBASE
    }

    Scaffold(
        topBar = {
            TTopBar(
                title = when (selectedItem) {
                    BottomBarItem.FIREBASE -> "Firebase"
                    BottomBarItem.KTOR -> "Ktor"
                    BottomBarItem.DATASTORE -> "DataStore"
                }
            )
        },
        bottomBar = {
            AppBottomBar(
                selectedItem = selectedItem,
                onItemSelected = { item ->
                    navController.navigate(item.route) {
                        launchSingleTop = true
                    }
                }
            )
        }
    ) { paddingValues ->
        TNavHost(
            navController = navController,
            startDestination = Route.FirebaseFeed::class,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable<Route.FirebaseFeed> {
                FirebaseFeedScreen()
            }

            composable<Route.Ktor> {
                DataStoreScreen()
            }

            composable<Route.DataStore> {
                KtorScreen()
            }
        }
    }
}