package com.souiid.demoproject.domain.usecase

import com.souiid.demoproject.data.AuthRepository

class SignOutUseCase(
    private val repository: AuthRepository
) {
    suspend operator fun invoke() {
        repository.signOut()
    }
}