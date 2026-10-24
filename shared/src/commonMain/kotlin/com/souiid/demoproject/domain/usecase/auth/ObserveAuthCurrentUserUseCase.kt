package com.souiid.demoproject.domain.usecase.auth

import com.souiid.demoproject.domain.repository.IAuthRepository
import dev.gitlive.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow

class ObserveAuthCurrentUserUseCase(
    private val authRepository: IAuthRepository
) {
    operator fun invoke(): Flow<FirebaseUser?> {
        return authRepository.authState
    }
}
