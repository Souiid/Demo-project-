package com.souiid.mutiplatformtemplate.ui.screens

import androidx.lifecycle.ViewModel
import com.souiid.mutiplatformtemplate.domain.ObserveUserNameUseCase
import com.souiid.mutiplatformtemplate.domain.UpdateUserNameUseCase

class HomeViewModel(
    private val updateUserNameUseCase: UpdateUserNameUseCase,
    private val observeUserNameUseCase: ObserveUserNameUseCase
): ViewModel() {

    val userName = observeUserNameUseCase()

    fun updateUserName(name: String) {
        updateUserNameUseCase(name)
    }
}