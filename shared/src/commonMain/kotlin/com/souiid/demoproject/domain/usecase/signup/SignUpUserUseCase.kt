package com.souiid.demoproject.domain.usecase.signup

import com.souiid.demoproject.data.SignUpRepository
import com.souiid.demoproject.domain.model.AppError
import com.souiid.demoproject.domain.model.AppResult
import kotlin.coroutines.cancellation.CancellationException

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
            return AppResult.Failure(AppError.Validation(AppError.Field.EMAIL, "Email is required"))
        }

        if (!EMAIL_REGEX.matches(cleanEmail)) {
            return AppResult.Failure(AppError.Validation(AppError.Field.EMAIL, "Invalid email address"))
        }

        if (password.length < 8) {
            return AppResult.Failure(AppError.Validation(AppError.Field.PASSWORD, "Password must contain at least 8 characters"))
        }

        if (password != confirmPassword) {
            return AppResult.Failure(AppError.Validation(AppError.Field.CONFIRM_PASSWORD, "Passwords do not match"))
        }

        return try {
            repository.signUp(cleanEmail, password)
            AppResult.Success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            AppResult.Failure(AppError.Unknown("Unable to create your account"))
        }
    }

    companion object {
        private val EMAIL_REGEX = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
    }
}