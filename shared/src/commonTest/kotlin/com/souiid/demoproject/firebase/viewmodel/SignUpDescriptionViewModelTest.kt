package com.souiid.demoproject.firebase.viewmodel

import com.souiid.demoproject.firebase.domain.model.SignUpInfo
import com.souiid.demoproject.firebase.domain.repository.ISignUpRepository
import com.souiid.demoproject.firebase.domain.usecase.signup.ObserveSignUpInfoUseCase
import com.souiid.demoproject.firebase.domain.usecase.signup.UpdateSignUpDescriptionUseCase
import com.souiid.demoproject.ui.screens.auth.signup.description.SignUpDescriptionViewModel
import dev.mokkery.MockMode
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.mock
import dev.mokkery.verify
import kotlinx.coroutines.flow.MutableStateFlow
import kotlin.test.Test
import kotlin.test.assertEquals

class SignUpDescriptionViewModelTest {

    private val repository = mock<ISignUpRepository>(MockMode.autoUnit)
    private val signUpInfo = MutableStateFlow(SignUpInfo())

    private fun createViewModel(): SignUpDescriptionViewModel {
        every { repository.signUpInfo } returns signUpInfo
        return SignUpDescriptionViewModel(
            ObserveSignUpInfoUseCase(repository),
            UpdateSignUpDescriptionUseCase(repository)
        )
    }

    @Test
    fun initialStateUsesSavedDescription() {
        signUpInfo.value = SignUpInfo(description = "Hello!")
        val viewModel = createViewModel()

        assertEquals("Hello!", viewModel.description)
    }

    @Test
    fun updateDescriptionSavesTrimmedValue() {
        val viewModel = createViewModel()
        viewModel.description = "  My description  "

        viewModel.updateDescription()

        verify { repository.updateDescription("My description") }
    }
}