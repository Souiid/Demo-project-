package com.souiid.demoproject.ui.navigation

import com.souiid.demoproject.ui.screens.app.firebase.profile.ProfileListType
import kotlinx.serialization.Serializable

sealed interface Route {

    @Serializable
    data object Firebase : Route

    @Serializable
    data object Ktor : Route

    @Serializable
    data object DataStore : Route

    @Serializable
    data object SignIn : Route

    @Serializable
    data object SignUpName : Route

    @Serializable
    data object SignUpDescription : Route

    @Serializable
    data object SignUpCredential : Route

    @Serializable
    data object FirebaseFeed : Route

    @Serializable
    data class FirebaseProfile(val userId: String? = null) : Route

    @Serializable
    data object EditProfile: Route

    @Serializable
    data object Friends: Route

    @Serializable
    data class FirebaseProfileList(
        val userId: String,
        val type: String,
    ) : Route
}