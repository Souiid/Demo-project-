package com.souiid.demoproject.firebase.domain.usecase.auth

import com.souiid.demoproject.firebase.domain.repository.IAuthRepository
import com.souiid.demoproject.firebase.domain.repository.INotificationRepository
import com.souiid.demoproject.firebase.domain.usecase.user.ObserveCurrentUserUseCase

class SignOutUseCase(
    private val repository: IAuthRepository,
    private val notificationRepository: INotificationRepository,
    private val observeCurrentUserUseCase: ObserveCurrentUserUseCase
) {
    suspend operator fun invoke() {
        val userId = observeCurrentUserUseCase().value?.id

        if (userId != null) {
            notificationRepository.deleteToken(userId)
        }

        repository.signOut()
    }
}