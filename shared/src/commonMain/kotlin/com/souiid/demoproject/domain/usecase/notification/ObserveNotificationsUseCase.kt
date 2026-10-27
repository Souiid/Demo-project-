package com.souiid.demoproject.domain.usecase.notification

import com.souiid.demoproject.domain.repository.INotificationRepository

class ObserveNotificationsUseCase(
    private val repository: INotificationRepository
) {
    operator fun invoke(userId: String) = repository.observeNotifications(userId)
}