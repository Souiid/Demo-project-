package com.souiid.demoproject.ui.screens.app.firebase

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.souiid.demoproject.domain.usecase.auth.SignOutUseCase
import kotlinx.coroutines.launch

class FirebaseFeedViewModel(
    private val signOutUseCase: SignOutUseCase
): ViewModel() {

     fun signOut() {
         viewModelScope.launch {
             signOutUseCase()
         }
     }
}