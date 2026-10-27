package com.souiid.demoproject.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.souiid.demoproject.ui.components.others.AppBottomBar
import com.souiid.demoproject.ui.components.others.AppIcon
import com.souiid.demoproject.ui.components.others.BottomBarItem
import com.souiid.demoproject.ui.components.others.TTopBar
import com.souiid.demoproject.ui.screens.app.datastore.DataStoreScreen
import com.souiid.demoproject.ui.screens.app.firebase.notification.NotificationBadgeViewModel
import com.souiid.demoproject.ui.screens.app.ktor.KtorScreen
import org.koin.compose.koinInject

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    val notificationBadgeViewModel: NotificationBadgeViewModel = koinInject()
    val unreadCount by notificationBadgeViewModel.unreadCount.collectAsStateWithLifecycle()
    val notificationNavigationData by NotificationNavigation.data.collectAsState()

    LaunchedEffect(notificationNavigationData) {
        val data = notificationNavigationData ?: return@LaunchedEffect

        when (data.type) {
            "NEW_FOLLOWER" -> {
                data.userId?.let {
                    navController.navigate(Route.FirebaseProfile(it))
                }
            }

            "NEW_LIKE", "NEW_POST" -> {
                data.postId?.let {
                    navController.navigate(Route.FirebaseFeed(it))
                }
            }
        }

        NotificationNavigation.clear()
    }

    val backStackEntry = navController.currentBackStackEntryAsState().value
    val destination = backStackEntry?.destination

    val selectedItem = when {
        destination?.hierarchy?.any { it.hasRoute<Route.Firebase>() } == true -> BottomBarItem.FIREBASE
        destination?.hasRoute<Route.Ktor>() == true -> BottomBarItem.KTOR
        destination?.hasRoute<Route.DataStore>() == true -> BottomBarItem.DATASTORE
        else -> BottomBarItem.FIREBASE
    }

    val isNotPrimaryScreen = destination?.hasRoute<Route.FirebaseFeed>() == false

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
                } else null,
                actions = {
                    if (destination?.hasRoute<Route.FirebaseFeed>() == true) {
                        IconButton(onClick = { navController.navigate(Route.Friends) }) {
                            Icon(
                                imageVector = AppIcon.Search,
                                contentDescription = "Search"
                            )
                        }

                        IconButton(onClick = { navController.navigate(Route.FirebaseProfile(null)) }) {
                            Icon(
                                imageVector = AppIcon.Person,
                                contentDescription = "Profile"
                            )
                        }

                        IconButton(
                            onClick = {
                                notificationBadgeViewModel.markAsRead()
                                navController.navigate(Route.NotificationCenter)
                            }
                        ) {
                            BadgedBox(
                                badge = {
                                    if (unreadCount > 0) {
                                        Badge {
                                            Text(if (unreadCount > 99) "99+" else unreadCount.toString())
                                        }
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = AppIcon.Notifications,
                                    contentDescription = "Notifications"
                                )
                            }
                        }
                    }
                }
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