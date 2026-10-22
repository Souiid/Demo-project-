package com.souiid.demoproject.domain.usecase.auth

import com.souiid.demoproject.domain.repository.IAuthRepository

class SignOutUseCase(
    private val repository: IAuthRepository
) {
    suspend operator fun invoke() {
        repository.signOut()
    }
}