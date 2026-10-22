package com.souiid.demoproject.domain.usecase.signup

import com.souiid.demoproject.domain.model.AppResult
import com.souiid.demoproject.domain.repository.ISignUpRepository

class UpdateSignUpDescriptionUseCase(
    private val repository: ISignUpRepository
) {

    operator fun invoke(description: String): AppResult<Unit> {
        repository.updateDescription(description.trim())
        return AppResult.Success(Unit)
    }
}