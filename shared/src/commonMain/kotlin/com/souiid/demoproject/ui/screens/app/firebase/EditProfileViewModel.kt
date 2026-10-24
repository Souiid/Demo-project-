package com.souiid.demoproject.ui.screens.app.firebase

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.souiid.demoproject.domain.model.AppError
import com.souiid.demoproject.domain.model.AppResult
import com.souiid.demoproject.domain.usecase.user.ObserveCurrentUserUseCase
import com.souiid.demoproject.domain.usecase.user.UpdateUserUseCase
import kotlinx.coroutines.launch

class EditProfileViewModel(
    observeCurrentUserUseCase: ObserveCurrentUserUseCase,
    private val updateUserUseCase: UpdateUserUseCase
) : ViewModel() {

    val currentUser = observeCurrentUserUseCase()

    var firstName by mutableStateOf(currentUser.value?.firstName.orEmpty())
    var lastName by mutableStateOf(currentUser.value?.lastName.orEmpty())
    var description by mutableStateOf(currentUser.value?.description.orEmpty())

    var isLoading by mutableStateOf(false)
        private set
    var isSuccess by mutableStateOf(false)
        private set
    var error by mutableStateOf<AppError?>(null)
        private set

    fun clearError() {
        error = null
    }

    fun updateUser() {
        if (isLoading) return

        viewModelScope.launch {
            isLoading = true
            isSuccess = false
            error = null

            try {
                when (val result = updateUserUseCase(firstName, lastName, description)) {
                    is AppResult.Success -> {
                        isSuccess = true
                    }
                    is AppResult.Failure -> {
                        error = result.error
                    }
                }
            } finally {
                isLoading = false
            }
        }
    }
}