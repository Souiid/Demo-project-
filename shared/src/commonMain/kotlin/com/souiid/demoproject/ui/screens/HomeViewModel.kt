package com.souiid.demoproject.ui.screens

import androidx.lifecycle.ViewModel
import com.souiid.demoproject.domain.ObserveUserNameUseCase
import com.souiid.demoproject.domain.UpdateUserNameUseCase

class HomeViewModel(
    private val updateUserNameUseCase: UpdateUserNameUseCase,
    private val observeUserNameUseCase: ObserveUserNameUseCase
): ViewModel() {

    val userName = observeUserNameUseCase()

    fun updateUserName(name: String) {
        updateUserNameUseCase(name)
    }
}