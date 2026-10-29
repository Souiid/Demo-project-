package com.souiid.demoproject.ui.navigation

import kotlinx.serialization.Serializable

sealed interface Route {

    //FIREBASE

    @Serializable
    data object Firebase : Route

    @Serializable
    data object SignIn : Route

    @Serializable
    data object SignUpName : Route

    @Serializable
    data object SignUpDescription : Route

    @Serializable
    data object SignUpCredential : Route

    @Serializable
    data class FirebaseFeed(val postId: String? = null) : Route
    
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

    @Serializable
    data object NotificationCenter : Route

//__________________________________________________________

    //KTOR
    @Serializable
    data object Ktor : Route

    @Serializable
    data object HomeMeal : Route

    @Serializable
    data class MealDetail(val mealId: String)

//__________________________________________________________

    //DATASTORE
    @Serializable
    data object DataStore : Route
}