package com.souiid.mutiplatformtemplate.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.souiid.mutiplatformtemplate.ui.components.others.AppBottomBar
import com.souiid.mutiplatformtemplate.ui.components.others.BottomBarItem
import com.souiid.mutiplatformtemplate.ui.screens.HomeScreen
import com.souiid.mutiplatformtemplate.ui.screens.ProfileScreen
import com.souiid.mutiplatformtemplate.ui.screens.SearchScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val backStackEntry = navController.currentBackStackEntryAsState().value

    val selectedItem = when {
        backStackEntry?.destination?.hasRoute<Route.Home>() == true -> BottomBarItem.HOME
        backStackEntry?.destination?.hasRoute<Route.Search>() == true -> BottomBarItem.SEARCH
        backStackEntry?.destination?.hasRoute<Route.Profile>() == true -> BottomBarItem.PROFILE
        else -> BottomBarItem.HOME
    }

    Scaffold(
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
        NavHost(
            navController = navController,
            startDestination = Route.Home,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable<Route.Home> {
                HomeScreen()
            }

            composable<Route.Search> {
                SearchScreen()
            }

            composable<Route.Profile> {
                ProfileScreen()
            }
        }
    }
}