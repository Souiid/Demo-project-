package com.souiid.demoproject.ui.screens.app.firebase.notification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.souiid.demoproject.domain.usecase.notification.MarkNotificationsAsReadUseCase
import com.souiid.demoproject.domain.usecase.notification.ObserveNotificationsUseCase
import com.souiid.demoproject.domain.usecase.user.ObserveCurrentUserUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import com.souiid.demoproject.platform.updateAppBadge


class NotificationBadgeViewModel(
    private val observeCurrentUserUseCase: ObserveCurrentUserUseCase,
    private val observeNotificationsUseCase: ObserveNotificationsUseCase,
    private val markNotificationsAsReadUseCase: MarkNotificationsAsReadUseCase
) : ViewModel() {

    private val _unreadCount = MutableStateFlow(0)
    val unreadCount = _unreadCount.asStateFlow()

    private var currentUserId: String? = null

    init {
        viewModelScope.launch {
            observeCurrentUserUseCase().collectLatest { user ->
                currentUserId = user?.id
                _unreadCount.value = 0
                updateAppBadge(0)

                if (user == null) return@collectLatest

                observeNotificationsUseCase(user.id).collect { notifications ->
                    val count = notifications.count { !it.isRead }
                    _unreadCount.value = count
                    updateAppBadge(count)
                }
            }
        }
    }

    fun markAsRead() {
        val userId = currentUserId ?: return

        viewModelScope.launch {
            try {
                markNotificationsAsReadUseCase(userId)
            } catch (error: Exception) {
                println("Failed to mark notifications as read: $error")
            }
        }
    }
}