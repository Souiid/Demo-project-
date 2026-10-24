package com.souiid.demoproject.domain.usecase.user

import com.souiid.demoproject.domain.model.User
import com.souiid.demoproject.domain.repository.IUserRepository
import kotlinx.coroutines.flow.StateFlow

class ObserveCurrentUserUseCase(
    private val userRepository: IUserRepository
) {
    operator fun invoke(): StateFlow<User?> {
        return userRepository.currentUser
    }
}