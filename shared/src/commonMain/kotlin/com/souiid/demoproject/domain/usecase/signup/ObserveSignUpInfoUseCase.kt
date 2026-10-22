package com.souiid.demoproject.domain.usecase.signup

import com.souiid.demoproject.domain.repository.ISignUpRepository

class ObserveSignUpInfoUseCase(
    private val repository: ISignUpRepository
) {
    operator fun invoke() = repository.signUpInfo
}