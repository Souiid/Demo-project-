package com.souiid.demoproject.firebase.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String,
    val firstName: String,
    val lastName: String,
    val description: String,
    val photoUrl: String? = null
)