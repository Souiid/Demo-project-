package com.souiid.demoproject.domain.model

data class AppNotification(
    val id: String,
    val type: String,
    val title: String,
    val body: String,
    val userId: String,
    val postId: String? = null,
    val createdAt: Long
)