package com.souiid.demoproject.firebase.domain.usecase.signup

import com.souiid.demoproject.firebase.domain.repository.ISignUpRepository

class ObserveSignUpInfoUseCase(
    private val repository: ISignUpRepository
) {
    operator fun invoke() = repository.signUpInfo
}