package com.souiid.demoproject.firebase.domain.usecase.notification

import com.souiid.demoproject.firebase.domain.repository.INotificationRepository

class SaveNotificationTokenUseCase(
    private val repository: INotificationRepository
) {
    suspend operator fun invoke(userId: String) {
        repository.saveToken(userId)
    }
}