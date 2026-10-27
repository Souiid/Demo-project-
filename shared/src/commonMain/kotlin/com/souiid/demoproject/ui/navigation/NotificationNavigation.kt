package com.souiid.demoproject.ui.navigation

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

data class NotificationNavigationData(
    val type: String,
    val userId: String? = null,
    val postId: String? = null
)

object NotificationNavigation {
    private val _data = MutableStateFlow<NotificationNavigationData?>(null)
    val data = _data.asStateFlow()

    fun navigate(type: String, userId: String?, postId: String?) {
        _data.value = NotificationNavigationData(type, userId, postId)
    }

    fun clear() {
        _data.value = null
    }
}