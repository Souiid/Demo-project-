package com.souiid.demoproject.ui.screens.auth.signup.description

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.souiid.demoproject.firebase.domain.usecase.signup.ObserveSignUpInfoUseCase
import com.souiid.demoproject.firebase.domain.usecase.signup.UpdateSignUpDescriptionUseCase

class SignUpDescriptionViewModel(
    observeSignUpInfoUseCase: ObserveSignUpInfoUseCase,
    private val updateSignUpDescriptionUseCase: UpdateSignUpDescriptionUseCase,
): ViewModel() {
    private val signUpInfo = observeSignUpInfoUseCase()

    var description by mutableStateOf(signUpInfo.value.description)

    fun updateDescription() {
        updateSignUpDescriptionUseCase(description)
    }
}
