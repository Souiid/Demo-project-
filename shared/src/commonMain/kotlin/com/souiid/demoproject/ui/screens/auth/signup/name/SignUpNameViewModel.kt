package com.souiid.demoproject.ui.screens.auth.signup.name

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.souiid.demoproject.domain.model.AppError
import com.souiid.demoproject.domain.model.AppResult
import com.souiid.demoproject.domain.usecase.signup.ObserveSignUpInfoUseCase
import com.souiid.demoproject.domain.usecase.signup.UpdateSignUpNameUseCase

class SignUpNameViewModel(
    observeSignUpInfoUseCase: ObserveSignUpInfoUseCase,
    private val updateSignUpNameUseCase: UpdateSignUpNameUseCase
) : ViewModel() {
    private val signUpInfo = observeSignUpInfoUseCase()

    var firstName by mutableStateOf(signUpInfo.value.firstName)
    var lastName by mutableStateOf(signUpInfo.value.lastName)
    var error by mutableStateOf<AppError.Validation?>(null)
        private set

    fun clearError() {
        error = null
    }

    fun updateName(): Boolean {
        return when (val result = updateSignUpNameUseCase(firstName, lastName)) {
            is AppResult.Success -> {
                error = null
                true
            }
            is AppResult.Failure -> {
                error = result.error as? AppError.Validation
                false
            }
        }
    }
}