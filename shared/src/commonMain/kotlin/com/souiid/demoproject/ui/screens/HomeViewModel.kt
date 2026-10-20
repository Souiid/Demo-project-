package com.souiid.demoproject.ui.screens

import androidx.lifecycle.ViewModel
import com.souiid.demoproject.domain.usecase.UpdateUserNameUseCase

class HomeViewModel(
    private val updateUserNameUseCase: UpdateUserNameUseCase,
): ViewModel() {

    fun updateUserName(name: String) {
        updateUserNameUseCase(name)
    }
}