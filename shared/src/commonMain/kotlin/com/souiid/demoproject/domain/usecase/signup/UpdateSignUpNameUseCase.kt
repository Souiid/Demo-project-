package com.souiid.demoproject.domain.usecase.signup

import com.souiid.demoproject.data.SignUpRepository
import com.souiid.demoproject.domain.model.AppError
import com.souiid.demoproject.domain.model.AppResult

class UpdateSignUpNameUseCase(
    private val repository: SignUpRepository
) {
    operator fun invoke(firstName: String, lastName: String): AppResult<Unit> {
        if (firstName.isBlank()) {
            return AppResult.Failure(
                AppError.Validation(
                    AppError.Field.FIRST_NAME,
                    "First name is required"
                )
            )
        }

        if (lastName.isBlank()) {
            return AppResult.Failure(
                AppError.Validation(
                    AppError.Field.LAST_NAME,
                    "Last name is required"
                )
            )
        }

        repository.updateFirstName(firstName.trim())
        repository.updateLastName(lastName.trim())

        return AppResult.Success(Unit)
    }
}