package com.souiid.demoproject.ui.screens.app.firebase.profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.souiid.demoproject.domain.model.AppError
import com.souiid.demoproject.domain.model.AppResult
import com.souiid.demoproject.domain.model.User
import com.souiid.demoproject.domain.usecase.auth.SignOutUseCase
import com.souiid.demoproject.domain.usecase.user.FetchUserUseCase
import com.souiid.demoproject.domain.usecase.user.ObserveCurrentUserUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FirebaseProfileViewModel(
    private val fetchUserUseCase: FetchUserUseCase,
    private val observeCurrentUserUseCase: ObserveCurrentUserUseCase,
    private val signOutUseCase: SignOutUseCase
) : ViewModel() {

    val currentUser = observeCurrentUserUseCase()

    private val _displayedUser = MutableStateFlow<User?>(null)
    val displayedUser = _displayedUser.asStateFlow()

    var error by mutableStateOf<AppError?>(null)
        private set

    var isLoading by mutableStateOf(false)
        private set

    fun loadUser(userId: String?) {
        if (isLoading) return

        viewModelScope.launch {
            isLoading = true
            error = null

            try {
                val user = currentUser.value

                if (user != null && (userId == null || userId == user.id)) {
                    _displayedUser.value = user
                } else if (userId != null) {
                    when (val result = fetchUserUseCase(userId)) {
                        is AppResult.Success -> _displayedUser.value = result.data
                        is AppResult.Failure -> error = result.error
                    }
                } else {
                    error = AppError.Unknown()
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