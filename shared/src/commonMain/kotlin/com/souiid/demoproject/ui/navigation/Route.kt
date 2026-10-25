package com.souiid.demoproject.ui.navigation

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
    data object FirebaseProfile : Route

    @Serializable
    data object EditProfile: Route

    @Serializable
    data object Friends: Route
}