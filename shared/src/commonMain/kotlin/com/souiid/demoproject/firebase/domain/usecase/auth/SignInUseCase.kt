package com.souiid.demoproject.firebase.domain.usecase.auth

import com.souiid.demoproject.firebase.domain.model.AppError
import com.souiid.demoproject.firebase.domain.model.AppResult
import com.souiid.demoproject.firebase.domain.repository.IAuthRepository
import kotlinx.coroutines.CancellationException

class SignInUseCase(
    private val repository: IAuthRepository
) {
    suspend operator fun invoke(email: String, password: String): AppResult<Unit> {
        val cleanEmail = email.trim()

        if (cleanEmail.isBlank()) {
            return AppResult.Failure(
                AppError.Validation(AppError.Field.EMAIL, AppError.ValidationReason.REQUIRED)
            )
        }

        if (password.isBlank()) {
            return AppResult.Failure(
                AppError.Validation(AppError.Field.PASSWORD, AppError.ValidationReason.REQUIRED)
            )
        }

        return try {
            repository.signIn(cleanEmail, password)
            AppResult.Success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            AppResult.Failure(
                AppError.Authentication(AppError.AuthenticationReason.INVALID_CREDENTIALS)
            )
        }
    }
}