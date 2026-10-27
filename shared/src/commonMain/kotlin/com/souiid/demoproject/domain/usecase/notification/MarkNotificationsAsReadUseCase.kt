package com.souiid.demoproject.domain.usecase.notification

import com.souiid.demoproject.domain.repository.INotificationRepository

class MarkNotificationsAsReadUseCase(
    private val repository: INotificationRepository
) {
    suspend operator fun invoke(userId: String) {
        repository.markNotificationsAsRead(userId)
    }
}