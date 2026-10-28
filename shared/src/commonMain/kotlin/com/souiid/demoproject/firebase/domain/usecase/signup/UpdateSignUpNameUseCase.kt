package com.souiid.demoproject.firebase.domain.usecase.signup

import com.souiid.demoproject.firebase.domain.model.AppError
import com.souiid.demoproject.firebase.domain.model.AppResult
import com.souiid.demoproject.firebase.domain.repository.ISignUpRepository

class UpdateSignUpNameUseCase(
    private val repository: ISignUpRepository
) {
    operator fun invoke(firstName: String, lastName: String): AppResult<Unit> {
        if (firstName.isBlank()) {
            return AppResult.Failure(
                AppError.Validation(
                    AppError.Field.FIRST_NAME,
                    AppError.ValidationReason.REQUIRED
                )
            )
        }

        if (lastName.isBlank()) {
            return AppResult.Failure(
                AppError.Validation(
                    AppError.Field.LAST_NAME,
                    AppError.ValidationReason.REQUIRED
                )
            )
        }

        repository.updateFirstName(firstName.trim())
        repository.updateLastName(lastName.trim())

        return AppResult.Success(Unit)
    }
}