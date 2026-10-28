package com.souiid.demoproject.ui.screens.auth.signup.credential

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.souiid.demoproject.firebase.domain.model.AppError
import com.souiid.demoproject.firebase.domain.model.AppResult
import com.souiid.demoproject.firebase.domain.usecase.signup.SignUpUserUseCase
import kotlinx.coroutines.launch

class SignUpCredentialViewModel(
    private val signUpUseCase: SignUpUserUseCase
) : ViewModel() {
    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var confirmPassword by mutableStateOf("")

    var error by mutableStateOf<AppError?>(null)
        private set

    var isLoading by mutableStateOf(false)
        private set

    fun clearError() {
        error = null
    }

    fun signUp() {
        if (isLoading) return

        viewModelScope.launch {
            isLoading = true
            error = null

            try {
                when (val result = signUpUseCase(email, password, confirmPassword)) {
                    is AppResult.Success -> Unit
                    is AppResult.Failure -> error = result.error
                }
            } finally {
                isLoading = false
            }
        }
    }
}