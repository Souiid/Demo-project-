package com.souiid.demoproject.domain.usecase

import com.souiid.demoproject.data.AuthRepository
import com.souiid.demoproject.domain.model.AuthState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.transformLatest

class ObserveAuthStateUseCase(
    private val authRepository: AuthRepository
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(): Flow<AuthState> = authRepository.authState.transformLatest { user ->
        if (user == null) {
            emit(AuthState.UNAUTHENTICATED)
        } else {
            emit(AuthState.LOADING)
            emitAll(
                authRepository.observeUserProfile(user.uid).map { exists ->
                    if (exists) AuthState.AUTHENTICATED else AuthState.LOADING
                }
            )
        }
    }
}