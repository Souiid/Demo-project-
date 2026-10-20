package com.souiid.demoproject.ui.navigation.authgate

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.souiid.demoproject.domain.model.AuthState
import com.souiid.demoproject.domain.usecase.ObserveAuthStateUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthGateViewModel(
    private val observeAuthStateUseCase: ObserveAuthStateUseCase
) : ViewModel() {

    private val _authState = MutableStateFlow(AuthState.LOADING)
    val authState = _authState.asStateFlow()

    init {
        viewModelScope.launch {
            observeAuthStateUseCase().collect { user ->
                _authState.value = if (user != null) AuthState.AUTHENTICATED else AuthState.UNAUTHENTICATED
            }
        }
    }
}