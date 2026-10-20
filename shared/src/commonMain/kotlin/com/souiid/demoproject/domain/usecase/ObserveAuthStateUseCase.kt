package com.souiid.demoproject.domain.usecase

import com.souiid.demoproject.data.AuthRepository
import dev.gitlive.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow

class ObserveAuthStateUseCase(
    private val authRepository: AuthRepository
) {
    operator fun invoke(): Flow<FirebaseUser?> = authRepository.authState
}