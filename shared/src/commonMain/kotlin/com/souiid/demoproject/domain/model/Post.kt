package com.souiid.demoproject.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Post(
    val id: String = "",
    val authorId: String = "",
    val content: String = "",
    val createdAt: Long = 0L,
    val likesCount: Int = 0
)