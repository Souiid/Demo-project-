package com.souiid.demoproject.ui.screens

import androidx.lifecycle.ViewModel
import com.souiid.demoproject.domain.ObserveAuthStateUseCase
import com.souiid.demoproject.domain.UpdateUserNameUseCase

class HomeViewModel(
    private val updateUserNameUseCase: UpdateUserNameUseCase,
): ViewModel() {

    fun updateUserName(name: String) {
        updateUserNameUseCase(name)
    }
}