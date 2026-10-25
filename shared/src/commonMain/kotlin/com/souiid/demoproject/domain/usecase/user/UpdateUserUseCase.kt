package com.souiid.demoproject.domain.usecase.user

import com.souiid.demoproject.domain.model.AppError
import com.souiid.demoproject.domain.model.AppError.Field
import com.souiid.demoproject.domain.model.AppError.ValidationReason
import com.souiid.demoproject.domain.model.AppResult
import com.souiid.demoproject.domain.repository.IUserRepository

class UpdateUserUseCase(
    private val userRepository: IUserRepository
) {
    suspend operator fun invoke(
        firstName: String,
        lastName: String,
        description: String,
        photoUrl: String? = null
    ): AppResult<Unit> {
        if (firstName.isBlank()) return AppResult.Failure(
            AppError.Validation(Field.FIRST_NAME, ValidationReason.REQUIRED)
        )
        if (lastName.isBlank()) return AppResult.Failure(
            AppError.Validation(Field.LAST_NAME, ValidationReason.REQUIRED)
        )

        return userRepository.updateUser(firstName.trim(), lastName.trim(), description.trim(), photoUrl)
    }
}