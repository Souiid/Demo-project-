package com.souiid.demoproject.firebase.viewmodel

import com.souiid.demoproject.firebase.domain.model.AppError
import com.souiid.demoproject.firebase.domain.repository.IAuthRepository
import com.souiid.demoproject.firebase.domain.usecase.auth.SignInUseCase
import com.souiid.demoproject.ui.screens.auth.signin.SignInViewModel
import dev.mokkery.MockMode
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.mock
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.test.resetMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull

@OptIn(ExperimentalCoroutinesApi::class)
class SignInViewModelTest {

    private val dispatcher = StandardTestDispatcher()
    private val repository = mock<IAuthRepository>(MockMode.autoUnit)
    private val viewModel = SignInViewModel(SignInUseCase(repository))

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
        assertNull(viewModel.error)
        assertFalse(viewModel.isLoading)
    }

    @Test
    fun successfulSignInClearsLoading() = runTest(dispatcher) {
        everySuspend {
            repository.signIn("test@example.com", "password123")
        } returns Unit

        viewModel.email = "test@example.com"
        viewModel.password = "password123"
        viewModel.signIn()

        advanceUntilIdle()

        assertFalse(viewModel.isLoading)
        assertNull(viewModel.error)
    }

    @Test
    fun failedSignInSetsError() = runTest(dispatcher) {
        viewModel.signIn()
        advanceUntilIdle()

        assertEquals(
            AppError.Validation(AppError.Field.EMAIL, AppError.ValidationReason.REQUIRED),
            viewModel.error
        )
        assertFalse(viewModel.isLoading)
    }

    @Test
    fun clearErrorRemovesError() = runTest(dispatcher) {
        viewModel.signIn()
        advanceUntilIdle()

        viewModel.clearError()

        assertNull(viewModel.error)
    }

    @Test
    fun signInWhileLoadingIsIgnored() = runTest(dispatcher) {
        val pendingSignIn = CompletableDeferred<Unit>()
        everySuspend {
            repository.signIn("test@example.com", "password123")
        } returns Unit

        viewModel.email = "test@example.com"
        viewModel.password = "password123"
        viewModel.signIn()
        runCurrent()

        assertFalse(viewModel.isLoading)
    }
}