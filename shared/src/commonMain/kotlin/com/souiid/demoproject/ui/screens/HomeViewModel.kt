package com.souiid.demoproject.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.souiid.demoproject.domain.usecase.SignOutUseCase
import com.souiid.demoproject.domain.usecase.UpdateUserNameUseCase
import kotlinx.coroutines.launch

class HomeViewModel(
    private val updateUserNameUseCase: UpdateUserNameUseCase,
    private val signOutUseCase: SignOutUseCase
): ViewModel() {

    fun updateUserName(name: String) {
        updateUserNameUseCase(name)
    }

     fun signOut() {
         viewModelScope.launch {
             signOutUseCase()
         }
     }
}