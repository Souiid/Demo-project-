package com.souiid.demoproject.domain

import com.souiid.demoproject.domain.model.AppError
import com.souiid.demoproject.domain.model.AppResult
import com.souiid.demoproject.domain.repository.IAuthRepository
import com.souiid.demoproject.domain.usecase.auth.SignInUseCase
import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.everySuspend
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class SignInUseCaseTest {

    private val repository = mock<IAuthRepository>()
    private val useCase = SignInUseCase(repository)

    @Test
    fun signInWithEmptyEmailReturnsRequiredError() = runTest {
        val result = useCase("", "password123")

        assertEquals(
            AppResult.Failure(
                AppError.Validation(AppError.Field.EMAIL, AppError.ValidationReason.REQUIRED)
            ),
            result
        )
    }

    @Test
    fun signInWithEmptyPasswordReturnsRequiredError() = runTest {
        val result = useCase("test@example.com", "")

        assertEquals(
            AppResult.Failure(
                AppError.Validation(AppError.Field.PASSWORD, AppError.ValidationReason.REQUIRED)
            ),
            result
        )
    }

    @Test
    fun signInWithValidCredentialsReturnsSuccess() = runTest {
        everySuspend { repository.signIn("test@example.com", "password123") } returns Unit

        val result = useCase("test@example.com", "password123")

        assertIs<AppResult.Success<Unit>>(result)
        verifySuspend { repository.signIn("test@example.com", "password123") }
    }

    @Test
    fun signInTrimsEmail() = runTest {
        everySuspend { repository.signIn("test@example.com", "password123") } returns Unit

        useCase("  test@example.com  ", "password123")

        verifySuspend { repository.signIn("test@example.com", "password123") }
    }

    @Test
    fun signInFailureReturnsInvalidCredentials() = runTest {
        everySuspend {
            repository.signIn("test@example.com", "wrongpassword")
        } throws Exception("Authentication failed")

        val result = useCase("test@example.com", "wrongpassword")

        assertEquals(
            AppResult.Failure(
                AppError.Authentication(AppError.AuthenticationReason.INVALID_CREDENTIALS)
            ),
            result
        )
    }
}