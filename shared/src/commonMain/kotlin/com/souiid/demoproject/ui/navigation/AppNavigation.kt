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
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.souiid.demoproject.ui.components.others.AppBottomBar
import com.souiid.demoproject.ui.components.others.AppIcon
import com.souiid.demoproject.ui.components.others.BottomBarItem
import com.souiid.demoproject.ui.components.others.TTopBar
import com.souiid.demoproject.ui.screens.app.firebase.notification.NotificationBadgeViewModel
import com.souiid.demoproject.ui.screens.app.room.FavoriteMealsScreen
import demoproject.shared.generated.resources.Res
import demoproject.shared.generated.resources.editProfile
import demoproject.shared.generated.resources.favorites
import demoproject.shared.generated.resources.feed
import demoproject.shared.generated.resources.firebase
import demoproject.shared.generated.resources.followers
import demoproject.shared.generated.resources.followings
import demoproject.shared.generated.resources.ktor
import demoproject.shared.generated.resources.mealDetails
import demoproject.shared.generated.resources.meals
import demoproject.shared.generated.resources.notifications
import demoproject.shared.generated.resources.posts
import demoproject.shared.generated.resources.profile
import demoproject.shared.generated.resources.room
import demoproject.shared.generated.resources.search
import demoproject.shared.generated.resources.signIn
import demoproject.shared.generated.resources.signUp
import org.jetbrains.compose.resources.stringResource
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
        destination?.hierarchy?.any { it.hasRoute<Route.Ktor>() } == true -> BottomBarItem.KTOR
        destination?.hasRoute<Route.Room>() == true -> BottomBarItem.ROOM
        else -> BottomBarItem.FIREBASE
    }

    val isNotPrimaryScreen = when {
        destination?.hasRoute<Route.FirebaseFeed>() == true -> false
        destination?.hasRoute<Route.HomeMeal>() == true -> false
        destination?.hasRoute<Route.Room>() == true -> false
        else -> true
    }

    Scaffold(
        topBar = {
            TTopBar(
                title = getScreenTitle(backStackEntry, selectedItem),
                onBack = if (isNotPrimaryScreen) {
                    { navController.popBackStack() }
                } else null,
                actions = {
                    if (destination?.hasRoute<Route.FirebaseFeed>() == true) {
                        IconButton(onClick = { navController.navigate(Route.Friends) }) {
                            Icon(
                                imageVector = AppIcon.Search,
                                contentDescription = stringResource(Res.string.search)
                            )
                        }

                        IconButton(onClick = { navController.navigate(Route.FirebaseProfile(null)) }) {
                            Icon(
                                imageVector = AppIcon.Person,
                                contentDescription = stringResource(Res.string.profile)
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
                                    contentDescription = stringResource(Res.string.notifications)
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
            ktorNavigation(navController)

            composable<Route.Room> {
                FavoriteMealsScreen(
                    onMealClick = { mealId ->
                        navController.navigate(Route.MealDetail(mealId))
                    }
                )
            }
        }
    }
}

@Composable
private fun getScreenTitle(
    backStackEntry: NavBackStackEntry?,
    selectedItem: BottomBarItem
): String {
    val destination = backStackEntry?.destination

    return when {
        destination?.hasRoute<Route.FirebaseFeed>() == true -> stringResource(Res.string.feed)
        destination?.hasRoute<Route.FirebaseProfile>() == true -> stringResource(Res.string.profile)
        destination?.hasRoute<Route.EditProfile>() == true -> stringResource(Res.string.editProfile)
        destination?.hasRoute<Route.Friends>() == true -> stringResource(Res.string.search)

        destination?.hasRoute<Route.FirebaseProfileList>() == true -> {
            when (backStackEntry.toRoute<Route.FirebaseProfileList>().type.lowercase()) {
                "followers" -> stringResource(Res.string.followers)
                "following" -> stringResource(Res.string.followings)
                else -> stringResource(Res.string.posts)
            }
        }

        destination?.hasRoute<Route.NotificationCenter>() == true -> stringResource(Res.string.notifications)
        destination?.hasRoute<Route.SignIn>() == true -> stringResource(Res.string.signIn)
        destination?.hasRoute<Route.SignUpName>() == true -> stringResource(Res.string.signUp)
        destination?.hasRoute<Route.SignUpDescription>() == true -> stringResource(Res.string.signUp)
        destination?.hasRoute<Route.SignUpCredential>() == true -> stringResource(Res.string.signUp)
        destination?.hasRoute<Route.HomeMeal>() == true -> stringResource(Res.string.meals)
        destination?.hasRoute<Route.MealDetail>() == true -> stringResource(Res.string.mealDetails)
        destination?.hasRoute<Route.Room>() == true -> stringResource(Res.string.favorites)

        else -> when (selectedItem) {
            BottomBarItem.FIREBASE -> stringResource(Res.string.firebase)
            BottomBarItem.KTOR -> stringResource(Res.string.ktor)
            BottomBarItem.ROOM -> stringResource(Res.string.room)
        }
    }
}
