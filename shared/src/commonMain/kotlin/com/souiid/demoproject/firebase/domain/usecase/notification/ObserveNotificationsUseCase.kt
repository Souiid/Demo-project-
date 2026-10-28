package com.souiid.demoproject.firebase.domain.usecase.notification

import com.souiid.demoproject.firebase.domain.repository.INotificationRepository

class ObserveNotificationsUseCase(
    private val repository: INotificationRepository
) {
    operator fun invoke(userId: String) = repository.observeNotifications(userId)
}