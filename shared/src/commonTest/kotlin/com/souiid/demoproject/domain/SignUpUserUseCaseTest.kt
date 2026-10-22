package com.souiid.demoproject.domain

import com.souiid.demoproject.domain.model.AppError
import com.souiid.demoproject.domain.model.AppResult
import com.souiid.demoproject.domain.repository.ISignUpRepository
import com.souiid.demoproject.domain.usecase.signup.SignUpUserUseCase
import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.everySuspend
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs

class SignUpUserUseCaseTest {

    private val repository = mock<ISignUpRepository>()
    private val useCase = SignUpUserUseCase(repository)

    @Test
    fun signUpWithEmptyEmailReturnsRequiredError() = runTest {
        val result = useCase("", "password123", "password123")

        assertEquals(
            AppResult.Failure(
                AppError.Validation(AppError.Field.EMAIL, AppError.ValidationReason.REQUIRED)
            ),
            result
        )
    }

    @Test
    fun signUpWithInvalidEmailReturnsInvalidEmailError() = runTest {
        val result = useCase("invalid-email", "password123", "password123")

        assertEquals(
            AppResult.Failure(
                AppError.Validation(AppError.Field.EMAIL, AppError.ValidationReason.INVALID_EMAIL)
            ),
            result
        )
    }

    @Test
    fun signUpWithShortPasswordReturnsPasswordTooShortError() = runTest {
        val result = useCase("test@example.com", "1234567", "1234567")

        assertEquals(
            AppResult.Failure(
                AppError.Validation(AppError.Field.PASSWORD, AppError.ValidationReason.PASSWORD_TOO_SHORT)
            ),
            result
        )
    }

    @Test
    fun signUpWithDifferentPasswordsReturnsMismatchError() = runTest {
        val result = useCase("test@example.com", "password123", "password456")

        assertEquals(
            AppResult.Failure(
                AppError.Validation(AppError.Field.CONFIRM_PASSWORD, AppError.ValidationReason.PASSWORDS_DO_NOT_MATCH)
            ),
            result
        )
    }

    @Test
    fun signUpWithValidCredentialsReturnsSuccess() = runTest {
        everySuspend { repository.signUp("test@example.com", "password123") } returns Unit

        val result = useCase("test@example.com", "password123", "password123")

        assertIs<AppResult.Success<Unit>>(result)
        verifySuspend { repository.signUp("test@example.com", "password123") }
    }

    @Test
    fun signUpTrimsEmail() = runTest {
        everySuspend { repository.signUp("test@example.com", "password123") } returns Unit

        useCase("  test@example.com  ", "password123", "password123")

        verifySuspend { repository.signUp("test@example.com", "password123") }
    }

    @Test
    fun signUpFailureReturnsAuthenticationError() = runTest {
        everySuspend {
            repository.signUp("test@example.com", "password123")
        } throws Exception("Firebase error")

        val result = useCase("test@example.com", "password123", "password123")

        assertEquals(
            AppResult.Failure(
                AppError.Authentication(AppError.AuthenticationReason.UNABLE_TO_CREATE_ACCOUNT)
            ),
            result
        )
    }

    @Test
    fun signUpCancellationIsRethrown() = runTest {
        everySuspend {
            repository.signUp("test@example.com", "password123")
        } throws CancellationException("Cancelled")

        assertFailsWith<CancellationException> {
            useCase("test@example.com", "password123", "password123")
        }
    }
}