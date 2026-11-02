package com.souiid.demoproject.firebase.viewmodel

import com.souiid.demoproject.firebase.domain.model.AppError
import com.souiid.demoproject.firebase.domain.model.SignUpInfo
import com.souiid.demoproject.firebase.domain.repository.ISignUpRepository
import com.souiid.demoproject.firebase.domain.usecase.signup.ObserveSignUpInfoUseCase
import com.souiid.demoproject.firebase.domain.usecase.signup.UpdateSignUpNameUseCase
import com.souiid.demoproject.ui.screens.auth.signup.name.SignUpNameViewModel
import dev.mokkery.MockMode
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.mock
import dev.mokkery.verify
import kotlinx.coroutines.flow.MutableStateFlow
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class SignUpNameViewModelTest {

    private val repository = mock<ISignUpRepository>(MockMode.autoUnit)
    private val signUpInfo = MutableStateFlow(SignUpInfo())

    private fun createViewModel(): SignUpNameViewModel {
        every { repository.signUpInfo } returns signUpInfo
        return SignUpNameViewModel(
            ObserveSignUpInfoUseCase(repository),
            UpdateSignUpNameUseCase(repository)
        )
    }

    @Test
    fun initialStateUsesSavedNames() {
        signUpInfo.value = SignUpInfo(firstName = "John", lastName = "Doe")
        val viewModel = createViewModel()

        assertEquals("John", viewModel.firstName)
        assertEquals("Doe", viewModel.lastName)
        assertNull(viewModel.error)
        assertTrue(viewModel.isContinueEnabled)
    }

    @Test
    fun continueIsDisabledWhenNameIsMissing() {
        val viewModel = createViewModel()

        assertFalse(viewModel.isContinueEnabled)

        viewModel.firstName = "John"
        assertFalse(viewModel.isContinueEnabled)

        viewModel.lastName = "Doe"
        assertTrue(viewModel.isContinueEnabled)

        viewModel.firstName = " "
        assertFalse(viewModel.isContinueEnabled)
    }

    @Test
    fun updateNameWithValidNamesReturnsSuccess() {
        val viewModel = createViewModel()
        viewModel.firstName = "  John  "
        viewModel.lastName = "  Doe  "

        val result = viewModel.updateName()

        assertTrue(result)
        assertNull(viewModel.error)
        verify { repository.updateFirstName("John") }
        verify { repository.updateLastName("Doe") }
    }

    @Test
    fun updateNameWithEmptyFirstNameReturnsValidationError() {
        val viewModel = createViewModel()
        viewModel.firstName = ""
        viewModel.lastName = "Doe"

        val result = viewModel.updateName()

        assertFalse(result)
        assertEquals(
            AppError.Validation(AppError.Field.FIRST_NAME, AppError.ValidationReason.REQUIRED),
            viewModel.error
        )
    }

    @Test
    fun updateNameWithEmptyLastNameReturnsValidationError() {
        val viewModel = createViewModel()
        viewModel.firstName = "John"
        viewModel.lastName = ""

        val result = viewModel.updateName()

        assertFalse(result)
        assertEquals(
            AppError.Validation(AppError.Field.LAST_NAME, AppError.ValidationReason.REQUIRED),
            viewModel.error
        )
    }

    @Test
    fun clearErrorRemovesValidationError() {
        val viewModel = createViewModel()
        viewModel.updateName()

        assertEquals(
            AppError.Validation(AppError.Field.FIRST_NAME, AppError.ValidationReason.REQUIRED),
            viewModel.error
        )

        viewModel.clearError()

        assertNull(viewModel.error)
    }
}