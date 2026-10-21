package com.souiid.demoproject.domain.usecase.signup

import com.souiid.demoproject.data.SignUpRepository
import com.souiid.demoproject.domain.model.AppResult

class UpdateSignUpDescriptionUseCase(
    private val repository: SignUpRepository
) {

    operator fun invoke(description: String): AppResult<Unit> {
        repository.updateDescription(description.trim())
        return AppResult.Success(Unit)
    }
}