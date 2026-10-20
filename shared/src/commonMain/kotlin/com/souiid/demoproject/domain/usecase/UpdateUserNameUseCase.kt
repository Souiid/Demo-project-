package com.souiid.demoproject.domain.usecase

import com.souiid.demoproject.data.AuthRepository

class UpdateUserNameUseCase(
    private val authRepository: AuthRepository
) {
     operator fun invoke(name: String) = authRepository.updateUserName(name)
}