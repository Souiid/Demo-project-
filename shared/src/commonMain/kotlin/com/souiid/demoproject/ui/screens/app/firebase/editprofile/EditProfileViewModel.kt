package com.souiid.demoproject.ui.screens.app.firebase.editprofile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.souiid.demoproject.domain.model.AppError
import com.souiid.demoproject.domain.model.AppResult
import com.souiid.demoproject.domain.usecase.user.ObserveCurrentUserUseCase
import com.souiid.demoproject.domain.usecase.user.UpdateUserUseCase
import com.souiid.demoproject.domain.usecase.user.UploadPhotoUseCase
import kotlinx.coroutines.launch

class EditProfileViewModel(
    observeCurrentUserUseCase: ObserveCurrentUserUseCase,
    private val updateUserUseCase: UpdateUserUseCase,
    private val uploadPhotoUseCase: UploadPhotoUseCase
) : ViewModel() {

    val currentUser = observeCurrentUserUseCase()

    var firstName by mutableStateOf(currentUser.value?.firstName.orEmpty())
    var lastName by mutableStateOf(currentUser.value?.lastName.orEmpty())
    var description by mutableStateOf(currentUser.value?.description.orEmpty())
    var photoUri by mutableStateOf<String?>(null)

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
                if (firstName.isBlank() || lastName.isBlank()) {
                    when (val result = updateUserUseCase(firstName, lastName, description)) {
                        is AppResult.Failure -> error = result.error
                        is AppResult.Success -> Unit
                    }
                    return@launch
                }

                var photoUrl: String? = null

                if (photoUri != null) {
                    when (val result = uploadPhotoUseCase(photoUri!!)) {
                        is AppResult.Success -> photoUrl = result.data
                        is AppResult.Failure -> {
                            error = result.error
                            return@launch
                        }
                    }
                }

                when (val result = updateUserUseCase(firstName, lastName, description, photoUrl)) {
                    is AppResult.Success -> isSuccess = true
                    is AppResult.Failure -> error = result.error
                }
            } finally {
                isLoading = false
            }
        }
    }
}