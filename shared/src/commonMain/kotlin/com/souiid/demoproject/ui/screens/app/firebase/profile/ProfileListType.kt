package com.souiid.demoproject.ui.screens.app.firebase.profile

import kotlinx.serialization.Serializable

@Serializable
enum class ProfileListType {
    POSTS,
    FOLLOWERS,
    FOLLOWING
}