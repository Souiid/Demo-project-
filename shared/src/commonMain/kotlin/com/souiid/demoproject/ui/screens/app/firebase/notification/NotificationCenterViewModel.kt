package com.souiid.demoproject.ui.screens.app.firebase.notification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.souiid.demoproject.domain.model.AppNotification
import com.souiid.demoproject.domain.usecase.notification.ObserveNotificationsUseCase
import com.souiid.demoproject.domain.usecase.user.ObserveCurrentUserUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class NotificationCenterViewModel(
    private val observeCurrentUserUseCase: ObserveCurrentUserUseCase,
    private val observeNotificationsUseCase: ObserveNotificationsUseCase
) : ViewModel() {

    private val _notifications = MutableStateFlow<List<AppNotification>>(emptyList())
    val notifications = _notifications.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    init {
        viewModelScope.launch {
            observeCurrentUserUseCase().collectLatest { user ->
                _notifications.value = emptyList()

                if (user == null) {
                    _isLoading.value = false
                    return@collectLatest
                }

                _isLoading.value = true
                observeNotificationsUseCase(user.id).collect { notifications ->
                    _notifications.value = notifications
                    _isLoading.value = false
                }
            }
        }
    }
}