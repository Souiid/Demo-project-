package com.souiid.demoproject.ui.screens.app.firebase

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.souiid.demoproject.domain.model.AppError
import com.souiid.demoproject.domain.model.AppResult
import com.souiid.demoproject.domain.usecase.user.FetchUserUseCase
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import kotlinx.coroutines.launch
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.souiid.demoproject.domain.usecase.auth.SignOutUseCase

class FirebaseFeedViewModel(
    private val signOutUseCase: SignOutUseCase,
    private val fetchUserUseCase: FetchUserUseCase
) : ViewModel() {

    var error by mutableStateOf<AppError?>(null)
        private set

    var isLoading by mutableStateOf(false)
        private set

    fun loadCurrentUser() {
        if (isLoading) return

        val userId = Firebase.auth.currentUser?.uid ?: run {
            error = AppError.Unknown()
            return
        }

        viewModelScope.launch {
            isLoading = true
            error = null

            try {
                when (val result = fetchUserUseCase(userId)) {
                    is AppResult.Success -> Unit
                    is AppResult.Failure -> error = result.error
                }
            } finally {
                isLoading = false
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            signOutUseCase()
        }
    }
}