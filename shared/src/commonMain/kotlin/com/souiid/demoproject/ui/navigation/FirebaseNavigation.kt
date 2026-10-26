package com.souiid.demoproject.ui.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.souiid.demoproject.ui.screens.app.firebase.editprofile.EditProfileScreen
import com.souiid.demoproject.ui.screens.app.firebase.feed.FirebaseFeedScreen
import com.souiid.demoproject.ui.screens.app.firebase.profile.FirebaseProfileScreen
import com.souiid.demoproject.ui.screens.app.firebase.friends.FriendsScreen
import com.souiid.demoproject.ui.screens.app.firebase.profile.FirebaseProfileListScreen

fun NavGraphBuilder.firebaseNavigation(navController: NavController) {
    navigation<Route.Firebase>(startDestination = Route.FirebaseFeed) {
        composable<Route.FirebaseFeed> {
            FirebaseFeedScreen(
                onUserClick = { userId ->
                    navController.navigate(Route.FirebaseProfile(userId))
                }
            )
        }

        composable<Route.FirebaseProfile> { backStackEntry ->
            val route = backStackEntry.toRoute<Route.FirebaseProfile>()

            FirebaseProfileScreen(
                userId = route.userId,
                goToEditProfile = { navController.navigate(Route.EditProfile) },
                onStatClick = { userId, type ->
                    navController.navigate(Route.FirebaseProfileList(userId, type))
                }
            )
        }

        composable<Route.EditProfile> {
            EditProfileScreen()
        }

        composable<Route.Friends> {
            FriendsScreen(
                onUserClick = { userId ->
                    navController.navigate(Route.FirebaseProfile(userId))
                }
            )
        }

        composable<Route.FirebaseProfileList> { backStackEntry ->
            val route = backStackEntry.toRoute<Route.FirebaseProfileList>()

            FirebaseProfileListScreen(
                userId = route.userId,
                type = route.type,
                onUserClick = { userId ->
                    navController.navigate(Route.FirebaseProfile(userId))
                }
            )
        }
    }
}