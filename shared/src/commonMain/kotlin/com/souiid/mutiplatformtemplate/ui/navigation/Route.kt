package com.souiid.mutiplatformtemplate.ui.navigation

import kotlinx.serialization.Serializable

sealed interface Route {

    @Serializable
    data object Home : Route

    @Serializable
    data object Search : Route

    @Serializable
    data object Profile : Route
}