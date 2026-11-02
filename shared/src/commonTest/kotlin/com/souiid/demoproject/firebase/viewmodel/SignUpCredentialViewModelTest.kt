package com.souiid.demoproject.firebase.viewmodel

import com.souiid.demoproject.firebase.domain.model.AppError
import com.souiid.demoproject.firebase.domain.repository.ISignUpRepository
import com.souiid.demoproject.firebase.domain.usecase.signup.SignUpUserUseCase
import com.souiid.demoproject.ui.screens.auth.signup.credential.SignUpCredentialViewModel
import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.everySuspend
import dev.mokkery.mock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class SignUpCredentialViewModelTest {

    private val dispatcher = StandardTestDispatcher()
    private val repository = mock<ISignUpRepository>()
    private val viewModel = SignUpCredentialViewModel(SignUpUserUseCase(repository))

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(dispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun initialStateIsEmpty() = runTest(dispatcher) {
        assertEquals("", viewModel.email)
        assertEquals("", viewModel.password)
        assertEquals("", viewModel.confirmPassword)
        assertNull(viewModel.error)
        assertFalse(viewModel.isLoading)
        assertFalse(viewModel.isContinueEnabled)
    }

    @Test
    fun continueIsEnabledOnlyWithValidCredentials() = runTest(dispatcher) {
        viewModel.email = "invalid-email"
        viewModel.password = "password123"
        viewModel.confirmPassword = "password123"
        assertFalse(viewModel.isContinueEnabled)

        viewModel.email = "test@example.com"
        viewModel.password = "1234567"
        viewModel.confirmPassword = "1234567"
        assertFalse(viewModel.isContinueEnabled)

        viewModel.password = "password123"
        viewModel.confirmPassword = "different123"
        assertFalse(viewModel.isContinueEnabled)

        viewModel.confirmPassword = "password123"
        assertTrue(viewModel.isContinueEnabled)
    }

    @Test
    fun successfulSignUpClearsLoading() = runTest(dispatcher) {
        everySuspend {
            repository.signUp("test@example.com", "password123")
        } returns Unit

        viewModel.email = "test@example.com"
        viewModel.password = "password123"
        viewModel.confirmPassword = "password123"
        viewModel.signUp()

        advanceUntilIdle()

        assertFalse(viewModel.isLoading)
        assertNull(viewModel.error)
    }

    @Test
    fun failedSignUpSetsError() = runTest(dispatcher) {
        viewModel.signUp()
        advanceUntilIdle()

        assertEquals(
            AppError.Validation(AppError.Field.EMAIL, AppError.ValidationReason.REQUIRED),
            viewModel.error
        )
        assertFalse(viewModel.isLoading)
    }

    @Test
    fun repositoryFailureSetsAuthenticationError() = runTest(dispatcher) {
        everySuspend {
            repository.signUp("test@example.com", "password123")
        } throws Exception("Firebase error")

        viewModel.email = "test@example.com"
        viewModel.password = "password123"
        viewModel.confirmPassword = "password123"
        viewModel.signUp()

        advanceUntilIdle()

        assertEquals(
            AppError.Authentication(AppError.AuthenticationReason.UNABLE_TO_CREATE_ACCOUNT),
            viewModel.error
        )
        assertFalse(viewModel.isLoading)
    }

    @Test
    fun clearErrorRemovesError() = runTest(dispatcher) {
        viewModel.signUp()
        advanceUntilIdle()

        viewModel.clearError()

        assertNull(viewModel.error)
    }
}