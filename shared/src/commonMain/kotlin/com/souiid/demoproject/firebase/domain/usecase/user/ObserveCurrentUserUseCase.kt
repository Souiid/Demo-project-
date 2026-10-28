package com.souiid.demoproject.firebase.domain.usecase.user

import com.souiid.demoproject.firebase.domain.model.User
import com.souiid.demoproject.firebase.domain.repository.IUserRepository
import kotlinx.coroutines.flow.StateFlow

class ObserveCurrentUserUseCase(
    private val userRepository: IUserRepository
) {
    operator fun invoke(): StateFlow<User?> {
        return userRepository.currentUser
    }
}