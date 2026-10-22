package com.souiid.demoproject.ui.navigation.authgate

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.souiid.demoproject.domain.model.AuthState
import com.souiid.demoproject.domain.usecase.ObserveAuthStateUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

class AuthGateViewModel(
    observeAuthStateUseCase: ObserveAuthStateUseCase
) : ViewModel() {

    val authState = observeAuthStateUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AuthState.LOADING
        )
}