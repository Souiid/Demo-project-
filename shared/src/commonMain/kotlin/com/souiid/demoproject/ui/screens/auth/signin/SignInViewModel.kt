package com.souiid.demoproject.ui.screens.auth.signin

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.souiid.demoproject.firebase.domain.model.AppError
import com.souiid.demoproject.firebase.domain.model.AppResult
import com.souiid.demoproject.firebase.domain.usecase.auth.SignInUseCase
import kotlinx.coroutines.launch

class SignInViewModel(
    private val signInUseCase: SignInUseCase
) : ViewModel() {

    var email by mutableStateOf("")
    var password by mutableStateOf("")

    var error by mutableStateOf<AppError?>(null)
        private set

    var isLoading by mutableStateOf(false)
        private set

    fun clearError() {
        error = null
    }

    fun signIn() {
        if (isLoading) return

        viewModelScope.launch {
            isLoading = true
            error = null

            try {
                when (val result = signInUseCase(email, password)) {
                    is AppResult.Success -> Unit
                    is AppResult.Failure -> error = result.error
                }
            } finally {
                isLoading = false
            }
        }
    }
}