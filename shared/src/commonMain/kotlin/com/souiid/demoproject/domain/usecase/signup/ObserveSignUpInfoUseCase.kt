package com.souiid.demoproject.domain.usecase.signup

import com.souiid.demoproject.data.SignUpRepository

class ObserveSignUpInfoUseCase(
    private val repository: SignUpRepository
) {
    operator fun invoke() = repository.signUpInfo
}