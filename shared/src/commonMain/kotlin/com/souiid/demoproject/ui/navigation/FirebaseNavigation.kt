package com.souiid.demoproject.ui.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.souiid.demoproject.ui.screens.app.firebase.editprofile.EditProfileScreen
import com.souiid.demoproject.ui.screens.app.firebase.feed.FirebaseFeedScreen
import com.souiid.demoproject.ui.screens.app.firebase.profile.FirebaseProfileScreen
import com.souiid.demoproject.ui.screens.app.firebase.friends.FriendsScreen

fun NavGraphBuilder.firebaseNavigation(navController: NavController) {
    navigation<Route.Firebase>(startDestination = Route.FirebaseFeed) {
        composable<Route.FirebaseFeed> {
            FirebaseFeedScreen(
                goToProfile = {
                    navController.navigate(Route.FirebaseProfile)
                },
                goToFriends = {
                    navController.navigate(Route.Friends)
                }
            )
        }

        composable<Route.FirebaseProfile> {
            FirebaseProfileScreen(
                goToEditProfile = {
                    navController.navigate(Route.EditProfile)
                }
            )
        }

        composable<Route.EditProfile> {
            EditProfileScreen()
        }

        composable<Route.Friends> {
            FriendsScreen(
                onUserClick = {

                }
            )
        }
    }
}