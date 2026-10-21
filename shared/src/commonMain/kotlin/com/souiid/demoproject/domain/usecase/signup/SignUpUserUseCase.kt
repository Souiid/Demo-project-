package com.souiid.demoproject.domain.usecase.signup

import com.souiid.demoproject.data.SignUpRepository
import com.souiid.demoproject.domain.model.AppError
import com.souiid.demoproject.domain.model.AppResult
import kotlinx.coroutines.CancellationException

class SignUpUserUseCase(
    private val repository: SignUpRepository
) {
    suspend operator fun invoke(
        email: String,
        password: String,
        confirmPassword: String
    ): AppResult<Unit> {
        val cleanEmail = email.trim()

        if (cleanEmail.isBlank()) {
            return AppResult.Failure(
                AppError.Validation(AppError.Field.EMAIL, AppError.ValidationReason.REQUIRED)
            )
        }

        if (!EMAIL_REGEX.matches(cleanEmail)) {
            return AppResult.Failure(
                AppError.Validation(AppError.Field.EMAIL, AppError.ValidationReason.INVALID_EMAIL)
            )
        }

        if (password.length < 8) {
            return AppResult.Failure(
                AppError.Validation(AppError.Field.PASSWORD, AppError.ValidationReason.PASSWORD_TOO_SHORT)
            )
        }

        if (password != confirmPassword) {
            return AppResult.Failure(
                AppError.Validation(AppError.Field.CONFIRM_PASSWORD, AppError.ValidationReason.PASSWORDS_DO_NOT_MATCH)
            )
        }

        return try {
            repository.signUp(cleanEmail, password)
            AppResult.Success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (_: Exception) {
            AppResult.Failure(
                AppError.Authentication(AppError.AuthenticationReason.UNABLE_TO_CREATE_ACCOUNT)
            )
        }
    }

    companion object {
        private val EMAIL_REGEX = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
    }
}